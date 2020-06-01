package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.PropertyTesting.computeDefaultIteration
import io.kotest.property.arbitrary.string
import io.kotest.property.exhaustive.exhaustive

class ConfigTest : FunSpec({

   test("Calculate default iterations arb") {
      computeDefaultIteration(Arb.string()) shouldBe PropertyTesting.defaultIterationCount
   }

   test("Calculate default iterations exhaustive") {
      computeDefaultIteration(bigExhaustive) shouldBe 10_000
      computeDefaultIteration(mediumExhaustive) shouldBe 5_000
      computeDefaultIteration(bigExhaustive, mediumExhaustive, Arb.string()) shouldBe 10_000
   }
})

private val bigExhaustive = exhaustive(List(10_000) { it })
private val mediumExhaustive = exhaustive(List(5_000) { it })
