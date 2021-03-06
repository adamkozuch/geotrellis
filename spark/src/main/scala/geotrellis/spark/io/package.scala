package geotrellis.spark

import spray.json.JsonFormat
import scala.util.{Failure, Success, Try}

package object io extends json.Implicits with avro.codecs.Implicits {
  implicit class TryOption[T](option: Option[T]) {
    def toTry(exception: => Throwable): Try[T] =
      option match {
        case Some(t) => Success(t)
        case None    => Failure(exception)
      }
  }

  // Custom exceptions
  class CatalogError(val message: String) extends Exception(message)

  class LayerReadError(layerId: LayerId)
    extends CatalogError(s"LayerMetaData not found for layer $layerId")

  class LayerExistsError(layerId: LayerId)
    extends CatalogError(s"Layer $layerId already exists in the catalog")

  class LayerNotFoundError(layerId: LayerId)
    extends CatalogError(s"Layer $layerId not found in the catalog")

  class LayerWriteError(layerId: LayerId, message: String = "")
    extends CatalogError(s"Failed to write $layerId" + (if (message.nonEmpty) ": " + message else message))

  class LayerUpdateError(layerId: LayerId, message: String = "")
    extends CatalogError(s"Failed to update $layerId $message" + (if (message.nonEmpty) ": " + message else message))

  class LayerDeleteError(layerId: LayerId)
    extends CatalogError(s"Failed to delete $layerId")

  class LayerReindexError(layerId: LayerId)
    extends CatalogError(s"Failed to reindex $layerId")

  class LayerCopyError(from: LayerId, to: LayerId)
    extends CatalogError(s"Failed to copy $from to $to")

  class LayerMoveError(from: LayerId, to: LayerId)
    extends CatalogError(s"Failed to move $from to $to")

  class AttributeNotFoundError(attributeName: String, layerId: LayerId)
    extends CatalogError(s"Attribute $attributeName not found for layer $layerId")

  class TileNotFoundError(key: Any, layerId: LayerId)
    extends CatalogError(s"Tile with key $key not found for layer $layerId")

  class HeaderMatchError[T <: Product](layerId: LayerId, headerl: T, headerr: T)
    extends CatalogError(s"Layer $layerId Header data ($headerl) not matches ($headerr)")

  class LayerOutOfKeyBoundsError(layerId: LayerId)
    extends CatalogError(s"Updating rdd is out of $layerId bounds")

  implicit class withJsonAttributeStoreMethods(store: AttributeStore[JsonFormat])
    extends JsonAttributeStoreMethods(store)
}
