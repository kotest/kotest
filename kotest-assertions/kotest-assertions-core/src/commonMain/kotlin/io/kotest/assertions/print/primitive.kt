package io.kotest.assertions.print

object NullPrint : Print<Any?> {
   override fun print(a: Any?): Printed = Printed("<null>", null)
}

object BooleanPrint : Print<Boolean> {
   override fun print(a: Boolean): Printed = Printed("$a", Boolean::class)
}

object DoublePrint : Print<Double> {
   override fun print(a: Double): Printed = Printed("$a", Double::class)
}

/**
 * Floats's are printed out as is, with the suffix f.
 */
object FloatPrint : Print<Float> {
   override fun print(a: Float): Printed = Printed("${a}f", Float::class)
}

/**
 * Long's are printed out as is, with the suffix L.
 */
object LongPrint : Print<Long> {
   override fun print(a: Long): Printed = Printed("${a}L", Long::class)
}

object IntPrint : Print<Int> {
   override fun print(a: Int): Printed = Printed("$a", Int::class)
}

object CharPrint : Print<Char> {
   override fun print(a: Char): Printed = Printed("'$a'", Char::class)
}

object ShortPrint : Print<Short> {
   override fun print(a: Short): Printed = Printed("$a", Short::class)
}

object BytePrint : Print<Byte> {
   override fun print(a: Byte): Printed = Printed("$a", Byte::class)
}

object UBytePrint : Print<UByte> {
   override fun print(a: UByte): Printed = Printed("$a (UByte)", UByte::class)
}

object UShortPrint : Print<UShort> {
   override fun print(a: UShort): Printed = Printed("$a (UShort)", UShort::class)
}

object UIntPrint : Print<UInt> {
   override fun print(a: UInt): Printed = Printed("$a (UInt)", UInt::class)
}

object ULongPrint : Print<ULong> {
   override fun print(a: ULong): Printed = Printed("$a (ULong)", ULong::class)
}

/**
 * A [Print] typeclass that uses the object's toString() method
 * to object a [Printed] result.
 */
object ToStringPrint : Print<Any> {
   override fun print(a: Any): Printed = Printed("$a")
}

object LongRangePrint : Print<LongRange> {
   override fun print(a: LongRange): Printed = Printed("$a", LongRange::class)
}

object IntRangePrint : Print<IntRange> {
   override fun print(a: IntRange): Printed = Printed("$a", IntRange::class)
}

object CharRangePrint : Print<CharRange> {
   override fun print(a: CharRange): Printed = Printed("$a", CharRange::class)
}

object ULongRangePrint : Print<ULongRange> {
   override fun print(a: ULongRange): Printed = Printed("$a", ULongRange::class)
}

object UIntRangePrint : Print<UIntRange> {
   override fun print(a: UIntRange): Printed = Printed("$a", UIntRange::class)
}
