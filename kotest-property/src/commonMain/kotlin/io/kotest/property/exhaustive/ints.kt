package io.kotest.property.exhaustive

fun Exhaustive.Companion.ints(range: IntRange) = object : Exhaustive<Int> {
   override val values = range.toList()
}

fun Exhaustive.Companion.longs(range: LongRange) = object : Exhaustive<Long> {
   override val values = range.toList()
}

