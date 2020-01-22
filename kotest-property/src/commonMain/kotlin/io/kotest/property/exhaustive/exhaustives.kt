package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

/**
 * Returns an [Exhaustive] that returns the values in the given [IntRange].
 */
fun Exhaustive.Companion.int(range: IntRange) = object : Exhaustive<Int> {
   override fun values(): Sequence<Int> = range.asSequence()
}

/**
 * Returns an [Exhaustive] that returns the values in the given [LongRange].
 */
fun Exhaustive.Companion.long(range: LongRange) = object : Exhaustive<Long> {
   override fun values(): Sequence<Long> = range.asSequence()
}

/**
 * Returns an [Exhaustive] that returns the AZ* strings with lengths from range.start to range.end.
 * Eg, azstring(2..3) will return all 2 and 3 character strings with the letters a..z.
 */
fun Exhaustive.Companion.azstring(range: IntRange) = object : Exhaustive<String> {
   private fun az() = ('a'..'z').asSequence().map { it.toString() }
   override fun values(): Sequence<String> = range.asSequence().flatMap { size ->
      List(size) { az() }.reduce { acc, seq -> acc.zip(seq).map { (a, b) -> a + b } }
   }
}

/**
 * Returns a [Exhaustive] whose value is a single constant.
 */
fun <T> Exhaustive.Companion.constant(constant: T) = object : Exhaustive<T> {
   override fun values(): Sequence<T> = sequenceOf(constant)
}

/**
 * Returns a [Exhaustive] of the two possible boolean values - true and false.
 */
fun Exhaustive.Companion.bools() = object : Exhaustive<Boolean> {
   override fun values(): Sequence<Boolean> = sequenceOf(true, false)
}

/**
 * Returns a [Exhaustive] of bytes from [Byte.MIN_VALUE] to [Byte.MAX_VALUE].
 */
fun Exhaustive.Companion.byte() = object : Exhaustive<Byte> {
   override fun values(): Sequence<Byte> = (Byte.MIN_VALUE..Byte.MAX_VALUE).asSequence().map { it.toByte() }
}
