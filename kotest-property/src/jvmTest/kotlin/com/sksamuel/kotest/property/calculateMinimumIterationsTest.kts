package com.sksamuel.kotest.property

import io.kotest.core.script.describe
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.calculateMinimumIterations
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.ints

describe("calculateMinimumIterations") {

   it("should calculate cartesian product when all gens are exhaustives") {
      calculateMinimumIterations(
         Exhaustive.ints(0..10), // 11 values
         Exhaustive.ints(0..1), // 2 values
         Exhaustive.boolean(), // 2 values
      ) shouldBe 44
   }

   it("should calculate min for arbs") {
      calculateMinimumIterations(
         Arb.string(1).withEdgecases(listOf("a", "b")),
         Arb.string(1)
      ) shouldBe 2
   }

   it("should calculate min for a mix of exhaustives and arbs") {
      calculateMinimumIterations(
         Exhaustive.ints(0..10), // 11 values
         Arb.string(1) // edgecase is the empty string
      ) shouldBe 11
   }
}
