package io.kotest.assertions.nondeterministic

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.nonDeterministicTestTimeSource
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@EnabledIf(LinuxOnlyGithubCondition::class)
class ContinuallyTest : FunSpec() {

   init {
      coroutineTestScope = true
      nonDeterministicTestVirtualTimeEnabled = true

      test("pass tests that succeed for the entire duration") {
         val result = testContinually(500.milliseconds) {
            1 shouldBe 1
         }

         result.value shouldBe 1
         result.invocationTimes shouldHaveSize (500 / 25)
         result.invocationTimes.shouldForAll { it <= 500.milliseconds }
      }

      test("pass tests that succeed for the entire duration for millis") {
         val result = testContinually(500) {
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
         val start = nonDeterministicTestTimeSource().markNow()
         val failure = shouldFail {
            testContinually(1.minutes) {
               false shouldBe true
            }
         }
         failure.shouldHaveMessage("expected:<true> but was:<false>")
         start.elapsedNow() shouldBe Duration.ZERO
      }

      test("fail should throw the underlying error") {
         val start = nonDeterministicTestTimeSource().markNow()
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

      test("continually should throw AssertionError if function suspends and does not pass after duration").config(
         coroutineTestScope = false
      ) {
         shouldThrow<AssertionError> {
            continually(100.milliseconds) {
               delay(10.milliseconds)
               "error" shouldBe "ok"
            }
         }.also { error ->
            error shouldHaveMessage "expected:<ok> but was:<error>"
         }
      }

      test("continually should throw AssertionError if function does not return within specified duration - even if the assertion would have passed").config(
         coroutineTestScope = false
      ) {
         shouldThrow<AssertionError> {
            continually(10.milliseconds) {
               delay(1.days)
               "ok" shouldBe "ok"
            }
         }.also { error ->
            val msg = requireNotNull(error.message)

            val regex = Regex("""Test timed out at ([\d.]+)ms as max expected duration was 10ms; attempted 0 times""")
            msg shouldMatch regex

            val actualMs = regex.find(msg)!!.groupValues[1].toDouble().toDuration(DurationUnit.MILLISECONDS)
            actualMs shouldBeGreaterThan 10.milliseconds
         }
      }
   }
}


private suspend fun <T> testContinually(
   duration: Duration,
   test: suspend () -> T,
): TestContinuallyResult<T> {
   val start = nonDeterministicTestTimeSource().markNow()
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
   durationMs: Long,
   test: suspend () -> T,
): TestContinuallyResult<T> {
   val start = nonDeterministicTestTimeSource().markNow()
   val invocationTimes = mutableListOf<Duration>()

   val value: T = continually(durationMs) {
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
   val start = nonDeterministicTestTimeSource().markNow()
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
