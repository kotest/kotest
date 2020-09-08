package io.kotest.property.arbitrary

import io.kotest.property.Arb

fun Arb.Companion.bool() = arbitrary(listOf(true, false)) { it.random.nextBoolean() }
