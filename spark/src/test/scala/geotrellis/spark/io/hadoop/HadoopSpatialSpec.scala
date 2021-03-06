package geotrellis.spark.io.hadoop

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.index._
import geotrellis.spark.testfiles.TestFiles

abstract class HadoopSpatialSpec
  extends PersistenceSpec[SpatialKey, Tile, RasterMetaData]
    with TestEnvironment
    with TestFiles
    with AllOnesTestTileTests {

  lazy val reader = HadoopLayerReader[SpatialKey, Tile, RasterMetaData](outputLocal)
  lazy val deleter = HadoopLayerDeleter(outputLocal)
  lazy val copier = HadoopLayerCopier[SpatialKey, Tile, RasterMetaData](outputLocal)
  lazy val mover  = HadoopLayerMover[SpatialKey, Tile, RasterMetaData](outputLocal)
  lazy val reindexer = HadoopLayerReindexer[SpatialKey, Tile, RasterMetaData](outputLocal, ZCurveKeyIndexMethod)
  lazy val tiles = HadoopTileReader[SpatialKey, Tile](outputLocal)
  lazy val sample = AllOnesTestFile
}

class HadoopSpatialRowMajorSpec extends HadoopSpatialSpec {
  lazy val writer = HadoopLayerWriter[SpatialKey, Tile, RasterMetaData](outputLocal, RowMajorKeyIndexMethod)
}

class HadoopSpatialZCurveSpec extends HadoopSpatialSpec {
  lazy val writer = HadoopLayerWriter[SpatialKey, Tile, RasterMetaData](outputLocal, ZCurveKeyIndexMethod)
}

class HadoopSpatialHilbertSpec extends HadoopSpatialSpec {
  lazy val writer = HadoopLayerWriter[SpatialKey, Tile, RasterMetaData](outputLocal, HilbertKeyIndexMethod)
}


