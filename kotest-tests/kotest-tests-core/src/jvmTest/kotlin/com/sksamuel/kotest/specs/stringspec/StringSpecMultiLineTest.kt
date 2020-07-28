package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestResult

// tests that multi line test names are normalized
class StringSpecMultiLineTest : StringSpec() {

   init {

      extension { (testCase, execute) ->
         execute(testCase)
         when (testCase.name) {
            "test    case    1" -> TestResult.success(0)
            else -> TestResult.throwable(IllegalStateException("failed"), 0)
         }
      }

      """
    test
    case
    1
    """ {

      }
   }

}
