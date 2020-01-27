package com.sksamuel.kotest.inspectors

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.CompositeSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

val tests = funSpec {
   test("should error with large failure count #938") {
      shouldFail {
         List(100_000) { it }.forAll {
            it shouldBe -1
         }
      }
   }
}

class InspectorTests : CompositeSpec(tests)
