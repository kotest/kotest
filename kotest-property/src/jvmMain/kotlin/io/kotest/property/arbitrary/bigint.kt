package io.kotest.property.arbitrary

import io.kotest.property.Arb

fun Arb.Companion.bigInt(range: IntRange) = Arb.int(range).map { it.toBigInteger() }
