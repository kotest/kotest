package io.kotest.assertions.nondeterministic

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
import io.kotest.common.nonDeterministicTestTimeSource
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
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

//@EnabledIf(LinuxCondition::class)
class EventuallyTest : FunSpec() {

   init {
      coroutineTestScope = true
      nonDeterministicTestVirtualTimeEnabled = true

      test("pass working tests") {
         val result = testEventually(5.days) {
            1 shouldBe 1
         }
         result.value shouldBe 1
         result.invocationTimes.shouldContainExactly(0.milliseconds)
      }

      test("should return final state") {
         var k = 0
         val result = testEventually(5.days) {
            k++
            k shouldBe 10
         }
         result.value shouldBe 10
         result.invocationTimes shouldHaveSize 10
      }

      test("an interval longer than duration should not override duration").config(timeout = 2.seconds) {
         val start = nonDeterministicTestTimeSource().markNow()
         val config = eventuallyConfig {
            duration = 1.seconds
            interval = 100.seconds
         }
         shouldThrowAny {
            testEventually(config) {
               1 shouldBe 2
            }
         }
         start.elapsedNow() shouldBe config.duration
      }

      context("pass tests that completed within the time allowed") {
         test("RuntimeException") {
            val start = nonDeterministicTestTimeSource().markNow()
            val end = start.plus(150.milliseconds)
            val result = testEventually(1.seconds) {
               if (end.hasNotPassedNow())
                  throw RuntimeException("foo")
            }
            result.invocationTimes.shouldContainExactly(
               0.milliseconds,
               25.milliseconds,
               50.milliseconds,
               75.milliseconds,
               100.milliseconds,
               125.milliseconds,
               150.milliseconds,
            )
         }

         test("AssertionError") {
            val start = nonDeterministicTestTimeSource().markNow()
            val end = start.plus(150.milliseconds)
            val result = testEventually(5.days) {
               if (end.hasNotPassedNow())
                  assert(false)
            }
            result.invocationTimes.shouldContainExactly(
               0.milliseconds,
               25.milliseconds,
               50.milliseconds,
               75.milliseconds,
               100.milliseconds,
               125.milliseconds,
               150.milliseconds,
            )
         }

         test("custom expected exception") {
            val config = eventuallyConfig {
               duration = 5.seconds
               expectedExceptions = setOf(FileNotFoundException::class)
            }

            val start = nonDeterministicTestTimeSource().markNow()
            val end = start.plus(150.milliseconds)

            val result = testEventually(config) {
               if (end.hasNotPassedNow())
                  throw FileNotFoundException()
            }

            result.invocationTimes.shouldContainExactly(
               0.milliseconds,
               25.milliseconds,
               50.milliseconds,
               75.milliseconds,
               100.milliseconds,
               125.milliseconds,
               150.milliseconds,
            )
         }
      }

      test("fail tests that do not complete within the time allowed") {
         val failure = shouldFail {
            testEventually(150.milliseconds) {
               throw RuntimeException("foo")
            }
         }
         failure.message shouldContain "Block failed after 150ms; attempted 6 time(s)"
         failure.message shouldContain "The first error was caused by: foo"
         failure.message shouldContain "The last error was caused by: foo"
      }

      test("return the result computed inside") {
         val result = testEventually(2.seconds) {
            1
         }
         result.value shouldBe 1
         result.invocationTimes.shouldContainExactly(0.milliseconds)
      }

      test("fail tests that throw unexpected exception types") {
         val start = nonDeterministicTestTimeSource().markNow()
         val config = eventuallyConfig {
            duration = 5.seconds
            expectedExceptions = setOf(IOException::class)
         }

         val failure = shouldFail {
            testEventually(config) {
               throw ArrayIndexOutOfBoundsException()
            }
         }
         failure.message shouldContain "Block failed after 5s; attempted 200 time(s)"
         failure.message shouldContain "The first error was caused by: \njava.lang.ArrayIndexOutOfBoundsException"
         failure.message shouldContain "The last error was caused by: \njava.lang.ArrayIndexOutOfBoundsException"
         start.elapsedNow() shouldBe config.duration
      }

      test("pass tests that throws FileNotFoundException for some time") {
         val start = nonDeterministicTestTimeSource().markNow()
         val end = start.plus(500.milliseconds)
         val result = testEventually(5.days) {
            if (end.hasNotPassedNow())
               throw FileNotFoundException("foo")
         }
         result.invocationTimes shouldHaveSize 21
         result.invocationTimes.shouldForAll { it > 500.milliseconds }
      }

      test("handle kotlin assertion errors") {
         var thrown = false
         testEventually(400.milliseconds) {
            if (!thrown) {
               thrown = true
               throw AssertionError("boom")
            }
         }
      }

      test("handle java assertion errors") {
         var thrown = false
         testEventually(400.milliseconds) {
            if (!thrown) {
               thrown = true
               throw java.lang.AssertionError("boom")
            }
         }
      }

      test("do not retry after OutOfMemoryError") {
         var count = 0
         val thrown = shouldThrow<Error> {
            testEventually(1.seconds) {
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
            testEventually(1.seconds) {
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
         val message = shouldFail {
            testEventually(400.milliseconds) {
               if (count++ == 0) {
                  fail("first")
               } else {
                  fail("last")
               }
            }
         }.message
         message shouldContain "Block failed after 400ms; attempted $count time(s)"
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
            testEventually(config) {
               if (count++ == 0) {
                  fail("first")
               } else {
                  error("last")
               }
            }
         }.message
         count shouldBe 2
         message shouldContain "Block failed after 25ms; attempted $count time(s)"
         message shouldContain "The first error was caused by: first"
         message shouldContain "The last error was caused by: last"
      }

      test("allow suspendable functions") {
         val start = nonDeterministicTestTimeSource().markNow()
         testEventually(100.milliseconds) {
            delay(47.milliseconds)
         }
         start.elapsedNow() shouldBe 47.milliseconds
      }

      test("allow configuring interval delay") {
         var count = 0
         val config = eventuallyConfig {
            duration = 250.milliseconds
            interval = 100.milliseconds
         }
         testEventually(config) {
            count += 1
         }
         count.shouldBeLessThan(3)
      }

      test("handle shouldNotBeNull") {
         val start = nonDeterministicTestTimeSource().markNow()
         val failure = shouldFail {
            testEventually(50.milliseconds) {
               val str: String? = null
               str.shouldNotBeNull()
            }
         }
         failure.message shouldContain "Block failed after 50ms; attempted 2 time(s)"
         failure.message shouldContain "The first error was caused by: Expected value to not be null, but was null."
         failure.message shouldContain "The last error was caused by: Expected value to not be null, but was null."
         start.elapsedNow() shouldBe 50.milliseconds
      }

      test("support fibonacci interval functions") {
         val start = nonDeterministicTestTimeSource().markNow()
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
         testEventually(config) {
            t += "x"
            t shouldBe "xxxxxx"
         }
         invocations.shouldContainExactly(
            0.milliseconds,
            25.milliseconds,
            50.milliseconds,
            100.milliseconds,
            175.milliseconds,
         )
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

         testEventually(5.seconds) {
            5
         }

         testEventually(5.seconds) {
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
            testEventually(config) {
               1 shouldBe 2
            }
         }
         failure.message shouldContain "Block failed after 50ms; attempted 2 time(s)"
      }

      test("override assertion to hard assertion before executing assertion and reset it after executing") {
         val start = nonDeterministicTestTimeSource().markNow()
         val target = start.plus(150.milliseconds)
         val failure = shouldFail {
            assertSoftly {
               withClue("Eventually which should pass") {
                  testEventually(2.seconds) {
                     if (target.hasNotPassedNow()) {
                        fail("target has not passed")
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
            testEventually(config) {
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
         testEventually(config) {
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
            testEventually(config) {
               1 shouldBe 2
            }
         }
         failure.message shouldContain "Block failed after 0s; attempted 1 time"
      }

      test("suppress first error") {
         var count = 0
         shouldFail {
            val config = eventuallyConfig {
               duration = 400.milliseconds
               includeFirst = false
            }
            testEventually(config) {
               if (count++ == 0) {
                  fail("first")
               } else {
                  fail("last")
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

      test("when duration is set to default it cannot end test until iteration is done") {
         val finalCount = 100
         var count = 0
         val config = eventuallyConfig {
            retries = finalCount
         }
         shouldThrow<AssertionError> {
            eventually(config) {
               count++
               1 shouldBe 2
            }
         }

         count shouldBe finalCount
      }

      test("test eventually without configuration") {
         // linked to issue #3988
         var count = 0
         eventually {
            count += 1
            count shouldBe 100
         }
         count shouldBe 100
      }
   }
}


private suspend fun <T> testEventually(
   duration: Duration,
   test: suspend () -> T,
): TestEventuallyResult<T> {
   val start = nonDeterministicTestTimeSource().markNow()
   val invocationTimes = mutableListOf<Duration>()

   val value: T = eventually(duration) {
      invocationTimes += start.elapsedNow()
      test()
   }

   return TestEventuallyResult(
      invocationTimes = invocationTimes,
      value = value,
   )
}

private suspend fun <T> testEventually(
   config: EventuallyConfiguration,
   test: suspend () -> T,
): TestEventuallyResult<T> {
   val start = nonDeterministicTestTimeSource().markNow()
   val invocationTimes = mutableListOf<Duration>()

   val value: T = eventually(config = config) {
      invocationTimes += start.elapsedNow()
      test()
   }

   return TestEventuallyResult(
      invocationTimes = invocationTimes,
      value = value,
   )
}

private data class TestEventuallyResult<T>(
   val value: T,
   val invocationTimes: List<Duration>,
)
