package com.sksamuel.kotest.matchers.concurrent.suspension

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.concurrent.suspension.shouldCompleteBetween
import io.kotest.matchers.concurrent.suspension.shouldCompleteWithin
import io.kotest.matchers.concurrent.suspension.shouldTimeout
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class ConcurrentTest : FunSpec({

   test("should not fail when given lambda pass in given time") {
      shouldNotThrowAny {
         shouldCompleteWithin(2000, TimeUnit.MILLISECONDS) {
            delay(1000)
            null
         }
      }
   }

   test("should fail when given lambda does not complete in given time") {
      val message = shouldThrow<AssertionError> {
         shouldCompleteWithin(1000, TimeUnit.MILLISECONDS) {
            delay(1500)
            null
         }
      }.message

      message shouldBe "Test should have completed within 1000/MILLISECONDS"
   }

   test("should not fail when given lambda pass in given time range") {
      shouldNotThrowAny {
         shouldCompleteBetween(1, 2, TimeUnit.SECONDS) {
            delay(1500)
         }
      }
   }

   test("should fail when given lambda pass before the given time range") {
      val message = shouldThrow<AssertionError> {
         shouldCompleteBetween(1, 2, TimeUnit.SECONDS) {
            delay(500)
         }
      }.message

      message shouldBe "Test should not have completed before 1/SECONDS"
   }

   test("should fail when given lambda did not complete with in the given time range") {
      val message = shouldThrow<AssertionError> {
         shouldCompleteBetween(1, 2, TimeUnit.SECONDS) {
            delay(2500)
         }
      }.message

      message shouldBe "Test should have completed within 1/SECONDS to 2/SECONDS"
   }

   test("should not throw any if given lambda did not complete in given time") {
      shouldNotThrowAny {
         shouldTimeout(1, TimeUnit.SECONDS) {
            delay(1100)
         }
      }
   }

   test("should fail if given lambda complete within given time") {
      shouldThrow<AssertionError> {
         shouldTimeout(1, TimeUnit.SECONDS) {
            delay(100)
         }
      }
   }
})
