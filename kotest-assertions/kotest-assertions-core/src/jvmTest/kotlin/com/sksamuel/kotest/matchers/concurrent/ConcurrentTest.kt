package com.sksamuel.kotest.matchers.concurrent

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.concurrent.shouldCompleteWithin
import io.kotest.matchers.concurrent.shouldTimeout
import io.kotest.matchers.shouldBe
import java.util.concurrent.TimeUnit

class ConcurrentTest : FunSpec({

   test("should not fail when given lambda pass in given time using blocking call") {
      shouldNotThrowAny {
         shouldCompleteWithin(1500, TimeUnit.MILLISECONDS) {
            Thread.sleep(10)
         }
      }
   }

   test("should fail when given lambda does not complete in given time") {
      val message = shouldThrow<AssertionError> {
         shouldCompleteWithin(2, TimeUnit.MILLISECONDS) {
            Thread.sleep(500)
            null
         }
      }.message

      message shouldBe "Test should have completed within 2/MILLISECONDS"
   }

   test("should return the resulting value of the function block") {
      val result = shouldCompleteWithin(1000, TimeUnit.MILLISECONDS) {
         "some value"
      }

      result shouldBe "some value"
   }

   test("should not throw any if given lambda did not complete in given time") {
      shouldNotThrowAny {
         shouldTimeout(2, TimeUnit.MILLISECONDS) {
            Thread.sleep(1000)
         }
      }
   }

   test("should fail if given lambda complete within given time") {
      shouldThrow<AssertionError> {
         shouldTimeout(1000, TimeUnit.MILLISECONDS) {
            Thread.sleep(10)
         }
      }
   }
})
