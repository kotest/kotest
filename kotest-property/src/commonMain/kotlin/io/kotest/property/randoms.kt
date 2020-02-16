package io.kotest.property

import kotlin.random.Random

fun Random.azchar(): Char = nextInt(from = 97, until = 123).toChar()

fun Random.azstring(range: IntRange): String = range.map { azchar() }.joinToString()
