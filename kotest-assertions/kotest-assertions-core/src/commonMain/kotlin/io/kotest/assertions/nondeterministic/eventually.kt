package io.kotest.assertions.nondeterministic

import io.kotest.matchers.ErrorCollectionMode
import io.kotest.matchers.errorCollector
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.common.nonDeterministicTestTimeSource
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark

/**
 * Runs a function [test] until it doesn't throw as long as the specified duration hasn't passed.
 *
 * To supply more options to eventually, use the overload that accepts an [EventuallyConfiguration].
 */
suspend fun <T> eventually(
   test: suspend () -> T,
): T {
   val config = eventuallyConfig { }
   return eventually(config, test)
}

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

   val start = nonDeterministicTestTimeSource().markNow()
   val control = EventuallyControl(config, start)

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
   } catch (e: ShortCircuitControlException) {
      // Short-circuited out from retries, will throw below

      // If we terminated due to an exception, we are missing an iteration in the counter
      // since the step function is not invoked when terminating early
      control.iterations++
   } catch (e: Throwable) {
      if(e is Error && e !is AssertionError) {
         throw e
      }
      control.iterations++
   } finally {
      errorCollector.setCollectionMode(originalAssertionMode)
   }

   throw AssertionErrorBuilder.create()
      .withMessage(control.buildFailureMessage())
      .build()
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
      expectedExceptionsFn = { t -> this.expectedExceptions.any { it.isInstance(t) } ||
         (this.expectedExceptions.isEmpty() && this.expectedExceptionsFn(t)) } ,
      listener = this.listener ?: NoopEventuallyListener,
      shortCircuit = this.shortCircuit,
      includeFirst = this.includeFirst,
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
   val includeFirst: Boolean,
){
   init {
      require(duration >= Duration.ZERO) { "Duration must be greater than or equal to 0, but was $duration" }
      require(retries >= 0) { "Retries must be greater than or equal to 0, but was $retries" }
   }
}

internal object EventuallyConfigurationDefaults {
   val duration: Duration = Duration.INFINITE
   val initialDelay: Duration = Duration.ZERO
   val interval: Duration = 25.milliseconds
   val intervalFn: DurationFn? = null
   val retries: Int = Int.MAX_VALUE
   val expectedExceptions: Set<KClass<out Throwable>> = emptySet()
   val expectedExceptionsFn: (Throwable) -> Boolean = { true }
   val listener: EventuallyListener? = null
   val shortCircuit: (Throwable) -> Boolean = { false }
   val includeFirst: Boolean = true
}

class EventuallyConfigurationBuilder {

   /**
    * The total time that the [eventually] function can take to complete successfully. Must be non-negative.
    */
   var duration: Duration = EventuallyConfigurationDefaults.duration

   /**
    * A delay that is applied before the first invocation of the [eventually] function.
    */
   var initialDelay: Duration = EventuallyConfigurationDefaults.initialDelay

   /**
    * The delay between invocations. This delay is overridden by the [intervalFn] if it is not `null`.
    */
   var interval: Duration = EventuallyConfigurationDefaults.interval

   /**
    * A function that is invoked to calculate the next interval. This if this `null`, then the
    * value of [interval] is used.
    *
    * This function can be used to implement [fibonacci] or [exponential] backoff.
    */
   var intervalFn: DurationFn? = EventuallyConfigurationDefaults.intervalFn

   /**
    * The maximum number of invocations regardless of durations. By default, this is set to max retries. MUST be non-negative.
    */
   var retries: Int = EventuallyConfigurationDefaults.retries

   /**
    * A set of exceptions, which, if thrown, will cause the test function to be retried.
    * By default, all exceptions are retried.
    *
    * This set, if provided and not empty, overrides the logic specified by [expectedExceptionsFn].
    */
   var expectedExceptions: Set<KClass<out Throwable>> = EventuallyConfigurationDefaults.expectedExceptions

   /**
    * A function that is invoked to determine if a thrown exception is expected and the test
    * function retried. By default, this function returns true for all exceptions, or in other words,
    * all errors cause the test function to be retried.
    *
    * This function is applied only when no values are specified by [expectedExceptions].
    */
   var expectedExceptionsFn: (Throwable) -> Boolean = EventuallyConfigurationDefaults.expectedExceptionsFn

   /**
    * A listener that is invoked after each failed invocation, with the iteration count,
    * and the failing cause.
    */
   var listener: EventuallyListener? = EventuallyConfigurationDefaults.listener

   /**
    * A function that is invoked after each failed invocation which causes no further
    * invocations, but instead immediately fails the [eventually] function.
    *
    * This is useful for unrecoverable failures, where retrying would not have any effect.
    */
   var shortCircuit: (Throwable) -> Boolean = EventuallyConfigurationDefaults.shortCircuit

   /**
    * An option that can be used to turn off the first error.
    *
    * This is useful for those who don't want to see the first error.
    */
   var includeFirst: Boolean = EventuallyConfigurationDefaults.includeFirst
}

typealias EventuallyListener = suspend (Int, Throwable) -> Unit

object NoopEventuallyListener : EventuallyListener {
   override suspend fun invoke(iteration: Int, error: Throwable) {}
}

private class EventuallyControl(
   val config: EventuallyConfiguration,
   private val start: TimeMark,
) {

   val end: TimeMark = start.plus(config.duration)

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

      // do not intercept Error unless it's an AssertionError
      if (e is Error && e !is AssertionError) {
         return true
      }

      return !config.expectedExceptionsFn(e)
   }

   suspend fun step() {
      lastInterval = config.intervalFn.next(++iterations)
      val delayMark = start.elapsedNow()
      // cap the interval at remaining time
      delay(minOf(lastInterval, config.duration - delayMark))
      lastDelayPeriod = (start.elapsedNow() - delayMark)
   }

   fun hasAttemptsRemaining(): Boolean = end.hasNotPassedNow() && iterations < config.retries

   fun buildFailureMessage(): String = buildString {
      appendLine("Block failed after ${start.elapsedNow()}; attempted $iterations time(s)")

      firstError?.takeIf { config.includeFirst }?.run {
         appendLine("The first error was caused by: ${this.message ?: ""}")
         appendLine(this.stackTraceToString())
      }

      lastError?.run {
         appendLine("The last error was caused by: ${this.message ?: ""}")
         appendLine(this.stackTraceToString())
      }
   }
}

internal object ShortCircuitControlException : Throwable()
