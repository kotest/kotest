package io.kotest.assertions.print

object NullPrint : Print<Any?> {
   override fun print(a: Any?): Printed = Printed("<null>")
}

object BooleanPrint : Print<Boolean> {
   override fun print(a: Boolean): Printed = "$a".printed()
}

object DoublePrint : Print<Double> {
   override fun print(a: Double): Printed = a.toString().printed()
}

/**
 * Floats's are printed out as is, with the suffix f.
 */
object FloatPrint : Print<Float> {
   override fun print(a: Float): Printed = "${a}f".printed()
}

/**
 * Long's are printed out as is, with the suffix L.
 */
object LongPrint : Print<Long> {
   override fun print(a: Long): Printed = "${a}L".printed()
}

object IntPrint : Print<Int> {
   override fun print(a: Int): Printed = a.toString().printed()
}

object CharPrint : Print<Char> {
   override fun print(a: Char): Printed = "'$a'".printed()
}

object ShortPrint : Print<Short> {
   override fun print(a: Short): Printed = a.toString().printed()
}

object BytePrint : Print<Byte> {
   override fun print(a: Byte): Printed = a.toString().printed()
}

object UBytePrint: Print<UByte> {
   override fun print(a: UByte): Printed = "$a (UByte)".printed()
}

object UShortPrint: Print<UShort> {
   override fun print(a: UShort): Printed = "$a (UShort)".printed()
}

object UIntPrint: Print<UInt> {
   override fun print(a: UInt): Printed = "$a (UInt)".printed()
}

object ULongPrint: Print<ULong> {
   override fun print(a: ULong): Printed = "$a (ULong)".printed()
}

/**
 * A [Print] typeclass that uses the object's toString() method
 * to object a [Printed] result.
 */
object ToStringPrint : Print<Any> {
   override fun print(a: Any, level: Int): Printed = a.toString().printed()
   override fun print(a: Any): Printed = a.toString().printed()
}

object LongRangePrint : Print<LongRange> {
   override fun print(a: LongRange): Printed = Printed(a.toString())
}

object IntRangePrint : Print<IntRange> {
   override fun print(a: IntRange): Printed = Printed(a.toString())
}

object CharRangePrint : Print<CharRange> {
   override fun print(a: CharRange): Printed = Printed(a.toString())
}

object ULongRangePrint : Print<ULongRange> {
   override fun print(a: ULongRange): Printed = Printed(a.toString())
}

object UIntRangePrint : Print<UIntRange> {
   override fun print(a: UIntRange): Printed = Printed(a.toString())
}
