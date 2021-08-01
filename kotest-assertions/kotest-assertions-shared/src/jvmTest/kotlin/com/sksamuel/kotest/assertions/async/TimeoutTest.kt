package com.sksamuel.kotest.assertions.async

import io.kotest.assertions.async.shouldTimeout
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.delay
import java.time.Duration
import java.util.concurrent.TimeUnit

class TimeoutTest : FunSpec({
   test("shouldTimeout should capture the error if a coroutine takes longer than the given timeout") {
      shouldTimeout(Duration.ofMillis(2)) {
         delay(25)
      }
       shouldTimeout(kotlin.time.Duration.milliseconds(2)) {
           delay(25)
       }
      shouldTimeout(2, TimeUnit.MILLISECONDS) {
         delay(25)
      }
   }
   test("shouldTimeout should fail if a coroutine does not timeout") {
      shouldFail {
         shouldTimeout(Duration.ofMillis(50)) {
            delay(1)
         }
      }
      shouldFail {
          shouldTimeout(kotlin.time.Duration.milliseconds(50)) {
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
