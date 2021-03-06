package geotrellis.spark.reproject

import geotrellis.raster._
import geotrellis.raster.crop._
import geotrellis.raster.merge._
import geotrellis.raster.prototype._
import geotrellis.raster.reproject._
import geotrellis.raster.stitch._
import geotrellis.spark._
import geotrellis.spark.ingest.ProjectedExtentComponent

import org.apache.spark.rdd._

import scala.reflect.ClassTag

object Implicits extends Implicits

trait Implicits {
  implicit class withProjectedExtentReprojectMethods[K: ProjectedExtentComponent, V <: CellGrid: (? => TileReprojectMethods[V])](self: RDD[(K, V)])
      extends ProjectedExtentComponentReprojectMethods[K, V](self) { }

  implicit class withTileRDDReprojectMethods[
    K: SpatialComponent: ClassTag,
    V <: CellGrid: ClassTag: Stitcher: (? => TileReprojectMethods[V]): (? => CropMethods[V]): (? => TileMergeMethods[V]): (? => TilePrototypeMethods[V])
  ](self: RDD[(K, V)] with Metadata[RasterMetaData]) extends TileRDDReprojectMethods[K, V](self)
}
