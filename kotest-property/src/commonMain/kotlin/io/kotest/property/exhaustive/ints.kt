package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun Exhaustive.Companion.ints(range: IntRange): Exhaustive<Int> = range.toList().exhaustive()
fun Exhaustive.Companion.longs(range: LongRange): Exhaustive<Long> = range.toList().exhaustive()
