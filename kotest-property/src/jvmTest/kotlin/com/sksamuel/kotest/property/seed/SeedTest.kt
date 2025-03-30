package com.sksamuel.kotest.property.seed

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.forAll
import io.kotest.property.random

@EnabledIf(LinuxOnlyGithubCondition::class)
class SeedTest : FunSpec({

   test("fixed seeds should result in consistent randoms") {
      checkAll(Arb.long()) { seed ->
         Arb.int().single(seed.random()) shouldBe Arb.int().single(seed.random())
         Arb.long().single(seed.random()) shouldBe Arb.long().single(seed.random())
         Arb.string().single(seed.random()) shouldBe Arb.string().single(seed.random())
         Arb.boolean().single(seed.random()) shouldBe Arb.boolean().single(seed.random())
      }
   }

   test("should use random seed by default") {
      // allow some failures for edge cases
      checkAll<Long, Long>(config = PropTestConfig(maxFailure = 5, minSuccess = 995)) { a, b ->
         a shouldNotBe b
      }
   }

   test("failed test should print seed") {
      shouldThrowAny {
         forAll<Int, Int> { a, b -> a + b == b + 1 }
      }.message.shouldContain("Repeat this test by using seed")
      shouldThrowAny {
         forAll<Int, Int>(config = PropTestConfig(seed = 12345)) { a, b -> a + b == b + 1 }
      }.message.shouldContain("Repeat this test by using seed 12345")
   }

   test("errored test should print seed") {
      shouldThrowAny {
         checkAll<Int, Int> { _, _ -> error("boom") }
      }.message.shouldContain("Repeat this test by using seed")
   }
})
