package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.modifyEdgecasesWithRandom
import io.kotest.property.arbitrary.single
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

   test("mapping should map edgecases") {
      val intArb = Arb.int(1, 10)
         .withEdgecases(1, 2)
         .modifyEdgecasesWithRandom { initial, rs ->
            initial + Arb.int(1..10).single(rs)
         }
         .map { "value is: $it" }
      intArb.edgecases(RandomSource.seeded(1234L)) shouldBe listOf("value is: 1", "value is: 2", "value is: 10")
   }
})
