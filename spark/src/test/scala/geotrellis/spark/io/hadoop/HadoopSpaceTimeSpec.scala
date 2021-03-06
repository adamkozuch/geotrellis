package geotrellis.spark.io.hadoop

import geotrellis.raster.Tile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.index._
import geotrellis.spark.testfiles.TestFiles

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime

abstract class HadoopSpaceTimeSpec
  extends PersistenceSpec[SpaceTimeKey, Tile, RasterMetaData]
    with TestEnvironment
    with TestFiles
    with CoordinateSpaceTimeTests {
  lazy val reader = HadoopLayerReader[SpaceTimeKey, Tile, RasterMetaData](outputLocal)
  lazy val deleter = HadoopLayerDeleter(outputLocal)
  lazy val copier = HadoopLayerCopier[SpaceTimeKey, Tile, RasterMetaData](outputLocal)
  lazy val mover  = HadoopLayerMover[SpaceTimeKey, Tile, RasterMetaData](outputLocal)
  lazy val reindexer = HadoopLayerReindexer[SpaceTimeKey, Tile, RasterMetaData](outputLocal, ZCurveKeyIndexMethod.byPattern("YMM"))
  lazy val tiles = HadoopTileReader[SpaceTimeKey, Tile](outputLocal)
  lazy val sample =  CoordinateSpaceTime
}

class HadoopSpaceTimeZCurveByYearSpec extends HadoopSpaceTimeSpec {
  lazy val writer = HadoopLayerWriter[SpaceTimeKey, Tile, RasterMetaData](outputLocal, ZCurveKeyIndexMethod.byYear)
}

class HadoopSpaceTimeZCurveByFuncSpec extends HadoopSpaceTimeSpec {
  lazy val writer = HadoopLayerWriter[SpaceTimeKey, Tile, RasterMetaData](outputLocal, ZCurveKeyIndexMethod.by{ x =>  if (x < DateTime.now) 1 else 0 })
}

class HadoopSpaceTimeHilbertSpec extends HadoopSpaceTimeSpec {
  lazy val writer = HadoopLayerWriter[SpaceTimeKey, Tile, RasterMetaData](outputLocal, HilbertKeyIndexMethod(DateTime.now - 20.years, DateTime.now, 4))
}

class HadoopSpaceTimeHilbertWithResolutionSpec extends HadoopSpaceTimeSpec {
  lazy val writer = HadoopLayerWriter[SpaceTimeKey, Tile, RasterMetaData](outputLocal,  HilbertKeyIndexMethod(2))
}
