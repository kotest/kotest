@file:Suppress("BlockingMethodInNonBlockingContext")

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
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.time.days

class EventuallyTest : WordSpec() {

   init {
      "eventually" should {
         "pass working tests" {
            eventually(Duration.days(5)) {
               System.currentTimeMillis()
            }
         }
         "pass tests that completed within the time allowed" {
            val end = System.currentTimeMillis() + 250
            eventually(Duration.seconds(1)) {
               if (System.currentTimeMillis() < end)
                  throw RuntimeException("foo")
            }
         }
         "fail tests that do not complete within the time allowed" {
            shouldThrow<AssertionError> {
                eventually(Duration.milliseconds(150)) {
                    throw RuntimeException("foo")
                }
            }
         }
         "return the result computed inside" {
            val result = eventually(Duration.seconds(2)) {
               1
            }
            result shouldBe 1
         }
         "pass tests that completed within the time allowed, AssertionError"  {
            val end = System.currentTimeMillis() + 250
            eventually(Duration.days(5)) {
               if (System.currentTimeMillis() < end)
                  assert(false)
            }
         }
         "pass tests that completed within the time allowed, custom exception"  {
            val end = System.currentTimeMillis() + 250
            eventually(Duration.seconds(5), FileNotFoundException::class) {
               if (System.currentTimeMillis() < end)
                  throw FileNotFoundException()
            }
         }
         "fail tests throw unexpected exception type"  {
            shouldThrow<NullPointerException> {
               eventually(Duration.seconds(2), exceptionClass = IOException::class) {
                  (null as String?)!!.length
               }
            }
         }
         "pass tests that throws FileNotFoundException for some time"  {
            val end = System.currentTimeMillis() + 150
            eventually(Duration.days(5)) {
               if (System.currentTimeMillis() < end)
                  throw FileNotFoundException("foo")
            }
         }
         "handle kotlin assertion errors" {
            var thrown = false
             eventually(Duration.milliseconds(100)) {
                 if (!thrown) {
                     thrown = true
                     throw AssertionError("boom")
                 }
             }
         }
         "handle java assertion errors" {
            var thrown = false
             eventually(Duration.milliseconds(100)) {
                 if (!thrown) {
                     thrown = true
                     throw java.lang.AssertionError("boom")
                 }
             }
         }
         "display the first and last underlying failures" {
            var count = 0
            val message = shouldThrow<AssertionError> {
                eventually(Duration.milliseconds(100)) {
                    if (count == 0) {
                        count = 1
                        fail("first")
                    } else {
                        fail("last")
                    }
                }
            }.message
            message.shouldContain("Eventually block failed after 100ms; attempted \\d+ time\\(s\\); FixedInterval\\(duration=25.0ms\\) delay between attempts".toRegex())
            message.shouldContain("The first error was caused by: first")
            message.shouldContain("The last error was caused by: last")
         }
         "allow suspendable functions" {
             eventually(Duration.milliseconds(100)) {
                 delay(25)
                 System.currentTimeMillis()
             }
         }
         "allow configuring interval delay" {
            var count = 0
             eventually(Duration.milliseconds(200), Duration.milliseconds(40).fixed()) {
                 count += 1
             }
            count.shouldBeLessThan(6)
         }
         "do one final iteration if we never executed before interval expired" {
            val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
            launch(dispatcher) {
               Thread.sleep(2000)
            }
            val counter = AtomicInteger(0)
            withContext(dispatcher) {
               // we won't be able to run in here
               eventually(Duration.seconds(1), Duration.milliseconds(100)) {
                  counter.incrementAndGet()
               }
            }
            counter.get().shouldBe(1)
         }
         "do one final iteration if we only executed once and the last delay > interval" {
            val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
            // this will start immediately, free the dispatcher to allow eventually to run once, then block the thread
            launch(dispatcher) {
               delay(Duration.milliseconds(100))
               Thread.sleep(500)
            }
            val counter = AtomicInteger(0)
            withContext(dispatcher) {
               // this will execute once immediately, then the earlier async will steal the thread
               // and then since the delay has been > interval and times == 1, we will execute once more
                eventually(Duration.milliseconds(250), Duration.milliseconds(25)) {
                    counter.incrementAndGet() shouldBe 2
                }
            }
            counter.get().shouldBe(2)
         }
         "handle shouldNotBeNull" {
            val mark = TimeSource.Monotonic.markNow()
            shouldThrow<java.lang.AssertionError> {
                eventually(Duration.milliseconds(50)) {
                    val str: String? = null
                    str.shouldNotBeNull()
                }
            }
            mark.elapsedNow().inWholeMilliseconds.shouldBeGreaterThanOrEqual(50)
         }

         "eventually with boolean predicate" {
            eventually(Duration.seconds(5)) {
               System.currentTimeMillis() > 0
            }
         }

         "eventually with boolean predicate and interval" {
            eventually(Duration.seconds(5), Duration.seconds(1).fixed()) {
               System.currentTimeMillis() > 0
            }
         }

         "eventually with T predicate" {
            var t = ""
            eventually(Duration.seconds(5), predicate = { t == "xxxx" }) {
               t += "x"
            }
         }

         "eventually with T predicate and interval" {
            var t = ""
            val result =
               eventually(Duration.seconds(5), Duration.milliseconds(250).fixed(), predicate = { t == "xxxxxxxxxxx" }) {
                  t += "x"
                  t
               }
            result shouldBe "xxxxxxxxxxx"
         }

         "eventually with T predicate, interval, and listener" {
            var t = ""
            val latch = CountDownLatch(5)
            val result = eventually(
               Duration.seconds(5),
               Duration.milliseconds(250).fixed(),
               predicate = { t == "xxxxxxxxxxx" },
               listener = { latch.countDown() },
            ) {
               t += "x"
               t
            }
            latch.await(15, TimeUnit.SECONDS) shouldBe true
            result shouldBe "xxxxxxxxxxx"
         }

         "fail tests that fail a predicate" {
            shouldThrow<AssertionError> {
               eventually(Duration.seconds(1), predicate = { it == 2 }) {
                  1
               }
            }
         }

         "support fibonacci intervals" {
            var t = ""
            val latch = CountDownLatch(5)
            val result = eventually(
               duration = Duration.seconds(10),
               interval = Duration.milliseconds(200).fibonacci(),
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
            val slow = EventuallyConfig(duration = Duration.seconds(5))

            var i = 0
            val fast = slow.copy(retries = 1)

            assertSoftly {
               slow.retries shouldBe Int.MAX_VALUE
               fast.retries shouldBe 1
               slow.duration shouldBe Duration.seconds(5)
               fast.duration shouldBe Duration.seconds(5)
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
            val target = System.currentTimeMillis() + 1000
            val message = shouldThrow<AssertionError> {
               assertSoftly {
                  withClue("Eventually which should pass") {
                     eventually(Duration.seconds(2)) {
                        System.currentTimeMillis() shouldBeGreaterThan target
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
            var state: EventuallyState<Unit>? = null

            shouldThrow<Throwable> {
               eventually(retries = 1, listener = { if (state == null) { state = it } }) {
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
