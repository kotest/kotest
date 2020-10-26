package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.math.BigDecimal
import java.math.RoundingMode

fun Arb.Companion.bigDecimal() = Arb.double().map { it.toBigDecimal() }

fun Arb.Companion.bigDecimal(scale: Int, roundingMode: RoundingMode) = bigDecimal().map { it.setScale(scale, roundingMode) }

fun Arb.Companion.bigDecimal(min: BigDecimal, max: BigDecimal): Arb<BigDecimal> {
   return bigDecimal().filter { it in min..max }
}

