package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.toTestResult

// tests that multi line test names are normalized
class StringSpecMultiLineTest : StringSpec() {

   init {

      extension { (testCase, execute) ->
         execute(testCase)
         when (testCase.displayName) {
            "test    case    1" -> TestResult.success(0)
            else -> IllegalStateException("failed").toTestResult(0)
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
