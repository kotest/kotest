package com.sksamuel.kotest.engine.spec.multilinename

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.matchers.shouldBe

// tests that multi line test names are normalized
class ExpectSpecMultiLineTest : ExpectSpec() {

   init {

      val names = mutableSetOf<String>()

      afterSpec {
         names shouldBe setOf("test case 1", "test case 2")
      }

      expect(
         """
    test
    case
    1
    """
      ) {
         names.add(this.testCase.name.testName)
      }

      context("context") {
         expect(
            """
    test
    case
    2
    """
         ) {
            names.add(this.testCase.name.testName)

         }
      }
   }

}
