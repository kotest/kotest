package io.kotest.property.exhaustive

fun Exhaustive.Companion.ints(range: IntRange) = object : Exhaustive<Int> {
   override fun values() = range.asSequence()
}

fun Exhaustive.Companion.longs(range: LongRange) = object : Exhaustive<Long> {
   override fun values() = range.asSequence()
}

