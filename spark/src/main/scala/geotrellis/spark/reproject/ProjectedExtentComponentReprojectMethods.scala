package geotrellis.spark.reproject

import geotrellis.raster._
import geotrellis.raster.reproject._
import geotrellis.spark._
import geotrellis.spark.ingest._
import geotrellis.vector._
import geotrellis.proj4._
import geotrellis.util.MethodExtensions

import org.apache.spark.rdd._


class ProjectedExtentComponentReprojectMethods[K: ProjectedExtentComponent, V <: CellGrid: (? => TileReprojectMethods[V])](val self: RDD[(K, V)])
    extends MethodExtensions[RDD[(K, V)]] {
  import geotrellis.raster.reproject.Reproject.Options

  def reproject(destCrs: CRS, options: Options): RDD[(K, V)] = {
    ProjectedExtentComponentReproject(self, destCrs, options)
  }

  def reproject(destCrs: CRS): RDD[(K, V)] =
    reproject(destCrs, Options.DEFAULT)
}
