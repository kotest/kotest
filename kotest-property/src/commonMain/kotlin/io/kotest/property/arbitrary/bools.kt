package io.kotest.property.arbitrary

import io.kotest.property.Arb

fun Arb.Companion.bool() = Arb.create(listOf(true, false)) { it.random.nextBoolean() }
