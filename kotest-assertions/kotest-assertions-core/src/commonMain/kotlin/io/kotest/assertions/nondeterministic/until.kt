package io.kotest.assertions.nondeterministic

import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Runs a function [test] until it returns true, as long as the specified duration hasn't passed.
 *
 * To supply more options to until, use the overload that accepts an [UntilConfiguration].
 */
suspend fun until(
   duration: Duration,
   test: suspend () -> Boolean,
) {
   val config = untilConfig { this.duration = duration }
   until(config, test)
}

/**
 * Runs a function [test] until it returns true, using the supplied [config].
 */
suspend fun until(
   config: UntilConfiguration,
   test: suspend () -> Boolean,
) {
   val eventuallyConfiguration = EventuallyConfiguration(
      duration = config.duration,
      initialDelay = config.initialDelay,
      intervalFn = config.intervalFn,
      retries = config.retries,
      expectedExceptionsFn = config.expectedExceptionsFn,
      listener = object : EventuallyListener {
         override suspend fun invoke(iteration: Int, error: Throwable) {
            config.listener.invoke(iteration, error)
         }
      },
      shortCircuit = config.shortCircuit
   )
   eventually(eventuallyConfiguration) { test() shouldBe true }
}

fun untilConfig(
   configure: UntilConfigurationBuilder.() -> Unit,
): UntilConfiguration {
   val config = UntilConfigurationBuilder()
   config.configure()
   return config.build()
}

private fun UntilConfigurationBuilder.build(): UntilConfiguration {
   return UntilConfiguration(
      duration = this.duration,
      initialDelay = this.initialDelay,
      intervalFn = this.intervalFn ?: DurationFn { interval },
      retries = this.retries,
      expectedExceptionsFn = { t -> this.expectedExceptions.any { it.isInstance(t) } || this.expectedExceptionsFn(t) },
      listener = this.listener ?: object : UntilListener {
         override suspend fun invoke(iteration: Int, error: Throwable) {}
      },
      shortCircuit = this.shortCircuit,
   )
}

data class UntilConfiguration(
   val duration: Duration,
   val initialDelay: Duration,
   val intervalFn: DurationFn,
   val retries: Int,
   val expectedExceptionsFn: (Throwable) -> Boolean,
   val listener: UntilListener,
   val shortCircuit: (Throwable) -> Boolean,
)

class UntilConfigurationBuilder {

   /**
    * The total time that the test function can take to complete successfully.
    */
   var duration: Duration = 5.seconds

   /**
    * A delay that is applied before the first invocation of the test function.
    */
   var initialDelay: Duration = Duration.ZERO

   /**
    * The delay between invocations. This delay is overriden by the [intervalFn] if that is not null.
    */
   var interval: Duration = 25.milliseconds

   /**
    * A function that is invoked to calculate the next interval. This if this null, then the
    * fixed value of [interval] is used.
    *
    * This function can be used to implement [fibonacci] or [exponential] backoffs.
    */
   var intervalFn: DurationFn? = null

   /**
    * The maximum number of invocations regardless of durations. By default this is set to [Int.MAX_VALUE].
    */
   var retries: Int = Int.MAX_VALUE

   /**
    * A set of exceptions, which if thrown, are not considered as failures.
    * By default, all exceptions are considered failing cases.
    *
    * This set is applied in addition to the values specified by [expectedExceptionsFn].
    */
   var expectedExceptions: Set<KClass<out Throwable>> = emptySet()

   /**
    * A function that is invoked to determine if a thrown exception should be ignored.
    * By default, this function returns false for all exceptions, or in other words, no
    * exception is ignored.
    *
    * This function is applied in addition to the values specified by [expectedExceptions].
    */
   var expectedExceptionsFn: (Throwable) -> Boolean = { true }

   /**
    * A listener that is invoked after each failed invocation, with the iteration count,
    * and the failing cause.
    */
   var listener: UntilListener? = null

   /**
    * A function that is invoked after each failed invocation which causes no further
    * invocations, but instead immediately fails the eventually function.
    *
    * This is useful for unrecoverable failures, where retrying would not have any effect.
    */
   var shortCircuit: (Throwable) -> Boolean = { false }
}

typealias UntilListener = suspend (Int, Throwable) -> Unit
