@file:Suppress("RETURN_VALUE_NOT_USED_COERCION", "RETURN_VALUE_NOT_USED")

package io.kotest.assertions.nondeterministic

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class EventuallyDslTest : FunSpec() {

   init {

      test("within sets the duration and returns a builder") {
         val builder = within(2.seconds)
         builder.duration shouldBe 2.seconds
      }

      test("within is the entry point for chaining") {
         val builder = within(2.seconds).checkingEvery(100.milliseconds)
         builder.duration shouldBe 2.seconds
         builder.interval shouldBe 100.milliseconds
      }

      test("checkingEvery sets the interval") {
         val builder = EventuallyConfigurationBuilder().checkingEvery(50.milliseconds)
         builder.interval shouldBe 50.milliseconds
      }

      test("checkingEvery sets the intervalFn") {
         val fn = DurationFn { 75.milliseconds }
         val builder = EventuallyConfigurationBuilder().checkingEvery(fn)
         builder.intervalFn shouldBe fn
      }

      test("withInitialDelay sets the initial delay") {
         val builder = EventuallyConfigurationBuilder().withInitialDelay(30.milliseconds)
         builder.initialDelay shouldBe 30.milliseconds
      }

      test("withRetries sets the retries") {
         val builder = EventuallyConfigurationBuilder().withRetries(7)
         builder.retries shouldBe 7
      }

      test("retryingOn sets the expected exceptions") {
         val builder = EventuallyConfigurationBuilder().retryingOn(IOException::class, IllegalStateException::class)
         builder.expectedExceptions shouldBe setOf(IOException::class, IllegalStateException::class)
      }

      test("retryingOn sets the expected exceptions predicate") {
         val predicate: (Throwable) -> Boolean = { it is IOException }
         val builder = EventuallyConfigurationBuilder().retryingOn(predicate)
         builder.expectedExceptionsFn shouldBe predicate
      }

      test("withListener sets the listener") {
         val listener: EventuallyListener = { _, _ -> }
         val builder = EventuallyConfigurationBuilder().withListener(listener)
         builder.listener shouldBe listener
      }

      test("shortCircuitOn sets the short circuit predicate") {
         val predicate: (Throwable) -> Boolean = { it is IOException }
         val builder = EventuallyConfigurationBuilder().shortCircuitOn(predicate)
         builder.shortCircuit shouldBe predicate
      }

      test("includingFirstError sets the includeFirst flag") {
         val builder = EventuallyConfigurationBuilder().includingFirstError(false)
         builder.includeFirst shouldBe false
      }

      test("all builder functions can be chained from within") {
         val builder = within(2.seconds)
            .withInitialDelay(10.milliseconds)
            .checkingEvery(100.milliseconds)
            .withRetries(5)
            .retryingOn(IOException::class)
            .includingFirstError(false)

         builder.duration shouldBe 2.seconds
         builder.initialDelay shouldBe 10.milliseconds
         builder.interval shouldBe 100.milliseconds
         builder.retries shouldBe 5
         builder.expectedExceptions shouldBe setOf(IOException::class)
         builder.includeFirst shouldBe false
      }

      test("eventually accepts a builder and runs the test until it passes") {
         var count = 0
         eventually(within(2.seconds).checkingEvery(10.milliseconds)) {
            count += 1
            count shouldBe 3
         }
         count shouldBe 3
      }

      test("eventually with a builder respects the configured duration") {
         var count = 0
         shouldThrow<AssertionError> {
            eventually(within(100.milliseconds).checkingEvery(20.milliseconds)) {
               count += 1
               error("never passes")
            }
         }
         count shouldBeLessThan 100
      }
   }
}
