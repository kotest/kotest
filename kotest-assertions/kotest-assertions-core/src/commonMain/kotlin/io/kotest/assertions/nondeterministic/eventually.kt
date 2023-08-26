package io.kotest.assertions.nondeterministic

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Runs a function [test] until it doesn't throw as long as the specified duration hasn't passed.
 *
 * To supply more options to eventually, use the overload that accepts an [EventuallyConfiguration].
 */
suspend fun <T> eventually(
   duration: Duration,
   test: suspend () -> T,
): T {
   val config = eventuallyConfig { this.duration = duration }
   return eventually(config, test)
}

/**
 * Runs a function [test] until it doesn't throw, using the supplied [config].
 */
suspend fun <T> eventually(
   config: EventuallyConfiguration,
   test: suspend () -> T,
): T {

   delay(config.initialDelay)

   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   val control = EventuallyControl(config)

   try {
      while (control.hasAttemptsRemaining()) {
         try {
            return test()
         } catch (e: Throwable) {
            val notSuppressible = control.exceptionIsNotSuppressible(e)
            config.listener.invoke(control.iterations + 1, e)
            if (config.shortCircuit.invoke(e)) {
               throw ShortCircuitControlException
            }
            if (notSuppressible) {
               throw e
            }
         }

         control.step()
      }
   } finally {
      errorCollector.setCollectionMode(originalAssertionMode)
   }

   throw failure(control.buildFailureMessage())
}

fun eventuallyConfig(
   configure: EventuallyConfigurationBuilder.() -> Unit,
): EventuallyConfiguration {
   val config = EventuallyConfigurationBuilder()
   config.configure()
   return config.build()
}

private fun EventuallyConfigurationBuilder.build(): EventuallyConfiguration {
   return EventuallyConfiguration(
      duration = this.duration,
      initialDelay = this.initialDelay,
      intervalFn = this.intervalFn ?: DurationFn { interval },
      retries = this.retries,
      expectedExceptionsFn = { t -> this.expectedExceptions.any { it.isInstance(t) } || this.expectedExceptionsFn(t) },
      listener = this.listener ?: NoopEventuallyListener,
      shortCircuit = this.shortCircuit,
   )
}

data class EventuallyConfiguration(
   val duration: Duration,
   val initialDelay: Duration,
   val intervalFn: DurationFn,
   val retries: Int,
   val expectedExceptionsFn: (Throwable) -> Boolean,
   val listener: EventuallyListener,
   val shortCircuit: (Throwable) -> Boolean,
)

class EventuallyConfigurationBuilder {

   /**
    * The total time that the eventually function can take to complete successfully.
    */
   var duration: Duration = Duration.INFINITE

   /**
    * A delay that is applied before the first invocation of the eventually function.
    */
   var initialDelay: Duration = Duration.ZERO

   /**
    * The delay between invocations. This delay is overriden by the [intervalFn] if it is not null.
    */
   var interval: Duration = 25.milliseconds

   /**
    * A function that is invoked to calculate the next interval. This if this null, then the
    * value of [interval] is used.
    *
    * This function can be used to implement [fibonacci] or [exponential] backoffs.
    */
   var intervalFn: DurationFn? = null

   /**
    * The maximum number of invocations regardless of durations. By default this is set to max retries.
    */
   var retries: Int = Int.MAX_VALUE

   /**
    * A set of exceptions, which if thrown, will cause the test function to be retried.
    * By default, all exceptions are retried.
    *
    * This set is applied in addition to the values specified by [expectedExceptionsFn].
    */
   var expectedExceptions: Set<KClass<out Throwable>> = emptySet()

   /**
    * A function that is invoked to determine if a thrown exception is expected and the test
    * function retried. By default, this function returns true for all exceptions, or in other words,
    * all errors cause the test function to be retried.
    *
    * This function is applied in addition to the values specified by [expectedExceptions].
    */
   var expectedExceptionsFn: (Throwable) -> Boolean = { true }

   /**
    * A listener that is invoked after each failed invocation, with the iteration count,
    * and the failing cause.
    */
   var listener: EventuallyListener? = null

   /**
    * A function that is invoked after each failed invocation which causes no further
    * invocations, but instead immediately fails the eventually function.
    *
    * This is useful for unrecoverable failures, where retrying would not have any effect.
    */
   var shortCircuit: (Throwable) -> Boolean = { false }
}

typealias EventuallyListener = suspend (Int, Throwable) -> Unit

object NoopEventuallyListener : EventuallyListener {
   override suspend fun invoke(iteration: Int, error: Throwable) {}
}

private class EventuallyControl(val config: EventuallyConfiguration) {

   val start = timeInMillis()
   val end = start + config.duration.inWholeMilliseconds

   var iterations = 0

   var firstError: Throwable? = null
   var lastError: Throwable? = null

   var lastDelayPeriod: Duration = Duration.ZERO
   var lastInterval: Duration = Duration.ZERO

   /**
    * Returns true if this throwable is not one we can ignore.
    */
   fun exceptionIsNotSuppressible(e: Throwable): Boolean {

      if (firstError == null) {
         firstError = e
      } else {
         lastError = e
      }

      // cannot ignore any control exceptions
      if (ShortCircuitControlException::class.isInstance(e)) {
         return true
      }

      return !config.expectedExceptionsFn(e)
   }

   suspend fun step() {
      lastInterval = config.intervalFn.next(++iterations)
      val delayMark = timeInMillis()
      // cap the interval at remaining time
      delay(min(lastInterval.inWholeMilliseconds, end - delayMark))
      lastDelayPeriod = (timeInMillis() - delayMark).milliseconds
   }

   fun hasAttemptsRemaining() = timeInMillis() < end && iterations < config.retries

   fun buildFailureMessage() = StringBuilder().apply {
      appendLine("Block failed after ${config.duration}; attempted $iterations time(s)")

      firstError?.run {
         appendLine("The first error was caused by: ${this.message}")
         appendLine(this.stackTraceToString())
      }

      lastError?.run {
         appendLine("The last error was caused by: ${this.message}")
         appendLine(this.stackTraceToString())
      }
   }.toString()
}

internal object ShortCircuitControlException : Throwable()
