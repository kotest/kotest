package com.sksamuel.kotest.matchers.concurrent.suspension

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.common.nonDeterministicTestTimeSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.concurrent.suspension.shouldCompleteBetween
import io.kotest.matchers.concurrent.suspension.shouldCompleteWithin
import io.kotest.matchers.concurrent.suspension.shouldTimeout
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class ConcurrentTest : FunSpec({

   coroutineTestScope = true
   nonDeterministicTestVirtualTimeEnabled = true

   test("should not fail when given lambda pass in given time") {
      val testDuration = nonDeterministicTestTimeSource().measureTime {
         shouldNotThrowAny {
            shouldCompleteWithin(2.seconds) {
               delay(1.seconds)
            }
         }
      }
      testDuration shouldBe 1.seconds
   }

   test("should fail when given lambda does not complete in given time") {
      val (failure, testDuration) = nonDeterministicTestTimeSource().measureTimedValue {
         shouldFail {
            shouldCompleteWithin(1.seconds) {
               delay(1.5.seconds)
            }
         }
      }

      failure.message shouldBe "Operation should have completed within 1s"
      testDuration shouldBe 1.seconds
   }

   test("should not fail when given lambda pass in given time range") {
      val testDuration = nonDeterministicTestTimeSource().measureTime {
         shouldNotThrowAny {
            shouldCompleteBetween(1.seconds..2.seconds) {
               delay(1.5.seconds)
            }
         }
      }
      testDuration shouldBe 1.5.seconds
   }

   test("should fail when given lambda pass before the given time range") {
      val (failure, testDuration) = nonDeterministicTestTimeSource().measureTimedValue {
         shouldFail {
            shouldCompleteBetween(1.seconds..2.seconds) {
               delay(0.5.seconds)
            }
         }
      }

      failure.message shouldBe "Operation should not have completed before 1s"
      testDuration shouldBe 0.5.seconds
   }

   test("should fail when given lambda did not complete with in the given time range") {
      val (failure, testDuration) = nonDeterministicTestTimeSource().measureTimedValue {
         shouldFail {
            shouldCompleteBetween(1.seconds..2.seconds) {
               delay(2.5.seconds)
            }
         }
      }

      failure.message shouldBe "Operation should have completed within 1s..2s"
      testDuration shouldBe 2.seconds
   }

   test("should not throw any if given lambda did not complete in given time") {
      val testDuration = nonDeterministicTestTimeSource().measureTime {
         shouldNotThrowAny {
            shouldTimeout(1.seconds) {
               delay(1.1.seconds)
            }
         }
      }
      testDuration shouldBe 1.seconds
   }

   test("should fail if given lambda complete within given time") {
      val (failure, testDuration) = nonDeterministicTestTimeSource().measureTimedValue {
         shouldFail {
            shouldTimeout(1.seconds) {
               delay(0.1.seconds)
            }
         }
      }
      failure.message shouldContain "Operation should not have completed before 1s"
      testDuration shouldBe 0.1.seconds
   }
})
