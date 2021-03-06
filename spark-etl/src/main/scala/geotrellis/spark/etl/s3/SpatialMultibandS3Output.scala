package geotrellis.spark.etl.s3

import geotrellis.raster.MultiBandTile
import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.index.KeyIndexMethod
import geotrellis.spark.io.s3.S3LayerWriter

class SpatialMultibandS3Output extends S3Output[SpatialKey, MultiBandTile, RasterMetaData] {
  def writer(method: KeyIndexMethod[SpatialKey], props: Parameters) =
    S3LayerWriter[SpatialKey, MultiBandTile, RasterMetaData](props("bucket"), props("key"), method)
}
