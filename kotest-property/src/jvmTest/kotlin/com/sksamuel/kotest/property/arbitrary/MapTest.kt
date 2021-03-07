package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.EdgeConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.IntShrinker
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.withEdgecases
import java.util.concurrent.atomic.AtomicInteger

class MapTest : FunSpec({
   test("mapping should only be invoked once") {
      val counter = AtomicInteger(0)
      val intArb = Arb.int(1, 10).map {
         counter.getAndIncrement()
      }
      intArb.single()
      counter.get().shouldBe(1)
   }

   test("should transform edgecases") {
      val arb = Arb.int(1, 10).withEdgecases(1).map { "$it" }
      arb.generate(RandomSource.Default, EdgeConfig(edgecasesGenerationProbability = 1.0)).first().value shouldBe "1"
   }

   test("should preserve shrinking") {
      val arb = arbitrary(IntShrinker(1..1000)) { rs -> Arb.int(1..1000).single(rs) }
      val sample = arb.map { it.toString() }.sample(RandomSource.seeded(1234L))
      val shrinks = sample.shrinks.children.value.map { it.value() } + sample.shrinks.value()
      sample.value shouldBe "120"
      shrinks shouldContainExactly listOf(
         "1",
         "40",
         "60",
         "80",
         "115",
         "116",
         "117",
         "118",
         "119",
         "120"
      )
   }
})
