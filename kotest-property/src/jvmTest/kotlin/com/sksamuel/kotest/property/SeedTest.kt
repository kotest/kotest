package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.random

class SeedTest : FunSpec({

   test("fixed seeds should result in consistent randoms") {
      checkAll(Arb.long()) { seed ->
         Arb.int().sample(seed.random()).value shouldBe Arb.int().sample(seed.random()).value
         Arb.long().sample(seed.random()).value shouldBe Arb.long().sample(seed.random()).value
         Arb.string().sample(seed.random()).value shouldBe Arb.string().sample(seed.random()).value
         Arb.bool().sample(seed.random()).value shouldBe Arb.bool().sample(seed.random()).value
      }
   }
   test("should use random seed by default") {
      // allow some failures for edge cases
      checkAll<Long, Long>(config = PropTestConfig(maxFailure = 5, minSuccess = 995)) { a, b ->
         a shouldNotBe b
      }
   }
})
