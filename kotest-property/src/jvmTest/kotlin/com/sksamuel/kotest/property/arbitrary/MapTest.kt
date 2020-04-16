package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
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
})
