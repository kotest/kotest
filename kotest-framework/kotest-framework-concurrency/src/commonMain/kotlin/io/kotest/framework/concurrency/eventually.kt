package io.kotest.framework.concurrency

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.common.KotestInternal
import kotlinx.coroutines.delay
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
typealias EventuallyStateFunction<T, U> = (EventuallyState<T>) -> U

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
typealias ThrowablePredicate = (Throwable) -> Boolean

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
data class EventuallyConfig<T>(
   val duration: Long = defaultDuration,
   val interval: Interval = defaultInterval,
   val initialDelay: Long = defaultDelay,
   val retries: Int = Int.MAX_VALUE,
   val suppressExceptions: Set<KClass<out Throwable>> = setOf(),
   val suppressExceptionIf: ThrowablePredicate? = null,
   val listener: EventuallyStateFunction<T, Unit>? = null,
   val predicate: EventuallyStateFunction<T, Boolean>? = null,
   val shortCircuit: EventuallyStateFunction<T, Boolean>? = null,
)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
private fun <T> EventuallyConfig<T>.toBuilder() = EventuallyBuilder<T>().apply {
   duration = this@toBuilder.duration
   interval = this@toBuilder.interval
   initialDelay = this@toBuilder.initialDelay
   retries = this@toBuilder.retries
   suppressExceptions = this@toBuilder.suppressExceptions
   suppressExceptionIf = this@toBuilder.suppressExceptionIf
   listener = this@toBuilder.listener
   predicate = this@toBuilder.predicate
   shortCircuit = this@toBuilder.shortCircuit
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
class EventuallyBuilder<T> {
   var duration: Long = defaultDuration
   var interval: Interval = defaultInterval
   var initialDelay: Long = defaultDelay
   var retries: Int = Int.MAX_VALUE
   var suppressExceptions: Set<KClass<out Throwable>> = setOf(AssertionError::class)
   var suppressExceptionIf: ThrowablePredicate? = null
   var listener: EventuallyStateFunction<T, Unit>? = null
   var predicate: EventuallyStateFunction<T, Boolean>? = null
   var shortCircuit: EventuallyStateFunction<T, Boolean>? = null

   fun build() = EventuallyConfig(
      duration = duration, interval = interval, initialDelay = initialDelay, retries = retries,
      suppressExceptions = suppressExceptions, suppressExceptionIf = suppressExceptionIf,
      listener = listener, predicate = predicate, shortCircuit = shortCircuit
   )

   @KotestInternal
   internal fun duration(duration: Duration) {
      this.duration = duration.inWholeMilliseconds
   }
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
class EventuallyShortCircuitException(override val message: String) : Throwable()

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
data class EventuallyState<T>(
   val result: T?,
   val start: Long,
   val end: Long,
   val times: Int,
   val firstError: Throwable?,
   val thisError: Throwable?,
)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
private class EventuallyControl(val config: EventuallyConfig<*>) {
   val start: TimeMark = TimeSource.Monotonic.markNow()

   var times = 0
   var predicateFailedTimes = 0

   var firstError: Throwable? = null
   var lastError: Throwable? = null

   var lastDelayPeriod: Long = 0L
   var lastInterval: Long = 0L

   fun exceptionIsNotSuppressible(e: Throwable): Boolean {
      if (firstError == null) {
         firstError = e
      } else {
         lastError = e
      }

      if (EventuallyShortCircuitException::class.isInstance(e)) {
         return true
      }

      if (config.suppressExceptionIf?.invoke(e) == false) {
         return true
      }

      return !config.suppressExceptions.any { it.isInstance(e) }
   }

   fun <T> toState(result: T?) = EventuallyState(
      result = result,
      start = 0L,
      end = config.duration,
      times = times,
      firstError = firstError,
      thisError = lastError
   )

   suspend fun step() {
      lastInterval = config.interval.next(++times)
      delay(lastInterval)
      lastDelayPeriod = lastInterval
   }

   fun attemptsRemaining(): Boolean = start.elapsedNow() < config.duration.milliseconds && times < config.retries

   /**
    * If we only executed once, and [lastDelayPeriod] was > [lastInterval], we didn't get a chance to run again,
    * so we run once more before exiting.
    */
   fun isLongWait() = times == 1 && lastDelayPeriod > lastInterval

   fun buildFailureMessage(): String = buildString {
      appendLine("Eventually block failed after ${config.duration}ms; attempted $times time(s); ${config.interval} delay between attempts")

      if (predicateFailedTimes > 0) {
         appendLine("The provided predicate failed $predicateFailedTimes times")
      }

      firstError?.run {
         appendLine("The first error was caused by: ${this.message}")
         appendLine(this.stackTraceToString())
      }

      lastError?.run {
         appendLine("The last error was caused by: ${this.message}")
         appendLine(this.stackTraceToString())
      }
   }
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend operator fun <T> EventuallyConfig<T>.invoke(f: suspend () -> T): T {
   delay(initialDelay)

   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   val control = EventuallyControl(this)

   try {
      while (control.attemptsRemaining() || control.isLongWait()) {
         try {
            val result = f()
            val state = control.toState<T>(result)

            listener?.invoke(state)

            when (shortCircuit?.invoke(state)) {
               null, false -> Unit
               true -> throw EventuallyShortCircuitException("The provided shortCircuit function caused eventually to exit early: $state")
            }

            when (predicate?.invoke(state)) {
               null, true -> return result
               false -> control.predicateFailedTimes++
            }
         } catch (e: Throwable) {
            val notSuppressible = control.exceptionIsNotSuppressible(e)
            listener?.invoke(control.toState(null))
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

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(
   config: EventuallyConfig<T>,
   configure: EventuallyBuilder<T>.() -> Unit,
   @BuilderInference test: suspend () -> T
): T {
   val resolvedConfig = config.toBuilder().apply(configure).build()
   return resolvedConfig.invoke(test)
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(
   configure: EventuallyBuilder<T>.() -> Unit, @BuilderInference test: suspend () -> T
): T {
   val config = EventuallyBuilder<T>().apply(configure).build()
   return config.invoke(test)
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(
   config: EventuallyConfig<T>, @BuilderInference test: suspend () -> T
): T {
   return config.invoke(test)
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(duration: Duration, test: suspend () -> T): T =
   eventually(duration.inWholeMilliseconds, test)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun <T> eventually(duration: Long, test: suspend () -> T): T = eventually({ this.duration = duration }, test)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun until(
   config: EventuallyConfig<Boolean>,
   configure: EventuallyBuilder<Boolean>.() -> Unit,
   @BuilderInference test: suspend () -> Boolean
) {
   val builder = config.toBuilder()
   builder.predicate = { it.result == true }
   builder.apply(configure)
   builder.build().invoke(test)
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun until(
   configure: EventuallyBuilder<Boolean>.() -> Unit, @BuilderInference test: suspend () -> Boolean
) {
   val builder = EventuallyBuilder<Boolean>()
   builder.predicate = { it.result == true }
   builder.apply(configure)
   builder.build().invoke(test)
}

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun until(duration: Duration, test: suspend () -> Boolean) = until(millis = duration.inWholeMilliseconds, test)

@Deprecated("Replaced with the io.kotest.assertions.nondeterministic utils. Deprecated in 5.7")
suspend fun until(millis: Long, test: suspend () -> Boolean) = until({ this.duration = millis }, test)
