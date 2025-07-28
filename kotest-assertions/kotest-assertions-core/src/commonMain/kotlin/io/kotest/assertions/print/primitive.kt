package io.kotest.assertions.print

object NullPrint : Print<Any?> {
   override fun print(a: Any?, level: Int): Printed = Printed("${indent(level)}<null>", null)
}

object BooleanPrint : Print<Boolean> {
   override fun print(a: Boolean, level: Int): Printed = Printed("${indent(level)}$a", Boolean::class)
}

object DoublePrint : Print<Double> {
   override fun print(a: Double, level: Int): Printed = Printed("${indent(level)}$a", Double::class)
}

/**
 * Floats's are printed out as is, with the suffix f.
 */
object FloatPrint : Print<Float> {
   override fun print(a: Float, level: Int): Printed = Printed("${indent(level)}${a}f", Float::class)
}

/**
 * Long's are printed out as is, with the suffix L.
 */
object LongPrint : Print<Long> {
   override fun print(a: Long, level: Int): Printed = Printed("${indent(level)}${a}L", Long::class)
}

object IntPrint : Print<Int> {
   override fun print(a: Int, level: Int): Printed = Printed("${indent(level)}$a", Int::class)
}

object CharPrint : Print<Char> {
   override fun print(a: Char, level: Int): Printed = Printed("${indent(level)}'$a'", Char::class)
}

object ShortPrint : Print<Short> {
   override fun print(a: Short, level: Int): Printed = Printed("${indent(level)}$a", Short::class)
}

object BytePrint : Print<Byte> {
   override fun print(a: Byte, level: Int): Printed = Printed("${indent(level)}$a", Byte::class)
}

object UBytePrint : Print<UByte> {
   override fun print(a: UByte, level: Int): Printed = Printed("${indent(level)}$a (UByte)", UByte::class)
}

object UShortPrint : Print<UShort> {
   override fun print(a: UShort, level: Int): Printed = Printed("${indent(level)}$a (UShort)", UShort::class)
}

object UIntPrint : Print<UInt> {
   override fun print(a: UInt, level: Int): Printed = Printed("${indent(level)}$a (UInt)", UInt::class)
}

object ULongPrint : Print<ULong> {
   override fun print(a: ULong, level: Int): Printed = Printed("${indent(level)}$a (ULong)", ULong::class)
}

/**
 * A [Print] typeclass that uses the object's toString() method
 * to object a [Printed] result.
 */
object ToStringPrint : Print<Any> {
   override fun print(a: Any, level: Int): Printed = Printed("${indent(level)}$a")
}

object LongRangePrint : Print<LongRange> {
   override fun print(a: LongRange, level: Int): Printed = Printed("${indent(level)}$a", LongRange::class)
}

object IntRangePrint : Print<IntRange> {
   override fun print(a: IntRange, level: Int): Printed = Printed("${indent(level)}$a", IntRange::class)
}

object CharRangePrint : Print<CharRange> {
   override fun print(a: CharRange, level: Int): Printed = Printed("${indent(level)}$a", CharRange::class)
}

object ULongRangePrint : Print<ULongRange> {
   override fun print(a: ULongRange, level: Int): Printed = Printed("${indent(level)}$a", ULongRange::class)
}

object UIntRangePrint : Print<UIntRange> {
   override fun print(a: UIntRange, level: Int): Printed = Printed("${indent(level)}$a", UIntRange::class)
}
