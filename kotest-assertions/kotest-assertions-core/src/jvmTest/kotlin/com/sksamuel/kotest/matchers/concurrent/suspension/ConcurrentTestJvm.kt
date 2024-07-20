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

      failure.message shouldBe "Operation took longer than expected. Expected that operation completed within 1s, but it took longer and was cancelled."
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

      failure.message shouldBe "Operation completed too quickly. Expected that operation lasted at least 1s, but it took 500ms."
   }

   test("should fail when given lambda did not complete with in the given time range") {
      val failure = shouldFail {
         shouldCompleteBetween(1, 2, TimeUnit.SECONDS) {
            delay(2.5.seconds)
         }
      }

      failure.message shouldBe "Operation took longer than expected. Expected that operation completed within 2s, but it took longer and was cancelled."
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
      failure.message shouldContain "Operation completed too quickly. Expected that operation completed faster than 1s, but it took 100ms."
   }
})
