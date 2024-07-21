package com.sksamuel.kotest.assertions.async

import io.kotest.assertions.async.shouldTimeout
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.common.testTimeSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class TimeoutTest : FunSpec({

   coroutineTestScope = true

   test("shouldTimeout - should not throw any if operation did not complete in given time") {
      val testDuration = testTimeSource().measureTime {
         shouldNotThrowAny {
            shouldTimeout(1.seconds) {
               delay(1.1.seconds)
            }
         }
      }
      testDuration shouldBe 1.seconds
   }

   test("shouldTimeout - should fail if operation completes within given time") {
      val (failure, testDuration) = testTimeSource().measureTimedValue {
         shouldFail {
            shouldTimeout(1.seconds) {
               delay(0.1.seconds)
            }
         }
      }
      failure.message shouldContain "Operation completed too quickly. Expected that operation completed faster than 1s, but it took 100ms."
      testDuration shouldBe 0.1.seconds
   }
})
