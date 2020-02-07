package io.kotest.property

import kotlin.random.Random

fun Long.random(): RandomSource = when (this) {
   0L -> RandomSource(Random.Default, 0)
   else -> RandomSource(Random(this), this)
}
