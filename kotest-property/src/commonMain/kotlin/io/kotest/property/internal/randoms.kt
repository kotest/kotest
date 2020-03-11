package io.kotest.property.internal

import io.kotest.property.RandomSource
import kotlin.random.Random

internal fun Random.azchar(): Char = nextInt(from = 97, until = 123).toChar()

internal fun Random.azstring(range: IntRange): String = range.map { azchar() }.joinToString()

internal fun Long.random(): RandomSource = when (this) {
    0L -> RandomSource(Random(0), 0)
    else -> RandomSource(Random(this), this)
}