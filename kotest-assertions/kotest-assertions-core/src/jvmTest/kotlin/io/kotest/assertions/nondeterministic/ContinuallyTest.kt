package io.kotest.assertions.nondeterministic

import io.kotest.assertions.shouldFail
import io.kotest.common.testTimeSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ContinuallyTest : FunSpec() {

   init {
      coroutineTestScope = true

      test("pass tests that succeed for the entire duration") {
         val result = testContinually(500.milliseconds) {
            1 shouldBe 1
         }

         result.value shouldBe 1
         result.invocationTimes shouldHaveSize (500 / 25)
         result.invocationTimes.shouldForAll { it <= 500.milliseconds }
      }

      test("pass tests with null values") {
         val result = testContinually(500.milliseconds) {
            null shouldBe null
         }
         result.invocationTimes shouldHaveSize (500 / 25)
         result.invocationTimes.shouldForAll { it <= 500.milliseconds }
      }

      test("use interval function") {
         val config = continuallyConfig<Unit> {
            duration = 700.milliseconds
            intervalFn = DurationFn { 300.milliseconds }
         }
         var k = 0
         val result = testContinually(config) {
            1 shouldBe 1
            k++
         }
         result.invocationTimes shouldHaveSize 3
         result.invocationTimes.shouldForAll { it <= config.duration }
      }

      test("invoke the listener for each successful invocation") {
         var listened = 0
         var invoked = 0
         val config = continuallyConfig<Unit> {
            duration = 500.milliseconds
            listener = { _, _ -> listened++ }
         }
         val result = testContinually(config) {
            1 shouldBe 1
            invoked++
         }
         invoked shouldBe listened
         result.invocationTimes shouldHaveSize listened
      }

      test("fail broken tests immediately") {
         val start = testTimeSource().markNow()
         val failure = shouldFail {
            testContinually(1.minutes) {
               false shouldBe true
            }
         }
         failure.shouldHaveMessage("expected:<true> but was:<false>")
         start.elapsedNow() shouldBe Duration.ZERO
      }

      test("fail should throw the underlying error") {
         val start = testTimeSource().markNow()
         shouldFail {
            testContinually(1.minutes) {
               throw AssertionError("boom")
            }
         }.message shouldBe "boom"
         start.elapsedNow() shouldBe Duration.ZERO
      }

      test("fail tests start off as passing then fail within the period") {
         var n = 0
         val failure = shouldFail {
            testContinually(3.seconds) {
               delay(10)
               (n++ < 10) shouldBe true
            }
         }
         n shouldBe 11
         failure shouldHaveMessage "Test failed after 360ms; expected to pass for 3s; attempted 10 times\nUnderlying failure was: expected:<true> but was:<false>"
      }
   }
}


private suspend fun <T> testContinually(
   duration: Duration,
   test: suspend () -> T,
): TestContinuallyResult<T> {
   val start = testTimeSource().markNow()
   val invocationTimes = mutableListOf<Duration>()

   val value: T = continually(duration) {
      invocationTimes += start.elapsedNow()
      test()
   }

   return TestContinuallyResult(
      invocationTimes = invocationTimes,
      value = value,
   )
}

private suspend fun <T> testContinually(
   config: ContinuallyConfiguration<T>,
   test: suspend () -> T,
): TestContinuallyResult<T> {
   val start = testTimeSource().markNow()
   val invocationTimes = mutableListOf<Duration>()

   val value: T = continually(config = config) {
      invocationTimes += start.elapsedNow()
      test()
   }

   return TestContinuallyResult(
      invocationTimes = invocationTimes,
      value = value,
   )
}

private data class TestContinuallyResult<T>(
   val value: T,
   val invocationTimes: List<Duration>,
)
