package io.kotest.assertions.print

object NullPrint : Print<Any?> {
   override fun print(a: Any?, level: Int): Printed = "${indent(level)}<null>".printed()
}

object BooleanPrint : Print<Boolean> {
   override fun print(a: Boolean, level: Int): Printed = "${indent(level)}$a".printed()
}

object DoublePrint : Print<Double> {
   override fun print(a: Double, level: Int): Printed = "${indent(level)}$a".printed()
}

/**
 * Floats's are printed out as is, with the suffix f.
 */
object FloatPrint : Print<Float> {
   override fun print(a: Float, level: Int): Printed = "${indent(level)}${a}f".printed()
}

/**
 * Long's are printed out as is, with the suffix L.
 */
object LongPrint : Print<Long> {
   override fun print(a: Long, level: Int): Printed = "${indent(level)}${a}L".printed()
}

object IntPrint : Print<Int> {
   override fun print(a: Int, level: Int): Printed = "${indent(level)}$a".printed()
}

object CharPrint : Print<Char> {
   override fun print(a: Char, level: Int): Printed = "${indent(level)}'$a'".printed()
}

object ShortPrint : Print<Short> {
   override fun print(a: Short, level: Int): Printed = "${indent(level)}$a".printed()
}

object BytePrint : Print<Byte> {
   override fun print(a: Byte, level: Int): Printed = "${indent(level)}$a".printed()
}

object UBytePrint : Print<UByte> {
   override fun print(a: UByte, level: Int): Printed = "${indent(level)}$a (UByte)".printed()
}

object UShortPrint : Print<UShort> {
   override fun print(a: UShort, level: Int): Printed = "${indent(level)}$a (UShort)".printed()
}

object UIntPrint : Print<UInt> {
   override fun print(a: UInt, level: Int): Printed = "${indent(level)}$a (UInt)".printed()
}

object ULongPrint : Print<ULong> {
   override fun print(a: ULong, level: Int): Printed = "${indent(level)}$a (ULong)".printed()
}

/**
 * A [Print] typeclass that uses the object's toString() method
 * to object a [Printed] result.
 */
object ToStringPrint : Print<Any> {
   override fun print(a: Any, level: Int): Printed = "${indent(level)}$a".printed()
}

object LongRangePrint : Print<LongRange> {
   override fun print(a: LongRange, level: Int): Printed = "${indent(level)}$a".printed()
}

object IntRangePrint : Print<IntRange> {
   override fun print(a: IntRange, level: Int): Printed = "${indent(level)}$a".printed()
}

object CharRangePrint : Print<CharRange> {
   override fun print(a: CharRange, level: Int): Printed = "${indent(level)}$a".printed()
}

object ULongRangePrint : Print<ULongRange> {
   override fun print(a: ULongRange, level: Int): Printed = "${indent(level)}$a".printed()
}

object UIntRangePrint : Print<UIntRange> {
   override fun print(a: UIntRange, level: Int): Printed = "${indent(level)}$a".printed()
}
