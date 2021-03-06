package geotrellis.spark.etl.hadoop

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.hadoop.HadoopLayerWriter
import geotrellis.spark.io.index.KeyIndexMethod

import org.apache.hadoop.fs.Path

class SpatialHadoopOutput extends HadoopOutput[SpatialKey, Tile, RasterMetaData] {
  def writer(method: KeyIndexMethod[SpatialKey], props: Parameters) =
    HadoopLayerWriter[SpatialKey, Tile, RasterMetaData](new Path(props("path")), method)
}
