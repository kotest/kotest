package com.sksamuel.kotest.engine.spec.multilinename

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// tests that multi line test names are normalized
class FunSpecMultiLineTest : FunSpec() {

   init {

      val names = mutableSetOf<String>()

      afterSpec {
         names shouldBe setOf("test case 1", "test case 2")
      }

      test(
         """
    test
    case
    1
    """
      ) {
         names.add(this.testCase.name.testName)
      }

      context("context") {
         test(
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
