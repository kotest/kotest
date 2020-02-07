package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.random.Random

class SeedTest : FunSpec({
   test("seeds should result in consistent randoms") {
      checkAll(Arb.long()) { seed ->
         val random = Random(seed)
         Arb.int().sample(random) shouldBe Arb.int().sample(random)
         Arb.long().sample(random) shouldBe Arb.long().sample(random)
         Arb.string().sample(random) shouldBe Arb.string().sample(random)
         Arb.bool().sample(random) shouldBe Arb.bool().sample(random)
      }
   }
})
