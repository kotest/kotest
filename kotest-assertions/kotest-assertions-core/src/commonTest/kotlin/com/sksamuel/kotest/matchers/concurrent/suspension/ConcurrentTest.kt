package com.sksamuel.kotest.matchers.concurrent.suspension

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.common.testTimeSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.concurrent.suspension.shouldCompleteBetween
import io.kotest.matchers.concurrent.suspension.shouldCompleteWithin
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class ConcurrentTest : FunSpec({

   coroutineTestScope = true

   test("shouldCompleteWithin - should not fail when operation completes in given time") {
      val testDuration = testTimeSource().measureTime {
         shouldNotThrowAny {
            shouldCompleteWithin(2.seconds) {
               delay(1.seconds)
            }
         }
      }
      testDuration shouldBe 1.seconds
   }

   test("shouldCompleteWithin - should fail when operation does not complete in given time") {
      val (failure, testDuration) = testTimeSource().measureTimedValue {
         shouldFail {
            shouldCompleteWithin(1.seconds) {
               delay(1.5.seconds)
            }
         }
      }

      failure.message shouldBe "Operation took longer than expected. Expected that operation completed within 1s, but it took longer and was cancelled."
      testDuration shouldBe 1.seconds
   }

   test("shouldCompleteBetween - should not fail when operation completes in given time range") {
      val testDuration = testTimeSource().measureTime {
         shouldNotThrowAny {
            shouldCompleteBetween(1.seconds..2.seconds) {
               delay(1.5.seconds)
            }
         }
      }
      testDuration shouldBe 1.5.seconds
   }

   test("shouldCompleteBetween - should fail when operation completes before the given time range") {
      val (failure, testDuration) = testTimeSource().measureTimedValue {
         shouldFail {
            shouldCompleteBetween(1.seconds..2.seconds) {
               delay(0.5.seconds)
            }
         }
      }

      failure.message shouldBe "Operation completed too quickly. Expected that operation lasted at least 1s, but it took 500ms."
      testDuration shouldBe 0.5.seconds
   }

   test("shouldCompleteBetween - should fail when operation did not complete with in the given time range") {
      val (failure, testDuration) = testTimeSource().measureTimedValue {
         shouldFail {
            shouldCompleteBetween(1.seconds..2.seconds) {
               delay(2.5.seconds)
            }
         }
      }

      failure.message shouldBe "Operation took longer than expected. Expected that operation completed within 2s, but it took longer and was cancelled."
      testDuration shouldBe 2.seconds
   }
})
