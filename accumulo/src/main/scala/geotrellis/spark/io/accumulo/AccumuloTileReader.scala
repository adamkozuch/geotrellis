package geotrellis.spark.io.accumulo

import geotrellis.spark.LayerId
import geotrellis.spark.io._
import geotrellis.spark.io.avro.{AvroEncoder, AvroRecordCodec}
import geotrellis.spark.io.avro.codecs.KeyValueRecordCodec
import geotrellis.spark.io.index.KeyIndex

import org.apache.accumulo.core.data.{Range => ARange}
import org.apache.accumulo.core.security.Authorizations
import org.apache.avro.Schema
import org.apache.hadoop.io.Text
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.collection.JavaConversions._
import scala.reflect.ClassTag

class AccumuloTileReader[K: AvroRecordCodec: JsonFormat: ClassTag, V: AvroRecordCodec](
    instance: AccumuloInstance,
    val attributeStore: AccumuloAttributeStore)
  extends Reader[LayerId, Reader[K, V]] {

  val codec = KeyValueRecordCodec[K, V]
  val rowId = (index: Long) => new Text(long2Bytes(index))

  def read(layerId: LayerId): Reader[K, V] = new Reader[K, V] {
    val (layerMetaData, _, _, keyIndex, writerSchema) =
      attributeStore.readLayerAttributes[AccumuloLayerHeader, Unit, Unit, KeyIndex[K], Schema](layerId)

    def read(key: K): V = {
      val scanner = instance.connector.createScanner(layerMetaData.tileTable, new Authorizations())
      scanner.setRange(new ARange(rowId(keyIndex.toIndex(key))))
      scanner.fetchColumnFamily(columnFamily(layerId))

      val tiles = scanner.iterator
        .map { entry =>
          AvroEncoder.fromBinary(writerSchema, entry.getValue.get)(codec)
        }
        .flatMap { pairs: Vector[(K, V)] =>
          pairs.filter(pair => pair._1 == key)
        }
        .toVector

      if (tiles.isEmpty) {
        throw new TileNotFoundError(key, layerId)
      } else if (tiles.size > 1) {
        throw new CatalogError(s"Multiple tiles found for $key for layer $layerId")
      } else {
        tiles.head._2
      }
    }
  }
}

object AccumuloTileReader {
  def apply[K: AvroRecordCodec: JsonFormat: ClassTag, V: AvroRecordCodec: ClassTag](
      instance: AccumuloInstance): AccumuloTileReader[K, V] =
    new AccumuloTileReader[K, V](
      instance = instance,
      attributeStore = AccumuloAttributeStore(instance.connector))
}
