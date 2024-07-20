package com.sksamuel.kotest.assertions

import io.kotest.assertions.retry
import io.kotest.assertions.retryConfig
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.nonDeterministicTestTimeSource
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark

class RetryTest : StringSpec() {
   init {
      coroutineTestScope = true
      nonDeterministicTestVirtualTimeEnabled = true

      "should allow execution of suspend functions" {
         val retryTester = retryTester(4)
         retry(maxRetry = 5, timeout = 900.milliseconds, delay = 100.milliseconds) {
            delay(100)
            retryTester.isReady() shouldBe true
         }
         retryTester.calledAtTimeInstance.shouldContainExactly(
            100.milliseconds + (100.milliseconds * 0),
            100.milliseconds + (100.milliseconds * 2),
            100.milliseconds + (100.milliseconds * 4),
            100.milliseconds + (100.milliseconds * 6),
         )
      }

      "should allow config" {
         val config = retryConfig {
            maxRetry = 5
            timeout = 900.milliseconds
            delay = 100.milliseconds
         }
         val retryTester = retryTester(4)
         retry(config) {
            delay(100)
            retryTester.isReady() shouldBe true
         }
         retryTester.calledAtTimeInstance.shouldContainExactly(
            100.milliseconds + (config.delay * 0),
            100.milliseconds + (config.delay * 2),
            100.milliseconds + (config.delay * 4),
            100.milliseconds + (config.delay * 6),
         )
      }

      "should run test until it passes in given number of times" {
         val retryTester = retryTester(4)
         shouldNotThrow<Exception> {
            retry(maxRetry = 5, timeout = 500.milliseconds, delay = 100.milliseconds) {
               retryTester.isReady() shouldBe true
            }
         }
         retryTester.calledAtTimeInstance.shouldContainExactly(
            0.milliseconds,
            100.milliseconds,
            200.milliseconds,
            300.milliseconds,
         )
      }

      "should not call given assertion beyond given number of times" {
         val retryTester = retryTester(4)
         val retryException = shouldThrow<AssertionError> {
            retry(maxRetry = 2, timeout = 500.milliseconds, delay = 100.milliseconds) {
               retryTester.isReady() shouldBe true
            }
         }
         retryException shouldHaveMessage "Test failed after 100ms; attempted 2 times; underlying cause was expected:<true> but was:<false>"
         retryTester.calledAtTimeInstance.shouldContainExactly(
            0.milliseconds,
            100.milliseconds,
         )
      }

      "should not call given assertion beyond given max duration" {

         val testTimeSource = nonDeterministicTestTimeSource()
         val config = retryConfig {
            maxRetry = 5
            timeout = 500.milliseconds
            delay = 400.milliseconds
            multiplier = 1
            timeSource = testTimeSource
         }

         val retryTester = retryTester(4)
         val retryException = shouldThrow<AssertionError> {
            retry(config) {
               retryTester.isReady() shouldBe true
            }
         }
         retryTester.calledAtTimeInstance shouldHaveSize 2
         retryException shouldHaveMessage "Test failed after 800ms; attempted 2 times; underlying cause was expected:<true> but was:<false>"
         retryTester.calledAtTimeInstance.shouldContainExactly(
            0.milliseconds,
            400.milliseconds,
         )
      }

      "should call given assertion exponentially" {
         val retryTester = retryTester(4)
         shouldNotThrow<Exception> {
            retry(maxRetry = 5, timeout = 900.milliseconds, delay = 100.milliseconds, multiplier = 2) {
               retryTester.isReady() shouldBe true
            }
         }
         retryTester.calledAtTimeInstance.shouldContainExactly(
            0.milliseconds,
            100.milliseconds,
            300.milliseconds,
            700.milliseconds,
         )
      }

      "should not retry in case of unexpected exception" {
         val retryTester = retryTester(2)
         val retryException = shouldThrow<IllegalStateException> {
            retry(5, 500.milliseconds, 20.milliseconds, 1, IllegalArgumentException::class) {
               retryTester.throwUnexpectedException()
            }
         }
         retryException shouldHaveMessage "unexpected exception from RetryTester"
         retryTester.calledAtTimeInstance.shouldContainExactly(
            0.milliseconds,
         )
      }

      "should retry in case of subclass exception" {
         val retryTester = retryTester(3)
         val retryException = shouldThrow<AssertionError> {
            retry(2, 500.milliseconds, 20.milliseconds, 1, Exception::class) {
               retryTester.throwUnexpectedException()
            }
         }
         retryException shouldHaveMessage "Test failed after 20ms; attempted 2 times; underlying cause was unexpected exception from RetryTester"
         retryTester.calledAtTimeInstance shouldHaveSize 2
      }

      "when maxRetry is negative, expect zero invocations" {
         val retryTester = retryTester(4)
         val retryException = shouldThrow<AssertionError> {
            retry(maxRetry = -1, timeout = 500.milliseconds) {
               retryTester.isReady() shouldBe true
            }
         }
         retryException shouldHaveMessage "Test failed after 0s; attempted 0 times"
         retryTester.calledAtTimeInstance.shouldBeEmpty()
      }

      "when multiplier is negative, expect ..." { // TODO
         val retryTester = retryTester(4)
         retry(maxRetry = 5, timeout = 900.milliseconds, delay = 100.milliseconds, multiplier = -1) {
            delay(100)
            retryTester.isReady() shouldBe true
         }
         retryTester.calledAtTimeInstance.shouldContainExactly(
            100.milliseconds,
            300.milliseconds,
            400.milliseconds,
            600.milliseconds,
         )
      }

      "when delay is negative, expect ..." { // TODO
         val retryTester = retryTester(4)
         shouldNotThrow<Exception> {
            retry(maxRetry = 5, timeout = 500.milliseconds, delay = (-100).milliseconds) {
               delay(100)
               retryTester.isReady() shouldBe true
            }
         }
         retryTester.calledAtTimeInstance.shouldContainExactly(
            100.milliseconds,
            200.milliseconds,
            300.milliseconds,
            400.milliseconds,
         )
      }

      "when timeout is negative, expect zero invocations" {
         val retryTester = retryTester(4)
         val retryException = shouldThrow<AssertionError> {
            retry(maxRetry = 5, timeout = (-500).milliseconds) {
               retryTester.isReady() shouldBe true
            }
         }
         retryException shouldHaveMessage "Test failed after 0s; attempted 0 times"
         retryTester.calledAtTimeInstance.shouldBeEmpty()
      }
   }

   private class RetryTester(
      private val readyAfter: Int,
      private val timeMark: TimeMark,
   ) {
      val calledAtTimeInstance = mutableListOf<Duration>()

      fun isReady(): Boolean {
         calledAtTimeInstance += timeMark.elapsedNow()
         return readyAfter == calledAtTimeInstance.size
      }

      fun throwUnexpectedException(): Nothing {
         calledAtTimeInstance += timeMark.elapsedNow()
         error("unexpected exception from RetryTester")
      }
   }

   private suspend fun retryTester(readyAfter: Int): RetryTester {
      return RetryTester(
         readyAfter = readyAfter,
         timeMark = nonDeterministicTestTimeSource().markNow()
      )
   }
}
