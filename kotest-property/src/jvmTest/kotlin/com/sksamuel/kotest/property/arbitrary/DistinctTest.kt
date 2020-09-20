package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.property.Arb
import io.kotest.property.arbitrary.distinct
import io.kotest.property.arbitrary.frequency
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.withEdgecases

class DistinctTest : FunSpec() {
   init {
      context("arb.distinct()") {
         test("should resample an input distribution into uniform random").config(invocations = 16, threads = 4) {
            val randomElements = imbalancedArb.distinct().take(10000).toList()

            val probabilityDistribution = randomElements
               .groupBy { it }
               .mapValues { it.value.size.toDouble() / randomElements.size }

            probabilityDistribution.forEach {
               it.value.shouldBeBetween(0.125, 0.125, 0.05)
            }
         }
      }
   }

   companion object {
      private val imbalancedArb = Arb
         .frequency(
            1 to 1,
            2 to 2,
            3 to 3,
            4 to 4,
            5 to 5,
            6 to 6,
            7 to 7,
            8 to 8
         )
         .withEdgecases(1, 2, 3, 4, 5, 6, 7, 8)
   }
}
