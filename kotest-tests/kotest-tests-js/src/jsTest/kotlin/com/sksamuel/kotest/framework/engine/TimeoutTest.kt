package com.sksamuel.kotest.framework.engine

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TimeoutTest : FunSpec() {
   init {

      aroundTest { (tc, fn) ->
         val result = fn(tc)
         if (tc.name.name == "JS engine should capture timeouts") {
            if (result.isErrorOrFailure)
               TestResult.Success(0.milliseconds)
            else
               TestResultBuilder.builder().withError(Throwable("Test should have failed")).build()
         } else {
            result
         }
      }

      // the default JS timeout in Karma is 2000, so if this passes when the delay is larger, then we know
      // Kotest is setting timeouts correctly on the karma promises
      test("JS engine should set timeout on the underlying promise").config(timeout = 1.days) {
         delay(3.seconds)
      }

      // the default JS timeout in Karma is 2 seconds, but Kotest should override that to the Kotest default of 10 minutes
      // if this test passes, then we know that the default has been set correctly because the delay in the test is over 2 seconds
      test("JS engine should set default timeout on the underlying promise") {
         delay(3.seconds)
      }

      // testing that we can set a very low timeout and capture the error
      test("JS engine should capture timeouts").config(timeout = 1.milliseconds) {
         delay(1.seconds)
      }
   }
}
