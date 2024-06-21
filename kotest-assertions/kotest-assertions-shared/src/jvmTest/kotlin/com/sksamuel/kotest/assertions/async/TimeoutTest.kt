package com.sksamuel.kotest.assertions.async

import io.kotest.assertions.async.shouldTimeout
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class TimeoutTest : FunSpec({
   test("shouldTimeout should capture the error if a coroutine takes longer than the given timeout") {
      shouldTimeout(Duration.ofMillis(2)) {
         delay(100)
      }
       shouldTimeout(2.milliseconds) {
           delay(100)
       }
      shouldTimeout(2, TimeUnit.MILLISECONDS) {
         delay(100)
      }
   }
   test("shouldTimeout should fail if a coroutine does not timeout") {
      shouldFail {
         shouldTimeout(Duration.ofMillis(200)) {
            delay(1)
         }
      }
      shouldFail {
          shouldTimeout(200.milliseconds) {
              delay(1)
          }
      }
      shouldFail {
         shouldTimeout(200, TimeUnit.MILLISECONDS) {
            delay(1)
         }
      }
   }
})
