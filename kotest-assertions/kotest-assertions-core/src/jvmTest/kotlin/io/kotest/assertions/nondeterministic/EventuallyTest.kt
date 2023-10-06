@file:Suppress("BlockingMethodInNonBlockingContext")

package io.kotest.assertions.nondeterministic

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
import io.kotest.common.measureTimeMillisCompat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.delay
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class EventuallyTest : FunSpec() {

   init {

      test("pass working tests") {
         eventually(5.days) {
            System.currentTimeMillis()
         }
      }

      test("should return final state") {
         var k = 0
         val result = eventually(5.days) {
            k++
            k shouldBe 10
         }
         result shouldBe 10
      }

      test("an interval longer than duration should not override duration").config(timeout = 2.seconds) {
         val config = eventuallyConfig {
            duration = 1.seconds
            interval = 100.seconds
         }
         shouldThrowAny {
            eventually(config) {
               1 shouldBe 2
            }
         }
      }

      test("pass tests that completed within the time allowed") {
         val end = System.currentTimeMillis() + 150
         eventually(1.seconds) {
            if (System.currentTimeMillis() < end)
               throw RuntimeException("foo")
         }
      }

      test("fail tests that do not complete within the time allowed") {
         shouldThrow<AssertionError> {
            eventually(150.milliseconds) {
               throw RuntimeException("foo")
            }
         }
      }

      test("return the result computed inside") {
         val result = eventually(2.seconds) {
            1
         }
         result shouldBe 1
      }

      test("pass tests that completed within the time allowed, AssertionError") {
         val end = System.currentTimeMillis() + 150
         eventually(5.days) {
            if (System.currentTimeMillis() < end)
               assert(false)
         }
      }

      test("pass tests that completed within the time allowed, custom exception") {

         val config = eventuallyConfig {
            duration = 5.seconds
            expectedExceptions = setOf(FileNotFoundException::class)
         }

         val end = System.currentTimeMillis() + 150
         eventually(config) {
            if (System.currentTimeMillis() < end)
               throw FileNotFoundException()
         }
      }

      test("fail tests that throw unexpected exception types") {

         val config = eventuallyConfig {
            duration = 5.seconds
            expectedExceptions = setOf(IOException::class)
         }

         shouldThrowAny {
            eventually(config) {
               throw ArrayIndexOutOfBoundsException()
            }
         }
      }

      test("pass tests that throws FileNotFoundException for some time") {
         val end = System.currentTimeMillis() + 500
         eventually(5.days) {
            if (System.currentTimeMillis() < end)
               throw FileNotFoundException("foo")
         }
      }

      test("handle kotlin assertion errors") {
         var thrown = false
         eventually(100.milliseconds) {
            if (!thrown) {
               thrown = true
               throw AssertionError("boom")
            }
         }
      }

      test("handle java assertion errors") {
         var thrown = false
         eventually(100.milliseconds) {
            if (!thrown) {
               thrown = true
               throw java.lang.AssertionError("boom")
            }
         }
      }

      test("display the first and last underlying failures") {
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
         message.shouldContain("Block failed after 100ms; attempted \\d+ time\\(s\\)".toRegex())
         message.shouldContain("The first error was caused by: first")
         message.shouldContain("The last error was caused by: last")
      }

      test("allow suspendable functions") {
         eventually(100.milliseconds) {
            delay(1)
            System.currentTimeMillis()
         }
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
         val duration = measureTimeMillisCompat {
            shouldThrow<java.lang.AssertionError> {
               eventually(50.milliseconds) {
                  val str: String? = null
                  str.shouldNotBeNull()
               }
            }
         }
         duration.shouldBeGreaterThanOrEqual(50)
      }

      test("support fibonacci interval functions") {
         val latch = CountDownLatch(5)
         val config = eventuallyConfig {
            duration = 2.seconds
            intervalFn = 25.milliseconds.fibonacci()
            listener = object : EventuallyListener {
               override suspend fun invoke(iteration: Int, error: Throwable) {
                  latch.countDown()
               }
            }
         }
         var t = ""
         eventually(config) {
            t += "x"
            t shouldBe "xxxxxx"
         }
         latch.await(2, TimeUnit.SECONDS) shouldBe true
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
         val message = shouldThrow<AssertionError> {
            eventually(config) {
               1 shouldBe 2
            }
         }.message

         message.shouldContain("Block failed after 5s")
         message.shouldContain("attempted 2 time(s)")
      }

      test("override assertion to hard assertion before executing assertion and reset it after executing") {
         val target = System.currentTimeMillis() + 150
         val message = shouldThrow<AssertionError> {
            assertSoftly {
               withClue("Eventually which should pass") {
                  eventually(2.seconds) {
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
            Pair(java.lang.RuntimeException::class, java.lang.RuntimeException())
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

      test("short circuit happy path") {
         shouldThrow<ShortCircuitControlException> {
            val config = eventuallyConfig {
               duration = 5.seconds
               shortCircuit = { true }
            }
            eventually(config) {
               1 shouldBe 2
            }
         }
      }

      test("short circuit exception cannot be suppressed") {
         shouldThrow<ShortCircuitControlException> {
            val config = eventuallyConfig {
               duration = 5.seconds
               expectedExceptions = setOf(ShortCircuitControlException::class)
               shortCircuit = { true }
            }
            eventually(config) {
               1 shouldBe 2
            }
         }
      }
   }
}
