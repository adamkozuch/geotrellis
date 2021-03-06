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

package geotrellis.raster.histogram

import geotrellis.raster._

import org.scalatest._
import math.abs
import scala.util.Random

class StreamingHistogramSpec extends FunSpec with Matchers {
  val r = Random
  val list1 = List(1,2,3,3,4,5,6,6,6,7,8,9,9,9,9,10,11,12,12,13,14,14,15,16,17,17,18,19)
  val list2 = List(1, 32, 243, 243, 1024, 3125, 7776, 7776, 7776, 16807, 32768, 59049, 59049, 59049)
  val list3 = List(1, 32, 243, 243, 1024, 1024, 7776, 7776, 7776, 16807, 32768, 59049, 59049,
    59049, 59049, 100000, 161051, 248832, 248832, 371293, 537824, 537824, 759375, 1048576,
    1419857, 1419857, 1889568, 2476099, 2147483647)

  describe("mode calculation") {
    it("should return None if no items are counted") {
      val h = StreamingHistogram()
      h.mode should be (None)
    }

    it("should return the same result for mode and statistics.mode") {
      val h = StreamingHistogram()

      list3.foreach({i => h.countItem(i) })

      val mode = h.mode.get
      mode should equal (59049)
      mode should equal (h.statistics.get.mode)
    }

    it(".mode and .statistics.mode should agree on a mode of a unique list") {
      val h = StreamingHistogram()
      val list = List(9, 8, 7, 6, 5, 4, 3, 2, -10)
      for(i <- list) {
        h.countItem(i)
      }

      val mode = h.mode.get
      mode should equal (h.statistics.get.mode)
    }
  }

  describe("median calculations") {
    it("should return the same result for median and statistics.median") {
      val h = StreamingHistogram()

      list1.foreach({ i => h.countItem(i) })

      h.median.get should equal (8.75)
      h.median.get should equal (h.statistics.get.median)
    }

    it("median should work when n is large with repeated elements") {
      val h = StreamingHistogram()

      Iterator.continually(list1)
        .flatten.take(list1.length * 10000)
        .foreach({ i => h.countItem(i) })

      h.median.get should equal (8.75)
      h.median.get should equal (h.statistics.get.median)
    }

    it("median should work when n is large with unique elements") {
      val h = StreamingHistogram()

      /* Here  the list of values  is used repeatedly, but  with small
       * perturbations to  make the values  unique (to make  sure that
       * the maximum number of buckets  is exceeded so that the median
       * can be tested under  those circumstances).  The perturbations
       * should be  positive numbers  with magnitude somewhere  in the
       * neighborhood of 1e-4.*/
      Iterator.continually(list1)
        .flatten.take(list1.length * 10000)
        .foreach({ i => h.countItem(i + (3.0 + r.nextGaussian) / 60000.0) })

      math.round(h.median.get).toInt should equal (9)
      h.median.get should equal (h.statistics.get.median)
    }
  }

  describe("mean calculation") {
    it("should return the same result for mean and statistics.mean") {
      val h = StreamingHistogram()

      list2.foreach({ i => h.countItem(i) })

      val mean = h.mean.get
      abs(mean - 18194.14285714286) should be < 1e-7
      mean should equal (h.statistics.get.mean)
    }

    it("mean should work when n is large with repeated elements") {
      val h = StreamingHistogram()

      Iterator.continually(list2)
        .flatten.take(list2.length * 10000)
        .foreach({ i => h.countItem(i) })

      val mean = h.mean.get
      abs(mean - 18194.14285714286) should be < 1e-7
      mean should equal (h.statistics.get.mean)
    }

    it("mean should work when n is large with unique elements") {
      val h = StreamingHistogram()

      /* The  list of values is  used repeatedly here, but  with small
       * perturbations.   The motivation  for  those  is similar  that
       * stated above for the median case.  The difference of the mean
       * of the perturbed list and the mean of the unperturbed list is
       * a random variable with mean zero and standard deviation 1e-6,
       * so this test "should never fail" unless the histogram code is
       * faulty. */
      Iterator.continually(list2)
        .flatten.take(list2.length * 10000)
        .foreach({ i => h.countItem(i + r.nextGaussian / 10000.0) })

      val mean = h.mean.get
      abs(mean - 18194.14285714286) should be < 1e-4
      mean should equal (h.statistics.get.mean)
    }
  }

}
