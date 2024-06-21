@file:Suppress(
   "DEPRECATION",
   "BlockingMethodInNonBlockingContext",
)

package io.kotest.framework.concurrency

import io.kotest.assertions.all
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@ExperimentalKotest
@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class EventuallySpec : FunSpec({

   test("eventually should immediately pass working tests") {
      eventually(5.seconds) {
         System.currentTimeMillis()
      }
   }

   test("eventually passes tests that complete within the time allowed") {
      val end = System.currentTimeMillis() + 250L
      eventually(5.seconds) {
         if (System.currentTimeMillis() < end)
            1 shouldBe 2
      }
   }

   test("eventually fails tests that do not complete within the time allowed") {
      shouldThrow<RuntimeException> {
         eventually(150L) {
            throw RuntimeException("foo")
         }
      }
   }

   test("eventually returns the result computed inside") {
      val result = eventually(2.seconds) {
         1
      }
      result shouldBe 1
   }

   test("eventually passes tests that completed within the time allowed, AssertionError") {
      val end = System.currentTimeMillis() + 250
      eventually(5.seconds) {
         if (System.currentTimeMillis() < end)
            assert(false)
      }
   }

   test("eventually fails tests that throw unexpected exception type") {
      shouldThrow<NullPointerException> {
         eventually({
            duration(2.seconds)
            suppressExceptions = setOf(IOException::class)
         }) {
            (null as String?)!!.length
         }
      }
   }

   test("eventually passes tests that throws FileNotFoundException for some time") {
      val end = System.currentTimeMillis() + 250
      eventually({
         duration(5.seconds)
         suppressExceptions = setOf(FileNotFoundException::class)
      }) {
         if (System.currentTimeMillis() < end)
            throw FileNotFoundException("foo")
      }
   }

   test("eventually handles kotlin assertion errors") {
      var thrown = false
      eventually(400.milliseconds) {
         if (!thrown) {
            thrown = true
            throw AssertionError("boom")
         }
      }
   }

   test("eventually handles java assertion errors") {
      var thrown = false
      eventually(400.milliseconds) {
         if (!thrown) {
            thrown = true
            throw java.lang.AssertionError("boom")
         }
      }
   }

   test("eventually displays the first and last underlying failures") {
      var count = 0
      val message = shouldThrow<AssertionError> {
         eventually(400.milliseconds) {
            if (count == 0) {
               count = 1
               fail("first")
            } else {
               fail("last")
            }
         }
      }.message

      // TODO: add this assertion when we can use kotlin.time again
//         message.shouldContain("Eventually block failed after 400ms; attempted \\d+ time\\(s\\); FixedInterval\\(duration=25.0ms\\) delay between attempts".toRegex())
      message.shouldContain("The first error was caused by: first")
      message.shouldContain("The last error was caused by: last")
   }

   test("eventually allows suspendable functions") {
      eventually(400.milliseconds) {
         delay(25)
         System.currentTimeMillis()
      }
   }

   test("eventually allows configuring interval delay") {
      var count = 0
      eventually({
         duration(400.milliseconds)
         interval = 80.milliseconds.fixed()
      }) {
         count += 1
      }
      count.shouldBeLessThan(6)
   }

   test("eventually does one final iteration if we never executed before interval expired") {
      newSingleThreadContext("single").use { dispatcher ->
         launch(dispatcher) {
            Thread.sleep(2000)
         }
         val counter = AtomicInteger(0)
         withContext(dispatcher) {
            // we won't be able to run in here
            eventually({
               duration(1.seconds)
               interval = 400.milliseconds.fixed()
            }) {
               counter.incrementAndGet()
            }
         }
         counter.get().shouldBe(1)
      }
   }

   test("eventually does one final iteration if we only executed once and the last delay > interval") {
      newSingleThreadContext("single").use { dispatcher ->
         // this will start immediately, free the dispatcher to allow eventually to run once, then block the thread
         launch(dispatcher) {
            delay(100.milliseconds)
            Thread.sleep(500)
         }
         val counter = AtomicInteger(0)
         withContext(dispatcher) {
            // this will execute once immediately, then the earlier async will steal the thread
            // and then since the delay has been > interval and times == 1, we will execute once more
            eventually({
               duration(250.milliseconds)
               interval = 25.milliseconds.fixed()
            }) {
               counter.incrementAndGet() shouldBe 2
            }
         }
         counter.get().shouldBe(2)
      }
   }

   test("eventually handles shouldNotBeNull") {
      measureTime {
         shouldThrow<java.lang.AssertionError> {
            eventually(50.milliseconds) {
               val str: String? = null
               str.shouldNotBeNull()
            }
         }
      }.shouldBeGreaterThanOrEqualTo(50.milliseconds)
   }

   test("eventually with boolean predicate") {
      eventually(5.seconds) {
         System.currentTimeMillis() > 0
      }
   }

   test("eventually with boolean predicate and interval") {
      eventually({
         duration(5.seconds)
         interval = 1.seconds.fixed()
         predicate = { System.currentTimeMillis() > 0 }
      }) {}
   }

   test("eventually with T predicate") {
      var t = ""
      eventually<String>({
         duration(5.seconds)
         predicate = { it.result == "xxxxxxxxxxx" }
      }) {
         t += "x"
         t
      }
   }

   test("eventually with T predicate and interval") {
      var t = ""
      val result = eventually({
         duration(5.seconds)
         interval = 250.milliseconds.fixed()
         predicate = { it.result == "xxxxxxxxxxx" }
      }) {
         t += "x"
         t
      }

      result shouldBe "xxxxxxxxxxx"
   }

   test("eventually with T predicate, interval, and listener") {
      var t = ""
      val latch = CountDownLatch(5)
      val result = eventually({
         duration(5.seconds)
         interval = 250.milliseconds.fixed()
         listener = { latch.countDown() }
         predicate = { it.result == "xxxxxxxxxxx" }
      }) {
         t += "x"
         t
      }

      latch.await(15, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxxxxxxxx"
   }

   test("eventually with T predicate, listener, and shortCircuit") {
      var t = ""
      val message = shouldThrow<EventuallyShortCircuitException> {
         eventually({
            duration(5.seconds)
            interval = 250.milliseconds.fixed()
            shortCircuit = { it.result == "xx" }
            predicate = { it.result == "xxxxxxxxxxx" }
         }) {
            t += "x"
            t
         }
      }.message

      all(message) {
         this.shouldContain("The provided shortCircuit function caused eventually to exit early")
         this.shouldContain("EventuallyState(result=xx")
      }
   }

   test("eventually fails tests that fail a predicate") {
      shouldThrow<AssertionError> {
         eventually({
            duration(1.seconds)
            predicate = { it.result == 2 }
         }) {
            1
         }
      }
   }

   test("eventually supports fibonacci intervals") {
      var t = ""
      val latch = CountDownLatch(5)

      val result = eventually({
         duration(10.seconds)
         interval = 200.milliseconds.fibonacci()
         predicate = { it.result == "xxxxxx" }
         listener = { latch.countDown() }
      }) {
         t += "x"
         t
      }

      latch.await(10, TimeUnit.SECONDS) shouldBe true
      result shouldBe "xxxxxx"
   }

   fun <T> slow() = EventuallyConfig<T>(interval = 250.seconds.fixed())

   test("eventually can accept shareable configuration with Unit as the type and overrides") {
      val a = eventually(slow()) {
         5
      }

      a shouldBe 5

      val b = eventually(slow(), {
         predicate = { it.result == "hi" }
      }) {
         "hi"
      }

      b shouldBe "hi"
   }


   test("eventually throws if retry limit is exceeded") {
      val message = shouldThrow<AssertionError> {
         eventually({
            duration = 100000
            retries = 2
         }) {
            1 shouldBe 2
         }
      }.message

      // TODO: re-enable this when we have kotlin.time again
//         message.shouldContain("Eventually block failed after Infinity")
      message.shouldContain("attempted 2 time(s)")
   }

   test("eventually overrides assertion to hard assertion before executing assertion and reset it after executing") {
      val target = System.currentTimeMillis() + 1000
      val message = shouldThrow<AssertionError> {
         all {
            withClue("Eventually that should pass") {
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

   test("eventually calls the listener when an exception is thrown in the producer function") {
      var state: EventuallyState<Int>? = null

      shouldThrow<Throwable> {
         eventually({
            duration(250.milliseconds)
            retries = 1
            listener = {
               if (state == null) {
                  state = it
               }
            }
         }) {
            withClue("1 should never be 2") {
               1 shouldBe 2
            }
         }
      }

      state.shouldNotBeNull()
      state?.firstError?.message shouldContain "1 should never be 2"
   }

   test("allows exception if predicate is satisfied") {
      var i = 0
      eventually({
         duration(2.seconds)
         suppressExceptionIf = { it.message == "foo" }
      }) {
         if (i++ < 3) {
            throw AssertionError("foo")
         }
      }
   }

   test("does not allow an exception if predicate is not satisfied") {
      shouldThrow<AssertionError> {
         var i = 0
         eventually({
            duration(2.seconds)
            suppressExceptionIf = { it.message == "bar" }
         }) {
            if (i++ < 3) {
               throw AssertionError("foo")
            }
         }
      }.message shouldBe "foo"
   }

   test("allows a set of exceptions") {
      val exceptions = setOf(
         Pair(FileNotFoundException::class, FileNotFoundException()),
         Pair(AssertionError::class, AssertionError()),
         Pair(java.lang.RuntimeException::class, java.lang.RuntimeException())
      )
      var i = 0
      eventually({
         duration(5.seconds)
         suppressExceptions = exceptions.map { it.first }.toSet()
      }) {
         exceptions.elementAtOrNull(i++)?.run {
            throw this.second
         }
      }

      i shouldBe exceptions.size + 1
   }

   test("short circuit exception cannot be suppressed") {
      shouldThrow<EventuallyShortCircuitException> {
         eventually({
            duration(5.seconds)
            suppressExceptions = setOf(EventuallyShortCircuitException::class)
            shortCircuit = { true }
         }) {
            1 shouldBe 1
         }
      }
   }
})
