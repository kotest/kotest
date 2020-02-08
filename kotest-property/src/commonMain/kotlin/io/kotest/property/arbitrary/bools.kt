package io.kotest.property.arbitrary

fun Arb.Companion.bool() = arb(listOf(true, false)) { it.random.nextBoolean() }
