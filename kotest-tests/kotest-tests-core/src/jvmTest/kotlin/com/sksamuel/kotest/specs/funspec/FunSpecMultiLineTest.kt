package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestResult
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

// tests that multi line test names are normalized
@ExperimentalTime
class FunSpecMultiLineTest : FunSpec() {

   init {

      extension { (testCase, execute) ->
         execute(testCase)
         when (testCase.name.displayName()) {
            "test    case    1", "test    case    2", "context" -> TestResult.success(0.seconds)
            else -> TestResult.throwable(RuntimeException(testCase.name.displayName() + " failed"), 0.seconds)
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
