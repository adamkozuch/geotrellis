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

package geotrellis.raster.rasterize

import geotrellis.raster._
import geotrellis.raster.rasterize.Rasterize.Options
import geotrellis.util.MethodExtensions
import geotrellis.vector.{Geometry,Feature}


trait FeatureIntRasterizeMethods[+G <: Geometry, T <: Feature[G,Int]] extends MethodExtensions[T] {

  def foreach(
    re : RasterExtent,
    options: Options = Options.DEFAULT
  )(fn: (Int, Int) => Unit): Unit =
    self.geom.foreach(re, options)(fn)

  def rasterize(
    re : RasterExtent,
    options: Options = Options.DEFAULT
  )(fn: (Int, Int) => Int): Raster[ArrayTile] =
    self.geom.rasterize(re, options)(fn)

  def rasterizeDouble(
    re : RasterExtent,
    options: Options = Options.DEFAULT
  )(fn: (Int, Int) => Double): Raster[ArrayTile] =
    self.geom.rasterizeDouble(re, options)(fn)

  def rasterize(re: RasterExtent, value: Int): Raster[ArrayTile] =
    self.geom.rasterize(re, value)

  def rasterizeDouble(re: RasterExtent, value: Double): Tile =
    self.geom.rasterizeDouble(re, value)

  def rasterize(re: RasterExtent): Raster[ArrayTile] =
    self.geom.rasterize(re, self.data)

  def rasterizeDouble(re: RasterExtent): Raster[ArrayTile] =
    self.geom.rasterizeDouble(re, self.data.toDouble)
}

trait FeatureDoubleRasterizeMethods[+G <: Geometry, T <: Feature[G,Double]] extends MethodExtensions[T] {

  def foreach(
    re : RasterExtent,
    options: Options = Options.DEFAULT
  )(fn: (Int, Int) => Unit): Unit =
    self.geom.foreach(re, options)(fn)

  def rasterize(
    re : RasterExtent,
    options: Options = Options.DEFAULT
  )(fn: (Int, Int) => Int): Raster[ArrayTile] =
    self.geom.rasterize(re, options)(fn)

  def rasterizeDouble(
    re : RasterExtent,
    options: Options = Options.DEFAULT
  )(fn: (Int, Int) => Double): Raster[ArrayTile] =
    self.geom.rasterizeDouble(re, options)(fn)

  def rasterize(re: RasterExtent, value: Int): Raster[ArrayTile] =
    self.geom.rasterize(re, value)

  def rasterizeDouble(re: RasterExtent, value: Double): Raster[ArrayTile] =
    self.geom.rasterizeDouble(re, value)

  def rasterize(re: RasterExtent): Raster[ArrayTile] =
    self.geom.rasterize(re, self.data.toInt)

  def rasterizeDouble(re: RasterExtent): Raster[ArrayTile] =
    self.geom.rasterizeDouble(re, self.data)
}
