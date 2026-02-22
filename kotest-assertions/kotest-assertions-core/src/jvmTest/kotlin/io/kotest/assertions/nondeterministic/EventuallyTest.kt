package io.kotest.assertions.nondeterministic

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeBetween
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.delay
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@EnabledIf(LinuxOnlyGithubCondition::class)
class EventuallyTest : FunSpec() {

   init {
      test("pass working tests") {
         eventually(5.days) {
            1 shouldBe 1
         }
      }

      test("pass working tests for millis") {
         eventually(50000) {
            1 shouldBe 1
         }
      }

      test("should return final state") {
         var k = 0
         val config = eventuallyConfig {
            duration = 5.days
            interval = 1.milliseconds
         }
         val result = eventually(config) {
            k++
            k shouldBe 10
         }
         result shouldBe 10
      }

      test("an interval longer than duration should not override duration").config(timeout = 2.seconds) {
         val start = TimeSource.Monotonic.markNow()
         val config = eventuallyConfig {
            duration = 1.milliseconds
            interval = 1.seconds
         }
         shouldThrowAny {
            eventually(config) {
               1 shouldBe 2
            }
         }
         start.elapsedNow() shouldBeLessThan 1.seconds // should be less the interval
      }

      context("pass tests that completed within the time allowed") {
         test("RuntimeException") {
            val start = TimeSource.Monotonic.markNow()
            val end = start.plus(10.milliseconds)
            val config = eventuallyConfig {
               duration = 1.days
               interval = 1.milliseconds
            }
            var executed = false
            eventually(config) {
               executed = true
               if (end.hasNotPassedNow())
                  throw RuntimeException("foo")
            }
            executed shouldBe true
         }

         test("AssertionError") {
            val start = TimeSource.Monotonic.markNow()
            val end = start.plus(10.milliseconds)
            val config = eventuallyConfig {
               duration = 1.days
               interval = 1.milliseconds
            }
            var executed = false
            eventually(config) {
               executed = true
               if (end.hasNotPassedNow())
                  assert(false)
            }
            executed shouldBe true
         }

         test("custom expected exception") {
            val config = eventuallyConfig {
               duration = 1.days
               interval = 1.milliseconds
               expectedExceptions = setOf(FileNotFoundException::class)
            }

            val start = TimeSource.Monotonic.markNow()
            val end = start.plus(10.milliseconds)

            var executed = false
            eventually(config) {
               executed = true
               if (end.hasNotPassedNow())
                  throw FileNotFoundException()
            }
            executed shouldBe true
         }
      }

      test("fail tests that do not complete within the time allowed") {
         val config = eventuallyConfig {
            duration = 10.milliseconds
            interval = 1.milliseconds
         }

         val failure = shouldFail {
            eventually(config) {
               throw RuntimeException("foo")
            }
         }
         failure.message shouldContain "Block failed after"
         failure.message shouldContain "The first error was caused by: foo"
         failure.message shouldContain "The last error was caused by: foo"
      }

      test("fail tests that do not complete within the time allowed for millis") {
         val failure = shouldFail {
            eventually(100) {
               throw RuntimeException("foo")
            }
         }
         failure.message shouldContain "Block failed after"
         failure.message shouldContain "The first error was caused by: foo"
         failure.message shouldContain "The last error was caused by: foo"
      }

      test("return the result computed inside") {
         val result = eventually(2.seconds) { 1 }
         result shouldBe 1
      }

      test("fail tests that throw unexpected exception types defined by a set") {
         val config = eventuallyConfig {
            duration = 5.seconds
            expectedExceptions = setOf(IOException::class)
         }

         val failure = shouldFail {
            eventually(config) {
               throw ArrayIndexOutOfBoundsException()
            }
         }
         failure.message shouldContain "Block failed after"
         failure.message shouldContain "attempted 1 time"
         failure.message shouldContain "The first error was caused by: \njava.lang.ArrayIndexOutOfBoundsException"
      }

      test("fail tests that throw unexpected exception types defined by a function") {
         val config = eventuallyConfig {
            duration = 5.seconds
            expectedExceptionsFn = { ex -> ex is IOException }
         }

         val failure = shouldFail {
            eventually(config) {
               throw ArrayIndexOutOfBoundsException()
            }
         }
         failure.message shouldContain "Block failed after"
         failure.message shouldContain "attempted 1 time"
         failure.message shouldContain "The first error was caused by: \njava.lang.ArrayIndexOutOfBoundsException"
      }

      test("pass tests that throws FileNotFoundException for some time") {
         val start = TimeSource.Monotonic.markNow()
         val end = start.plus(50.milliseconds)
         var iterations = 0
         val config = eventuallyConfig {
            duration = 5.days
            interval = 5.milliseconds
         }
         eventually(config) {
            iterations++
            if (end.hasNotPassedNow())
               throw FileNotFoundException("foo")
         }
         // the delay is 5ms so after 50ms should be approx 10 iterations, will go a few either side because of timing delays in CI
         iterations.shouldBeBetween(8, 12)
      }

      test("handle kotlin assertion errors") {
         var thrown = false
         eventually(1.days) {
            if (!thrown) {
               thrown = true
               throw AssertionError("boom")
            }
         }
      }

      test("handle java assertion errors") {
         var thrown = false
         eventually(1.days) {
            if (!thrown) {
               thrown = true
               throw java.lang.AssertionError("boom")
            }
         }
      }

      test("do not retry after OutOfMemoryError") {
         var count = 0
         val thrown = shouldThrow<Error> {
            eventually(1.days) {
               count++
               throw OutOfMemoryError()
            }
         }
         assertSoftly {
            thrown.shouldBeInstanceOf<OutOfMemoryError>()
            count shouldBe 1
         }
      }

      test("do not retry after StackOverflowError") {
         var count = 0
         val thrown = shouldThrow<Error> {
            eventually(1.days) {
               count++
               throw StackOverflowError()
            }
         }
         assertSoftly {
            thrown.shouldBeInstanceOf<StackOverflowError>()
            count shouldBe 1
         }
      }

      test("display the first and last underlying failures") {
         var count = 0
         val config = eventuallyConfig {
            interval = 1.milliseconds
            duration = 10.milliseconds
         }
         val message = shouldFail {
            eventually(config) {
               if (count++ == 0) {
                  AssertionErrorBuilder.fail("first")
               } else {
                  AssertionErrorBuilder.fail("last")
               }
            }
         }.message
         message shouldContain "Block failed after"
         message shouldContain "The first error was caused by: first"
         message shouldContain "The last error was caused by: last"
      }

      test("non-suppressible exception is not retried, but still printed with eventually-info") {
         val config = eventuallyConfig {
            duration = 400.milliseconds
            expectedExceptionsFn = { it is AssertionError }
         }

         var count = 0
         val message = shouldFail {
            eventually(config) {
               if (count++ == 0) {
                  AssertionErrorBuilder.fail("first")
               } else {
                  error("last")
               }
            }
         }.message
         count shouldBe 2
         message shouldContain "Block failed after"
         message shouldContain "The first error was caused by: first"
         message shouldContain "The last error was caused by: last"
      }

      test("allow suspendable functions") {
         val start = TimeSource.Monotonic.markNow()
         eventually(500.milliseconds) {
            delay(2.milliseconds)
         }
         // should be around the 47 millis we delayed for, allow for more for slow CI, but should be nowhere near the eventually limit
         start.elapsedNow() shouldBeLessThan 50.milliseconds
      }

      test("allow configuring interval delay") {
         var count = 0
         val config = eventuallyConfig {
            duration = 250.milliseconds
            interval = 100.milliseconds
         }
         eventually(config) {
            count += 1
         }
         count.shouldBeLessThan(3)
      }

      test("handle shouldNotBeNull") {
         val start = TimeSource.Monotonic.markNow()
         val duration = 100.milliseconds
         val failure = shouldFail {
            eventually(duration) {
               val str: String? = null
               str.shouldNotBeNull()
            }
         }
         failure.message shouldContain "Block failed after"
         failure.message shouldContain "The first error was caused by: Expected value to not be null, but was null."
         failure.message shouldContain "The last error was caused by: Expected value to not be null, but was null."
         start.elapsedNow() shouldBeLessThan duration * 3 // allow a bit extra for CI
      }

      test("support fibonacci interval functions") {
         val start = TimeSource.Monotonic.markNow()
         val invocations = mutableListOf<Duration>()
         val config = eventuallyConfig {
            duration = 2.seconds
            intervalFn = 25.milliseconds.fibonacci()
            listener = object : EventuallyListener {
               override suspend fun invoke(iteration: Int, error: Throwable) {
                  invocations += start.elapsedNow()
               }
            }
         }
         var t = ""
         eventually(config) {
            t += "x"
            t shouldBe "xxxxxx"
         }
         invocations.size shouldBe 5
      }

      test("eventually has a shareable configuration") {
         val slow = eventuallyConfig {
            duration = 5.seconds
         }

         var i = 0
         val fast = slow.copy(retries = 1)

         assertSoftly {
            slow.retries shouldBe Int.MAX_VALUE
            fast.retries shouldBe 1
            slow.duration shouldBe 5.seconds
            fast.duration shouldBe 5.seconds
         }

         eventually(5.seconds) {
            5
         }

         eventually(5.seconds) {
            i++
         }

         i shouldBe 1
      }

      test("throws if retry limit is exceeded") {
         val config = eventuallyConfig {
            duration = 5.seconds
            retries = 2
         }
         val failure = shouldFail {
            eventually(config) {
               1 shouldBe 2
            }
         }
         failure.message shouldContain "Block failed after"
      }

      test("override assertion to hard assertion before executing assertion and reset it after executing") {
         val start = TimeSource.Monotonic.markNow()
         val target = start.plus(150.milliseconds)
         val failure = shouldFail {
            assertSoftly {
               withClue("Eventually which should pass") {
                  eventually(2.seconds) {
                     if (target.hasNotPassedNow()) {
                        AssertionErrorBuilder.fail("target has not passed")
                     }
                  }
               }
               withClue("1 should never be 2") {
                  1 shouldBe 2
               }
               withClue("2 should never be 3") {
                  2 shouldBe 3
               }
            }
         }

         failure.message.shouldContainInOrder(
            "The following 2 assertions failed:",
            "1) 1 should never be 2",
            "2) 2 should never be 3",
         )
      }

      test("call the listener when an exception is thrown in the producer function") {
         var k = 0
         var t: Throwable? = null
         val config = eventuallyConfig {
            duration = 5.seconds
            retries = 1
            listener = { iteration, error ->
               k = iteration
               t = error
            }
         }
         shouldThrow<Throwable> {
            eventually(config) {
               withClue("1 should never be 2") { 1 shouldBe 2 }
            }
         }
         k shouldBe 1
         t.shouldNotBeNull()
      }

      test("allows a set of exceptions") {
         val exceptions = setOf(
            Pair(FileNotFoundException::class, FileNotFoundException()),
            Pair(AssertionError::class, AssertionError()),
            Pair(java.lang.RuntimeException::class, java.lang.RuntimeException()),
         )
         var i = 0
         val config = eventuallyConfig {
            duration = 5.seconds
            expectedExceptions = exceptions.map { it.first }.toSet()
         }
         eventually(config) {
            exceptions.elementAtOrNull(i++)?.run {
               throw this.second
            }
         }

         i shouldBe exceptions.size + 1
      }

      test("short-circuited exceptions are not retried") {
         val failure = shouldFail {
            val config = eventuallyConfig {
               duration = 5.seconds
               shortCircuit = { true }
            }
            eventually(config) {
               1 shouldBe 2
            }
         }
         failure.message shouldContain "attempted 1 time"
      }

      test("suppress first error") {
         var count = 0
         shouldFail {
            val config = eventuallyConfig {
               duration = 10.milliseconds
               includeFirst = false
               interval = 1.milliseconds
            }
            eventually(config) {
               if (count++ == 0) {
                  AssertionErrorBuilder.fail("first")
               } else {
                  AssertionErrorBuilder.fail("last")
               }
            }
         }.message shouldNotContain "The first error was caused by: first"
      }

      test("raise error if duration is less than 0") {
         val message =
            shouldThrow<IllegalArgumentException> {
               eventually((-1).milliseconds) {
                  1 shouldBe 2
               }
            }.message

         message shouldContain "Duration must be greater than or equal to 0"
      }

      test("raise error if retries is less than 0") {
         val message =
            shouldThrow<IllegalArgumentException> {
               eventuallyConfig {
                  retries = -1
               }
            }.message

         message shouldContain "Retries must be greater than or equal to 0"
      }

      test("should exit once retries has been reached") {
         val finalCount = 12
         var count = 0
         val config = eventuallyConfig {
            retries = finalCount
            duration = 1.days
            interval = 1.milliseconds
         }
         shouldThrow<AssertionError> {
            eventually(config) {
               count++
               1 shouldBe 2
            }
         }

         count shouldBe finalCount
      }

      // https://github.com/kotest/kotest/issues/3988
      test("test eventually without configuration") {
         var count = 0
         eventually {
            count += 1
            count shouldBe 100
         }
         count shouldBe 100
      }

      context("with real time") {
         test("eventually should throw AssertionError if function suspends and does not pass after duration").config(
            coroutineTestScope = false
         ) {
            shouldThrow<AssertionError> {
               eventually(100.milliseconds) {
                  delay(10)
                  "error" shouldBe "ok"
               }
            }
         }

         test("eventually should throw AssertionError if function does not return within specified duration").config(
            coroutineTestScope = false
         ) {
            shouldThrow<AssertionError> {
               eventually(10.milliseconds) {
                  delay(1.days)
                  "error" shouldBe "ok"
               }
            }
         }
      }

      context("with virtual time") {
         test("eventually should throw AssertionError if function suspends and does not pass after duration").config(
            coroutineTestScope = true
         ) {
            shouldThrow<AssertionError> {
               eventually(100.milliseconds) {
                  delay(10)
                  "error" shouldBe "ok"
               }
            }
         }
         test("eventually should throw AssertionError if function does not return within specified duration").config(
            coroutineTestScope = true
         ) {
            shouldThrow<AssertionError> {
               eventually(10.milliseconds) {
                  delay(1.days)
                  "error" shouldBe "ok"
               }
            }
         }
      }
   }
}
