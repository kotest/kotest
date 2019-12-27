package io.kotest.property

fun Progression.Companion.int(range: IntRange) = object : Progression<Int> {
   override fun values(): Sequence<Int> = range.asSequence()
}

fun Progression.Companion.long(range: LongRange) = object : Progression<Long> {
   override fun values(): Sequence<Long> = range.asSequence()
}

fun Progression.Companion.azstring(range: IntRange) = object : Progression<String> {
   private fun az() = ('a'..'z').asSequence().map { it.toString() }
   override fun values(): Sequence<String> = range.asSequence().flatMap { size ->
      List(size) { az() }.reduce { acc, seq -> acc.zip(seq).map { (a, b) -> a + b } }
   }
}

/**
 * Returns a [Progression] of the two possible boolean values - true and false.
 */
fun Progression.Companion.bools() = object : Progression<Boolean> {
   override fun values(): Sequence<Boolean> = sequenceOf(true, false)
}

/**
 * Returns a [Progression] of bytes from [Byte.MIN_VALUE] to [Byte.MAX_VALUE].
 */
fun Progression.Companion.byte() = object : Progression<Byte> {
   override fun values(): Sequence<Byte> = (Byte.MIN_VALUE..Byte.MAX_VALUE).asSequence().map { it.toByte() }
}
