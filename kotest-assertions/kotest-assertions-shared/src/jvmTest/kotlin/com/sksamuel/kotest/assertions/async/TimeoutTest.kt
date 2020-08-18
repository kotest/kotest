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
      shouldTimeout(Duration.ofMillis(50)) {
         delay(250)
      }
      shouldTimeout(50.milliseconds) {
         delay(250)
      }
      shouldTimeout(50, TimeUnit.MILLISECONDS) {
         delay(250)
      }
   }
   test("shouldTimeout should fail if a coroutine finishes before the timeout") {
      shouldFail {
         shouldTimeout(Duration.ofMillis(50)) {
            delay(1)
         }
      }
      shouldFail {
         shouldTimeout(50.milliseconds) {
            delay(1)
         }
      }
      shouldFail {
         shouldTimeout(50, TimeUnit.MILLISECONDS) {
            delay(1)
         }
      }
   }
})
