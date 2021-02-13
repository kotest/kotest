package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.EdgeCases
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.withEdgecases
import java.util.concurrent.atomic.AtomicInteger

class MapTest : FunSpec({
   test("mapping should only be invoked once") {
      val counter = AtomicInteger(0)
      val intArb = Arb.int(1, 10).map {
         counter.getAndIncrement()
      }
      intArb.values(RandomSource.Default).take(1).toList()
      counter.get().shouldBe(1)
   }

   context("edges") {
      test("mapping should map static edges") {
         val intArb = Arb.int(1, 10).withEdgecases(1, 10).map { "value is: $it" }
         intArb.edges() shouldBe EdgeCases.of("value is: 1", "value is: 10")
      }

      test("mapping should map random edges") {
         val intArb = Arb.int(1, 10).withEdgecases().map { "value is: $it" }
         intArb.edges().values(RandomSource.seeded(1234L)) shouldBe listOf("value is: 10")
      }
   }
})
