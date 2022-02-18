package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.math.BigDecimal
import java.math.RoundingMode

internal val bigDecimalDefaultEdgecases = listOf(
   BigDecimal(0.0),
   // BigDecimal compareTo and equals are not consistent
   BigDecimal("0.00"),
   BigDecimal(1.0),
   BigDecimal(-1.0),
   BigDecimal("1e-300"),
   BigDecimal("-1e-300"),
)

fun Arb.Companion.bigDecimal(): Arb<BigDecimal> {
   return arbitrary(bigDecimalDefaultEdgecases) {
      if (it.random.nextInt() % 2 == 0) {
         BigDecimal(it.random.nextLong()) * BigDecimal(it.random.nextDouble())
      } else {
         BigDecimal(it.random.nextInt()) * BigDecimal(it.random.nextDouble())
      }
   }
}

fun Arb.Companion.bigDecimal(scale: Int, roundingMode: RoundingMode) =
   bigDecimal().map { it.setScale(scale, roundingMode) }

fun Arb.Companion.bigDecimal(min: BigDecimal, max: BigDecimal): Arb<BigDecimal> {
   val boundedEdgecases = bigDecimalDefaultEdgecases
      .filter { min <= it && it < max }
      .plus(min)

   return arbitrary(boundedEdgecases) {
      min.add(BigDecimal(Math.random()).multiply(max.subtract(min)))
   }
}
