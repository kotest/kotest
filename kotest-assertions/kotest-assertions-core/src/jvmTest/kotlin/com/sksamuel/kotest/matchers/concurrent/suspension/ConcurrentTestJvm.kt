// Remove when shouldCompleteWithin/shouldCompleteBetween/shouldTimeout are removed in 6.0
@file:Suppress("DEPRECATION")

package com.sksamuel.kotest.matchers.concurrent.suspension

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.concurrent.suspension.shouldCompleteBetween
import io.kotest.matchers.concurrent.suspension.shouldCompleteWithin
import io.kotest.matchers.concurrent.suspension.shouldTimeout
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

class ConcurrentTestJvm : FunSpec({

   coroutineTestScope = true
   nonDeterministicTestVirtualTimeEnabled = true

   test("should not fail when given lambda pass in given time") {
      shouldNotThrowAny {
         shouldCompleteWithin(2000, TimeUnit.MILLISECONDS) {
            delay(1.seconds)
         }
      }
   }

   test("should fail when given lambda does not complete in given time") {
      val failure = shouldFail {
         shouldCompleteWithin(1000, TimeUnit.MILLISECONDS) {
            delay(1.5.seconds)
         }
      }

      failure.message shouldBe "Operation should have completed within 1s"
   }

   test("should not fail when given lambda pass in given time range") {
      shouldNotThrowAny {
         shouldCompleteBetween(1, 2, TimeUnit.SECONDS) {
            delay(1.5.seconds)
         }
      }
   }

   test("should fail when given lambda pass before the given time range") {
      val failure = shouldFail {
         shouldCompleteBetween(1, 2, TimeUnit.SECONDS) {
            delay(0.5.seconds)
         }
      }

      failure.message shouldBe "Operation should not have completed before 1s"
   }

   test("should fail when given lambda did not complete with in the given time range") {
      val failure = shouldFail {
         shouldCompleteBetween(1, 2, TimeUnit.SECONDS) {
            delay(2.5.seconds)
         }
      }

      failure.message shouldBe "Operation should have completed within 1s..2s"
   }

   test("should not throw any if given lambda did not complete in given time") {
      shouldNotThrowAny {
         shouldTimeout(1, TimeUnit.SECONDS) {
            delay(1.1.seconds)
         }
      }
   }

   test("should fail if given lambda complete within given time") {
      val failure = shouldFail {
         shouldTimeout(1, TimeUnit.SECONDS) {
            delay(0.1.seconds)
         }
      }
      failure.message shouldContain "Operation should not have completed before 1s"
   }
})
