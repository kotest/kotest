package io.kotest.assertions.nondeterministic

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

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

   val start = EventuallyTimeSource.current().markNow()
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
      control.iterations++
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
) {
   init {
      require(duration.inWholeMilliseconds >= 0) { "Duration must be greater than or equal to 0" }
      require(retries >= 0) { "Retries must be greater than or equal to 0" }
   }
}

object EventuallyConfigurationDefaults {
   var duration: Duration = Duration.INFINITE
   var initialDelay: Duration = Duration.ZERO
   var interval: Duration = 25.milliseconds
   var intervalFn: DurationFn? = null
   var retries: Int = Int.MAX_VALUE
   var expectedExceptions: Set<KClass<out Throwable>> = emptySet()
   var expectedExceptionsFn: (Throwable) -> Boolean = { true }
   var listener: EventuallyListener? = null
   var shortCircuit: (Throwable) -> Boolean = { false }
   var includeFirst: Boolean = true
}

class EventuallyConfigurationBuilder {

   /**
    * The total time that the [eventually] function can take to complete successfully. Must be greater than or equal to 0.
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
    * The maximum number of invocations regardless of durations. By default, this is set to max retries. And Must be greater than or equal to 0.
    */
   var retries: Int = EventuallyConfigurationDefaults.retries

   /**
    * A set of exceptions, which if thrown, will cause the test function to be retried.
    * By default, all exceptions are retried.
    *
    * This set is applied in addition to the values specified by [expectedExceptionsFn].
    */
   var expectedExceptions: Set<KClass<out Throwable>> = EventuallyConfigurationDefaults.expectedExceptions

   /**
    * A function that is invoked to determine if a thrown exception is expected and the test
    * function retried. By default, this function returns true for all exceptions, or in other words,
    * all errors cause the test function to be retried.
    *
    * This function is applied in addition to the values specified by [expectedExceptions].
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


/**
 * Store a [TimeSource] to be used by [eventually].
 */
internal class EventuallyTimeSource(
   val timeSource: TimeSource
) : CoroutineContext.Element {

   override val key: CoroutineContext.Key<EventuallyTimeSource>
      get() = KEY

   internal companion object {
      /**
       * Retrieves the [TimeSource] used by [eventually].
       *
       * For internal Kotest testing purposes the [TimeSource] can be overridden.
       * For normal usage [TimeSource.Monotonic] is used.
       */
      internal suspend fun current(): TimeSource =
         coroutineContext[KEY]?.timeSource
            ?: TimeSource.Monotonic

      internal val KEY = object : CoroutineContext.Key<EventuallyTimeSource> {}
   }
}
