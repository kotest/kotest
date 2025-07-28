package com.sksamuel.kotest.engine.active

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.WordSpec

class BangDisableWordSpec : WordSpec({
   "using the bang symbol" should {
      "!disable this test" {
        AssertionErrorBuilder.fail("boom")
      }
   }
})

class BangDisableBehaviorSpec : BehaviorSpec({
   given("given a test") {
      `when`("when using bang") {
         then("!test should be disabled") {
           AssertionErrorBuilder.fail("boom")
         }
      }
   }

   given("!given a disabled given") {
      error("boom")
   }
})
