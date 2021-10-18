package com.sksamuel.kotest.engine.spec.invaliddsl

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestResult

class InvalidFreeSpecTest : FreeSpec() {
   init {

      aroundTest { (testCase, fn) ->
         val result = fn(testCase)
         if (result.isSuccess && testCase.name.testName == "should fail")
            TestResult.failure(AssertionError("leaf"), 0)
         else
            TestResult.success(0)
      }

      "context" - {
         "leaf" {
            "invalid" - {

            }
         }
      }
   }
}
