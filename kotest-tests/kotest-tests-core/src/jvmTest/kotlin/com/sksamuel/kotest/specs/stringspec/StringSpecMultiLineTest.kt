package com.sksamuel.kotest.specs.stringspec

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestResult
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

// tests that multi line test names are normalized
@OptIn(ExperimentalTime::class)
class StringSpecMultiLineTest : StringSpec() {

   init {

      extension { (testCase, execute) ->
         execute(testCase)
         when (testCase.name) {
            "test    case    1" -> TestResult.success(0.seconds)
            else -> TestResult.throwable(IllegalStateException("failed"), 0.seconds)
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
