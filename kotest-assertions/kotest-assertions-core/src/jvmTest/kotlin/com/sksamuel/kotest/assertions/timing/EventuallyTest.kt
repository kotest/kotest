@file:Suppress("DEPRECATION")
@file:OptIn(ExperimentalCoroutinesApi::class)

package com.sksamuel.kotest.assertions.timing

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.timing.EventuallyConfig
import io.kotest.assertions.timing.EventuallyState
import io.kotest.assertions.timing.eventually
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.until.fixed
import io.kotest.assertions.withClue
import io.kotest.common.nonConstantTrue
import io.kotest.common.testTimeSource
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@OptIn(DelicateCoroutinesApi::class)
class EventuallyTest : WordSpec() {

   init {
      coroutineTestScope = true

      "eventually" should {
         "pass working tests" {
            eventually(5.days) {
               nonConstantTrue() shouldBe true
            }
         }
         "pass tests that completed within the time allowed" {
            val start = testTimeSource().markNow()
            eventually(1.seconds) {
               if (start.elapsedNow() < 150.milliseconds)
                  throw RuntimeException("foo")
            }
         }
         "fail tests that do not complete within the time allowed" {
            shouldThrow<AssertionError> {
               eventually(150.milliseconds) {
                  throw RuntimeException("foo")
               }
            }
         }
         "return the result computed inside" {
            val result = eventually(2.seconds) {
               1
            }
            result shouldBe 1
         }
         "pass tests that completed within the time allowed, AssertionError" {
            val start = testTimeSource().markNow()
            eventually(5.days) {
               if (start.elapsedNow() < 150.milliseconds)
                  assert(false)
            }
         }
         "pass tests that completed within the time allowed, custom exception" {
            val start = testTimeSource().markNow()
            eventually(5.seconds, FileNotFoundException::class) {
               if (start.elapsedNow() < 150.milliseconds)
                  throw FileNotFoundException()
            }
         }
         "fail tests throw unexpected exception type" {
            shouldThrow<NullPointerException> {
               eventually(2.seconds, exceptionClass = IOException::class) {
                  (null as String?)!!.length
               }
            }
         }
         "pass tests that throws FileNotFoundException for some time" {
            val start = testTimeSource().markNow()
            eventually(5.days) {
               if (start.elapsedNow() < 150.milliseconds)
                  throw FileNotFoundException("foo")
            }
         }
         "handle kotlin assertion errors" {
            var thrown = false
            eventually(100.milliseconds) {
               if (!thrown) {
                  thrown = true
                  throw AssertionError("boom")
               }
            }
         }
         "handle java assertion errors" {
            var thrown = false
            eventually(100.milliseconds) {
               if (!thrown) {
                  thrown = true
                  throw java.lang.AssertionError("boom")
               }
            }
         }
         "display the first and last underlying failures" {
            var count = 0
            val message = shouldThrow<AssertionError> {
               eventually(100.milliseconds) {
                  if (count == 0) {
                     count = 1
                     fail("first")
                  } else {
                     fail("last")
                  }
               }
            }.message
            message.shouldContain("Eventually block failed after 100ms; attempted \\d+ time\\(s\\); FixedInterval\\(duration=25ms\\) delay between attempts".toRegex())
            message.shouldContain("The first error was caused by: first")
            message.shouldContain("The last error was caused by: last")
         }
         "allow suspendable functions" {
            eventually(100.milliseconds) {
               delay(1)
               nonConstantTrue() shouldBe true
            }
         }
         "allow configuring interval delay" {
            var count = 0
            eventually(50.milliseconds, 20.milliseconds.fixed()) {
               count += 1
            }
            count.shouldBeLessThan(3)
         }
         "do one final iteration if we never executed before interval expired" {
            val counter = AtomicInteger(0)

            eventually(1.seconds, 400.milliseconds) {
               delay(500.milliseconds)
               // Although this iteration takes longer than the interval, it will be allowed to complete.
               counter.incrementAndGet()
            }

            counter.get().shouldBe(1)
         }
         "do one final iteration if we only executed once and the last delay > interval" {
            val counter = AtomicInteger(0)

            eventually(3.seconds, 400.milliseconds) {
               counter.incrementAndGet() shouldBe 2
               delay(600.milliseconds)
               // Although the first iteration takes longer than the interval, another iteration is allowed.
            }

            counter.get().shouldBe(2)
         }
         "handle shouldNotBeNull" {
            val duration = testTimeSource().measureTime {
               shouldThrow<java.lang.AssertionError> {
                  eventually(50.milliseconds) {
                     val str: String? = null
                     str.shouldNotBeNull()
                  }
               }
            }
            duration shouldBe 50.milliseconds
         }

         "eventually with boolean predicate" {
            eventually(5.seconds) {
               nonConstantTrue() shouldBe true
            }
         }

         "eventually with boolean predicate and interval" {
            eventually(5.seconds, 1.seconds.fixed()) {
               nonConstantTrue() shouldBe true
            }
         }

         "eventually with T predicate" {
            var t = ""
            eventually(5.seconds, predicate = { t == "xxxx" }) {
               t += "x"
            }
         }

         "eventually with T predicate and interval" {
            var t = ""
            val result =
               eventually(5.seconds, 1.milliseconds.fixed(), predicate = { t == "xxxxxxxxxxx" }) {
                  t += "x"
                  t
               }
            result shouldBe "xxxxxxxxxxx"
         }

         "eventually with T predicate, interval, and listener" {
            var t = ""
            val latch = CountDownLatch(5)
            val result = eventually(
               5.seconds,
               1.milliseconds.fixed(),
               predicate = { t == "xxxxxxxxxxx" },
               listener = { latch.countDown() },
            ) {
               t += "x"
               t
            }
            latch.await(15, TimeUnit.SECONDS) shouldBe true
            result shouldBe "xxxxxxxxxxx"
         }

         "fail tests that fail a predicate for the duration" {
            shouldThrow<AssertionError> {
               eventually(75.milliseconds, predicate = { it == 2 }) {
                  1
               }
            }
         }

         "support fibonacci intervals" {
            var t = ""
            val latch = CountDownLatch(5)
            val result = eventually(
               duration = 10.seconds,
               interval = 1.milliseconds.fibonacci(),
               predicate = { t == "xxxxxx" },
               listener = { latch.countDown() },
            ) {
               t += "x"
               t
            }
            latch.await(10, TimeUnit.SECONDS) shouldBe true
            result shouldBe "xxxxxx"
         }

         "eventually has a shareable configuration" {
            val slow = EventuallyConfig(duration = 5.seconds)

            var i = 0
            val fast = slow.copy(retries = 1)

            assertSoftly {
               slow.retries shouldBe Int.MAX_VALUE
               fast.retries shouldBe 1
               slow.duration shouldBe 5.seconds
               fast.duration shouldBe 5.seconds
            }

            eventually(slow) {
               5
            }

            eventually(fast, predicate = { i == 1 }) {
               i++
            }

            i shouldBe 1
         }

         "throws if retry limit is exceeded" {
            val message = shouldThrow<AssertionError> {
               eventually(EventuallyConfig(retries = 2)) {
                  1 shouldBe 2
               }
            }.message

            message.shouldContain("Eventually block failed after Infinity")
            message.shouldContain("attempted 2 time(s)")
         }

         "override assertion to hard assertion before executing assertion and reset it after executing" {
            val start = testTimeSource().markNow()
            val message = shouldThrow<AssertionError> {
               assertSoftly {
                  withClue("Eventually which should pass") {
                     eventually(2.seconds) {
                        start.elapsedNow() shouldBeGreaterThan 150.milliseconds
                     }
                  }
                  withClue("1 should never be 2") {
                     1 shouldBe 2
                  }
                  withClue("2 should never be 3") {
                     2 shouldBe 3
                  }
               }
            }.message

            message shouldContain "1) 1 should never be 2"
            message shouldContain "2) 2 should never be 3"
         }

         "call the listener when an exception is thrown in the producer function" {
            var state: EventuallyState<Int>? = null

            shouldThrow<Throwable> {
               eventually(retries = 1, listener = {
                  if (state == null) {
                     state = it
                  }
               }) {
                  withClue("1 should never be 2") { 1 shouldBe 2 }
               }
            }

            state.shouldNotBeNull()
            state?.result.shouldBeNull()

            state?.firstError?.message shouldContain "1 should never be 2"
         }
      }
   }
}
