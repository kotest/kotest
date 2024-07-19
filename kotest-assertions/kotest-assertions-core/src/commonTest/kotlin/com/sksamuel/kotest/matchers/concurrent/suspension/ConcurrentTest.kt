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

      failure.message shouldBe "Operation took longer than expected. Expected that operation completed within 1s, but it took longer and was cancelled."
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

      failure.message shouldBe "Operation completed too quickly. Expected that operation lasted at least 1s, but it took 500ms."
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

      failure.message shouldBe "Operation took longer than expected. Expected that operation completed within 2s, but it took longer and was cancelled."
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

   test("should fail if operation completes within given time") {
      val (failure, testDuration) = nonDeterministicTestTimeSource().measureTimedValue {
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
