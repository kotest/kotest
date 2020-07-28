package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult

// tests that multi line test names are normalized
class FunSpecMultiLineTest : FunSpec() {

   init {

      extension { (testCase, execute) ->
         execute(testCase)
         when (testCase.name) {
            "test    case    1", "test    case    2", "context" -> TestResult.success(0)
            else -> TestResult.throwable(RuntimeException(testCase.name + " failed"), 0)
         }
      }

      test("""
    test
    case
    1
    """) {

      }

      context("context") {
         test("""
    test
    case
    2
    """) {

         }
      }
   }

}
