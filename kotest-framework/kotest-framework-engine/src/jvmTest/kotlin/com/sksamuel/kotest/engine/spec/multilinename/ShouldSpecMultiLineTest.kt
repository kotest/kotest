package com.sksamuel.kotest.engine.spec.multilinename

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

// tests that multi line test names are normalized
class ShouldSpecMultiLineTest : ShouldSpec() {

   init {

      val names = mutableSetOf<String>()

      afterSpec {
         names shouldBe setOf("test case 1", "test case 2")
      }

      should(
         """
    test
    case
    1
    """
      ) {
         names.add(this.testCase.name.name)
      }

      context("context") {
         should(
            """
    test
    case
    2
    """
         ) {
            names.add(this.testCase.name.name)

         }
      }
   }

}
