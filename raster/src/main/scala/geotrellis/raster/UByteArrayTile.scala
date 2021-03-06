package geotrellis.raster

import geotrellis.vector.Extent

import spire.syntax.cfor._
import java.nio.ByteBuffer

/**
 * ArrayTile based on Array[Byte] (each cell as a Byte).
 */
abstract class UByteArrayTile(val array: Array[Byte], cols: Int, rows: Int)
    extends MutableArrayTile with IntBasedArrayTile {
  val cellType: UByteCells with NoDataHandling
  def apply(i: Int): Int
  def update(i: Int, z: Int)

  def toBytes: Array[Byte] = array.clone
  def copy = UByteArrayTile(array.clone, cols, rows, cellType)
}

final case class UByteRawArrayTile(arr: Array[Byte], val cols: Int, val rows: Int)
    extends UByteArrayTile(arr, cols, rows) {
  val cellType = UByteCellType
  def apply(i: Int): Int = arr(i) & 0xFF
  def update(i: Int, z: Int) { arr(i) = z.toByte }
}

final case class UByteConstantNoDataArrayTile(arr: Array[Byte], val cols: Int, val rows: Int)
    extends UByteArrayTile(arr, cols, rows) {
  val cellType = UByteConstantNoDataCellType
  def apply(i: Int): Int = ub2i(arr(i))
  def update(i: Int, z: Int) { arr(i) = i2ub(z) }
}

final case class UByteUserDefinedNoDataArrayTile(arr: Array[Byte], val cols: Int, val rows: Int, val cellType: UByteUserDefinedNoDataCellType)
    extends UByteArrayTile(arr, cols, rows)
       with UserDefinedByteNoDataConversions {
  val userDefinedByteNoDataValue = cellType.noDataValue
  def apply(i: Int): Int = udub2i(arr(i))
  def update(i: Int, z: Int) { arr(i) = i2udb(z) }
}

object UByteArrayTile {
  def apply(arr: Array[Byte], cols: Int, rows: Int): UByteArrayTile =
    apply(arr, cols, rows, UByteConstantNoDataCellType)

  def apply(arr: Array[Byte], cols: Int, rows: Int, cellType: UByteCells with NoDataHandling): UByteArrayTile =
    cellType match {
      case UByteCellType =>
        new UByteRawArrayTile(arr, cols, rows)
      case UByteConstantNoDataCellType =>
        new UByteConstantNoDataArrayTile(arr, cols, rows)
      case udct @ UByteUserDefinedNoDataCellType(_) =>
        new UByteUserDefinedNoDataArrayTile(arr, cols, rows, udct)
    }

  def ofDim(cols: Int, rows: Int): UByteArrayTile =
    ofDim(cols, rows, UByteConstantNoDataCellType)

  def ofDim(cols: Int, rows: Int, cellType: UByteCells with NoDataHandling): UByteArrayTile =  cellType match {
    case UByteCellType =>
      new UByteRawArrayTile(Array.ofDim[Byte](cols * rows), cols, rows)
    case UByteConstantNoDataCellType =>
      new UByteConstantNoDataArrayTile(Array.ofDim[Byte](cols * rows), cols, rows)
    case udct @ UByteUserDefinedNoDataCellType(_) =>
      new UByteUserDefinedNoDataArrayTile(Array.ofDim[Byte](cols * rows), cols, rows, udct)
  }

  def empty(cols: Int, rows: Int): UByteArrayTile =
    empty(cols, rows, UByteConstantNoDataCellType)

  def empty(cols: Int, rows: Int, cellType: UByteCells with NoDataHandling): UByteArrayTile = cellType match {
    case UByteCellType =>
      new UByteRawArrayTile(Array.ofDim[Byte](cols * rows).fill(ubyteNODATA), cols, rows)
    case UByteConstantNoDataCellType =>
      new UByteConstantNoDataArrayTile(Array.ofDim[Byte](cols * rows).fill(ubyteNODATA), cols, rows)
    case udct @ UByteUserDefinedNoDataCellType(nd) =>
      new UByteUserDefinedNoDataArrayTile(Array.ofDim[Byte](cols * rows).fill(nd), cols, rows, udct)
  }

  def fill(v: Byte, cols: Int, rows: Int): UByteArrayTile =
    fill(v, cols, rows, UByteConstantNoDataCellType)

  def fill(v: Byte, cols: Int, rows: Int, cellType: UByteCells with NoDataHandling): UByteArrayTile = cellType match {
    case UByteCellType =>
      new UByteRawArrayTile(Array.ofDim[Byte](cols * rows).fill(v), cols, rows)
    case UByteConstantNoDataCellType =>
      new UByteConstantNoDataArrayTile(Array.ofDim[Byte](cols * rows).fill(v), cols, rows)
    case udct @ UByteUserDefinedNoDataCellType(_) =>
      new UByteUserDefinedNoDataArrayTile(Array.ofDim[Byte](cols * rows).fill(v), cols, rows, udct)
  }

  def fromBytes(bytes: Array[Byte], cols: Int, rows: Int): UByteArrayTile =
    fromBytes(bytes, cols, rows, UByteConstantNoDataCellType)

  def fromBytes(bytes: Array[Byte], cols: Int, rows: Int, cellType: UByteCells with NoDataHandling): UByteArrayTile =
    cellType match {
      case UByteCellType =>
        new UByteRawArrayTile(bytes.clone, cols, rows)
      case UByteConstantNoDataCellType =>
        new UByteConstantNoDataArrayTile(bytes.clone, cols, rows)
      case udct @ UByteUserDefinedNoDataCellType(_) =>
        new UByteUserDefinedNoDataArrayTile(bytes.clone, cols, rows, udct)
    }
}
