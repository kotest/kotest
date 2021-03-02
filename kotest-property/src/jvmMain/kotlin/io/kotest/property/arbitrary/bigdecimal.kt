package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.math.BigDecimal
import java.math.RoundingMode

private val bigDecimalEdgecases = listOf(
   BigDecimal(0.0),
   BigDecimal(1.0),
   BigDecimal(-1.0),
   BigDecimal("1e-300"),
   BigDecimal("-1e-300"),
)

fun Arb.Companion.bigDecimal(): Arb<BigDecimal> {
   return arbitrary(bigDecimalEdgecases) {
      BigDecimal(it.random.nextInt() * it.random.nextDouble())
   }
}

fun Arb.Companion.bigDecimal(scale: Int, roundingMode: RoundingMode) =
   bigDecimal().map { it.setScale(scale, roundingMode) }

fun Arb.Companion.bigDecimal(min: BigDecimal, max: BigDecimal): Arb<BigDecimal> {
   return bigDecimal().filter { it in min..max }
}

