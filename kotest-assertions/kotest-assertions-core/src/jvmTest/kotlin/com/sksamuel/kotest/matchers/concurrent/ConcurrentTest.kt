package com.sksamuel.kotest.matchers.concurrent

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.concurrent.shouldCompleteWithin
import io.kotest.matchers.concurrent.shouldTimeout
import io.kotest.matchers.shouldBe
import java.util.concurrent.TimeUnit

class ConcurrentTest : FunSpec({

   test("should not fail when given lambda pass in given time") {
      shouldNotThrowAny {
         shouldCompleteWithin(2000, TimeUnit.MILLISECONDS) {
            Thread.sleep(1000)
            null
         }
      }
   }

   test("should fail when given lambda does not complete in given time") {
      val message = shouldThrow<AssertionError> {
         shouldCompleteWithin(1000, TimeUnit.MILLISECONDS) {
            Thread.sleep(1500)
            null
         }
      }.message

      message shouldBe "Test should have completed within 1000/MILLISECONDS"
   }

   test("should not throw any if given lambda did not complete in given time") {
      shouldNotThrowAny {
         shouldTimeout(1, TimeUnit.SECONDS) {
            Thread.sleep(1100)
         }
      }
   }

   test("should fail if given lambda complete within given time") {
      shouldThrow<AssertionError> {
         shouldTimeout(1, TimeUnit.SECONDS) {
            Thread.sleep(100)
         }
      }
   }
})
