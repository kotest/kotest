package com.sksamuel.kotest.assertions.async

import io.kotest.assertions.async.shouldTimeout
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
class TimeoutTest : FunSpec({
   test("shouldTimeout should pass if a coroutine takes longer than the given timeout") {
      shouldTimeout(Duration.ofMillis(5)) {
         delay(10)
      }
      shouldTimeout(5.milliseconds) {
         delay(10)
      }
      shouldTimeout(5, TimeUnit.MILLISECONDS) {
         delay(10)
      }
   }
   test("shouldTimeout should fail if a coroutine finishes before the timeout") {
      shouldFail {
         shouldTimeout(Duration.ofMillis(5)) {
            delay(1)
         }
      }
      shouldFail {
         shouldTimeout(5.milliseconds) {
            delay(1)
         }
      }
      shouldFail {
         shouldTimeout(5, TimeUnit.MILLISECONDS) {
            delay(1)
         }
      }
   }
})
