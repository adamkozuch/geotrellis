/*
 * Copyright (c) 2016 Azavea.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.spark.filter

import geotrellis.proj4.LatLng
import geotrellis.raster.{GridBounds, TileLayout, FloatConstantNoDataCellType}
import geotrellis.raster.io.geotiff.SingleBandGeoTiff
import geotrellis.spark._
import geotrellis.spark.filter._
import geotrellis.spark.io._
import geotrellis.spark.tiling._

import org.scalatest.FunSpec


class RasterRDDFilterMethodsSpec extends FunSpec with TestEnvironment {

  describe("SpaceTime RasterRDD Filter Methods") {
    val rdd = sc.parallelize(List(
      (SpaceTimeKey(0, 0, 1), true),
      (SpaceTimeKey(0, 1, 2), true),
      (SpaceTimeKey(1, 0, 2), true),
      (SpaceTimeKey(1, 1, 3), true),
      (SpaceTimeKey(0, 0, 3), true),
      (SpaceTimeKey(0, 1, 3), true),
      (SpaceTimeKey(1, 0, 4), true),
      (SpaceTimeKey(1, 1, 4), true),
      (SpaceTimeKey(0, 0, 4), true),
      (SpaceTimeKey(0, 1, 4), true)))
    val metadata: RasterMetaData = {
      val cellType = FloatConstantNoDataCellType
      val crs = LatLng
      val tileLayout = TileLayout(8, 8, 3, 4)
      val mapTransform = MapKeyTransform(crs, tileLayout.layoutDimensions)
      val gridBounds = GridBounds(1, 1, 6, 7)
      val extent = mapTransform(gridBounds)
      RasterMetaData(cellType, LayoutDefinition(crs.worldExtent, tileLayout), extent, crs)
    }
    val rasterRDD = ContextRDD(rdd, metadata)

    it("should filter out all items that are not at the given instant") {
      rasterRDD.toSpatial(0).count should be (0)
      rasterRDD.toSpatial(1).count should be (1)
      rasterRDD.toSpatial(2).count should be (2)
      rasterRDD.toSpatial(3).count should be (3)
      rasterRDD.toSpatial(4).count should be (4)
    }

    it ("should produce an RDD whose keys are of type SpatialKey") {
      rasterRDD.toSpatial(1).first._1 should be (SpatialKey(0,0))
    }
  }

  describe("Spatial RasterRDD Filter Methods") {
    val path = "raster-test/data/aspect.tif"
    val gt = SingleBandGeoTiff(path)
    val originalRaster = gt.raster.resample(500, 500)
    val (_, rdd) = createRasterRDD(originalRaster, 5, 5, gt.crs)
    val allKeys = KeyBounds(SpatialKey(0,0), SpatialKey(4,4))
    val someKeys = KeyBounds(SpatialKey(1,1), SpatialKey(3,3))
    val moreKeys = KeyBounds(SpatialKey(4,4), SpatialKey(4,4))
    val noKeys = KeyBounds(SpatialKey(5,5), SpatialKey(6,6))

    it("should correctly filter by a covering range") {
      val query = rdd.filterByKeyBounds(List(allKeys))
      query.count should be (25)
    }

    it("should correctly filter by an intersecting range") {
      val query = rdd.filterByKeyBounds(List(someKeys))
      query.count should be (9)
    }

    it("should correctly filter by an intersecting range given as a singleton") {
      val query = rdd.filterByKeyBounds(someKeys)
      query.count should be (9)
    }

    it("should correctly filter by a non-intersecting range") {
      val query = rdd.filterByKeyBounds(List(noKeys))
      query.count should be (0)
    }

    it("should correctly filter by multiple ranges") {
      val query = rdd.filterByKeyBounds(List(someKeys, moreKeys, noKeys))
      query.count should be (10)
    }
  }
}
