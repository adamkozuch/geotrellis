package geotrellis.spark.io.accumulo

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.index._
import geotrellis.spark.testfiles.TestFiles

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime

abstract class AccumuloSpaceTimeSpec
  extends PersistenceSpec[SpaceTimeKey, Tile, RasterMetaData]
    with AccumuloTestEnvironment
    with TestFiles
    with CoordinateSpaceTimeTests
    with LayerUpdateSpaceTimeTileTests {
  override val layerId = LayerId(name, 1)
  implicit val instance = MockAccumuloInstance()

  lazy val reader    = AccumuloLayerReader[SpaceTimeKey, Tile, RasterMetaData](instance)
  lazy val updater   = AccumuloLayerUpdater[SpaceTimeKey, Tile, RasterMetaData](instance, SocketWriteStrategy())
  lazy val deleter   = AccumuloLayerDeleter(instance)
  lazy val reindexer = AccumuloLayerReindexer[SpaceTimeKey, Tile, RasterMetaData](instance, "tiles", ZCurveKeyIndexMethod.byPattern("YMM"), SocketWriteStrategy())
  lazy val tiles     = AccumuloTileReader[SpaceTimeKey, Tile](instance)
  lazy val sample    = CoordinateSpaceTime
}

class AccumuloSpaceTimeZCurveByYearSpec extends AccumuloSpaceTimeSpec {
  lazy val writer = AccumuloLayerWriter[SpaceTimeKey, Tile, RasterMetaData](instance, "tiles", ZCurveKeyIndexMethod.byYear, SocketWriteStrategy())
  lazy val copier = AccumuloLayerCopier[SpaceTimeKey, Tile, RasterMetaData](instance, reader, writer)
  lazy val mover  = GenericLayerMover(copier, deleter)
}

class AccumuloSpaceTimeZCurveByFuncSpec extends AccumuloSpaceTimeSpec {
  lazy val writer = AccumuloLayerWriter[SpaceTimeKey, Tile, RasterMetaData](instance, "tiles", ZCurveKeyIndexMethod.by{ x =>  if (x < DateTime.now) 1 else 0 }, SocketWriteStrategy())
  lazy val copier = AccumuloLayerCopier[SpaceTimeKey, Tile, RasterMetaData](instance, reader, writer)
  lazy val mover  = GenericLayerMover(copier, deleter)
}

class AccumuloSpaceTimeHilbertSpec extends AccumuloSpaceTimeSpec {
  lazy val writer = AccumuloLayerWriter[SpaceTimeKey, Tile, RasterMetaData](instance, "tiles", HilbertKeyIndexMethod(DateTime.now - 20.years, DateTime.now, 4), SocketWriteStrategy())
  lazy val copier = AccumuloLayerCopier[SpaceTimeKey, Tile, RasterMetaData](instance, reader, writer)
  lazy val mover  = GenericLayerMover(copier, deleter)
}

class AccumuloSpaceTimeHilbertWithResolutionSpec extends AccumuloSpaceTimeSpec {
  lazy val writer = AccumuloLayerWriter[SpaceTimeKey, Tile, RasterMetaData](instance, "tiles",  HilbertKeyIndexMethod(2), SocketWriteStrategy())
  lazy val copier = AccumuloLayerCopier[SpaceTimeKey, Tile, RasterMetaData](instance, reader, writer)
  lazy val mover  = GenericLayerMover(copier, deleter)
}
