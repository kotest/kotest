package com.sksamuel.kotest.matchers.concurrent.suspension

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.concurrent.suspension.ShouldCompleteBetweenTimeSource
import io.kotest.matchers.concurrent.suspension.shouldCompleteBetween
import io.kotest.matchers.concurrent.suspension.shouldCompleteWithin
import io.kotest.matchers.concurrent.suspension.shouldTimeout
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class ConcurrentTest : FunSpec({

   coroutineTestScope = true

   test("should not fail when given lambda pass in given time") {
      val start = testCoroutineScheduler.timeSource.markNow()
      shouldNotThrowAny {
         shouldCompleteWithin(2.seconds) {
            delay(1.seconds)
         }
      }
      start.elapsedNow() shouldBe 1.seconds
   }

   test("should fail when given lambda does not complete in given time") {
      val start = testCoroutineScheduler.timeSource.markNow()
      val message = shouldFail {
         shouldCompleteWithin(1.seconds) {
            delay(1.5.seconds)
         }
      }.message

      message shouldBe "Operation should have completed within 1s"
      start.elapsedNow() shouldBe 1.seconds
   }

   test("should not fail when given lambda pass in given time range") {
      val start = testCoroutineScheduler.timeSource.markNow()
      withContext(ShouldCompleteBetweenTimeSource(testCoroutineScheduler.timeSource)) {
         shouldNotThrowAny {
            shouldCompleteBetween(1.seconds..2.seconds) {
               delay(1.5.seconds)
            }
         }
      }
      start.elapsedNow() shouldBe 1.5.seconds
   }

   test("should fail when given lambda pass before the given time range") {
      val start = testCoroutineScheduler.timeSource.markNow()
      val message = shouldFail {
         shouldCompleteBetween(1.seconds..2.seconds) {
            delay(0.5.seconds)
         }
      }.message

      message shouldBe "Operation should not have completed before 1s"
      start.elapsedNow() shouldBe 0.5.seconds
   }

   test("should fail when given lambda did not complete with in the given time range") {
      val start = testCoroutineScheduler.timeSource.markNow()
      val message = shouldFail {
         shouldCompleteBetween(1.seconds..2.seconds) {
            delay(2.5.seconds)
         }
      }.message

      message shouldBe "Operation should have completed within 1s..2s"
      start.elapsedNow() shouldBe 2.seconds
   }

   test("should not throw any if given lambda did not complete in given time") {
      val start = testCoroutineScheduler.timeSource.markNow()
      shouldNotThrowAny {
         shouldTimeout(1.seconds) {
            delay(1.1.seconds)
         }
      }
      start.elapsedNow() shouldBe 1.seconds
   }

   test("should fail if given lambda complete within given time") {
      val start = testCoroutineScheduler.timeSource.markNow()
      val failure = shouldFail {
         shouldTimeout(1.seconds) {
            delay(0.1.seconds)
         }
      }
      failure.message shouldContain "Operation should not have completed before 1s"
      start.elapsedNow() shouldBe 0.1.seconds
   }
})
