package io.kotest.property.arbitrary

fun Arb.Companion.bigInt(range: IntRange) = Arb.int(range).map { it.toBigInteger() }
