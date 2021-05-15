package io.kotest.framework.concurrency

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.common.ExperimentalKotest
import io.kotest.mpp.timeInMillis
import kotlinx.coroutines.delay
import kotlin.reflect.KClass

typealias ThrowablePredicate = (Throwable) -> Boolean

@ExperimentalKotest
data class EventuallyConfig(
   val patience: PatienceConfig = PatienceConfig(),
   val exceptions: Set<KClass<out Throwable>> = setOf(),
   val allowExceptionIf: ThrowablePredicate? = null,
   val initialDelay: Millis = 0L,
   val retries: Int = Int.MAX_VALUE,
) {
   constructor(
      duration: Millis,
      interval: Interval = PatienceConfig.defaultInterval,
      exceptions: Set<KClass<out Throwable>> = setOf(),
      allowExceptionIf: ThrowablePredicate? = null,
      initialDelay: Millis = 0L,
      retries: Int = Int.MAX_VALUE,
   ) : this(PatienceConfig(duration, interval), exceptions, allowExceptionIf, initialDelay, retries)

   init {
      require(initialDelay >= 0L) { "Value 'initialDelay' should be a non-negative number" }
      require(retries >= 0) { "Value 'retries' should be a non-negative number" }
   }

   fun allowExceptions(vararg exceptions: KClass<out Throwable>) = copy(exceptions = this.exceptions + exceptions)
   suspend fun <T> allowExceptions(
      vararg exceptions: KClass<out Throwable>, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>,
   ): T = copy(exceptions = this.exceptions + exceptions).invoke(listener, shortCircuit, f)

   fun allowExceptionIf(allowExceptionIf: ThrowablePredicate) = copy(allowExceptionIf = allowExceptionIf)
   suspend fun <T> allowExceptionIf(
      allowExceptionIf: ThrowablePredicate, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>,
   ): T = allowExceptionIf(allowExceptionIf).invoke(listener, shortCircuit, f)

   fun withInitialDelay(initialDelay: Millis) = copy(initialDelay = maxOf(initialDelay, 0L))
   suspend fun <T> withInitialDelay(
      initialDelay: Millis, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>,
   ): T = withInitialDelay(initialDelay).invoke(listener, shortCircuit, f)

   fun withRetries(retries: Int) = copy(retries = maxOf(retries, 0))
   suspend fun <T> withRetries(
      retries: Int, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>,
   ): T = withRetries(retries).invoke(listener, shortCircuit, f)
}

// region overloads

@ExperimentalKotest
suspend fun <T> eventually(config: EventuallyConfig, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>) =
   config.allowExceptions(AssertionError::class).invoke(listener, shortCircuit, f)

@ExperimentalKotest
fun eventually(patience: PatienceConfig) = EventuallyConfig(patience).allowExceptions(AssertionError::class)

@ExperimentalKotest
fun eventually(duration: Millis, interval: Interval = PatienceConfig.defaultInterval) =
   eventually(PatienceConfig(duration, interval)).allowExceptions(AssertionError::class)

@ExperimentalKotest
suspend fun <T> eventually(patience: PatienceConfig, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>) =
   eventually(patience).invoke(listener, shortCircuit, f)

@ExperimentalKotest
suspend fun <T> eventually(duration: Millis, interval: Interval = PatienceConfig.defaultInterval, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>) =
   eventually(duration, interval).invoke(listener, shortCircuit, f)

@ExperimentalKotest
fun until(patience: PatienceConfig) = EventuallyConfig(patience)

@ExperimentalKotest
suspend fun until(patience: PatienceConfig, booleanProducer: ConcurrencyProducer<Boolean>) =
   until(patience).invoke(listener = { it.result==true }, f = booleanProducer)

@ExperimentalKotest
fun until(duration: Millis, interval: Interval = PatienceConfig.defaultInterval) =
   until(PatienceConfig(duration, interval))

@ExperimentalKotest
suspend fun until(duration: Millis, interval: Interval = PatienceConfig.defaultInterval, booleanProducer: ConcurrencyProducer<Boolean>) =
   until(duration, interval).invoke(listener = { it.result==true }, f = booleanProducer)

@ExperimentalKotest
suspend fun <T> until(patience: PatienceConfig, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>) =
   until(patience).invoke(listener, shortCircuit, f)

@ExperimentalKotest
suspend fun <T> until(duration: Millis, interval: Interval = PatienceConfig.defaultInterval, listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>) =
   until(duration, interval).invoke(listener, shortCircuit, f)

// endregion

@ExperimentalKotest
class EventuallyShortCircuitException(override val message: String) : Throwable()

@ExperimentalKotest
data class EventuallyState<T>(
   val result: T?,
   val start: Millis,
   val end: Millis,
   val iteration: Int,
   val firstError: Throwable?,
   val thisError: Throwable?,
)

@ExperimentalKotest
fun interface EventuallyListener<T> {
   fun onEval(state: EventuallyState<T>): Boolean

   companion object {
      val default = EventuallyListener<Any?> { it.thisError==null }
   }
}

@ExperimentalKotest
private class EventuallyControl(val config: EventuallyConfig) {
   val start = timeInMillis()
   val end = start + config.patience.duration

   var times = 0
   var predicateFailedTimes = 0

   var firstError: Throwable? = null
   var lastError: Throwable? = null

   var lastDelayPeriod: Millis = 0L
   var lastInterval: Millis = 0L

   fun exceptionIsNotAllowed(e: Throwable): Boolean {
      if (firstError==null) {
         firstError = e
      } else {
         lastError = e
      }

      if (EventuallyShortCircuitException::class.isInstance(e)) {
         return true
      }

      if (config.allowExceptionIf?.invoke(e)==false) {
         return true
      }

      return !config.exceptions.any { it.isInstance(e) }
   }

   suspend fun step() {
      lastInterval = config.patience.interval.next(++times)
      val delayMark = timeInMillis()
      delay(lastInterval)
      lastDelayPeriod = timeInMillis() - delayMark
   }

   fun attemptsRemaining() = timeInMillis() < end && times < config.retries

   /**
    * if we only executed once, and the last delay was > last interval, we didn't get a chance to run again so we run once more before exiting
    */
   fun isLongWait() = times==1 && lastDelayPeriod > lastInterval

   fun buildFailureMessage() = StringBuilder().apply {
      val patienceConfig = config.patience
      appendLine("Eventually block failed after ${patienceConfig.duration}ms; attempted $times time(s); ${patienceConfig.interval} delay between attempts")

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
   }.toString()
}

@ExperimentalKotest
suspend operator fun <T> EventuallyConfig.invoke(
   listener: EventuallyListener<T>? = null, shortCircuit: ConcurrencyConsumer<T>? = null, f: ConcurrencyProducer<T>,
): T {
   delay(initialDelay) // TODO: should the initialDelay count against the patienceConfig.duration?

   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   val control = EventuallyControl(this)

   try {
      while (control.attemptsRemaining() || control.isLongWait()) {
         try {
            val result = f()
            val state = EventuallyState(result, control.start, control.end, control.times, control.firstError, control.lastError)

            when (shortCircuit?.invoke(result)) {
               null, false -> Unit
               true -> throw EventuallyShortCircuitException("The provided 'shortCircuit' function was triggered: $state")
            }

            when (listener?.onEval(state)) {
               null, true -> return result
               false -> control.predicateFailedTimes++
            }
         } catch (e: Throwable) {
            val notAllowed = control.exceptionIsNotAllowed(e)
            listener?.onEval(EventuallyState(null, control.start, control.end, control.times, control.firstError, control.lastError))

            if (notAllowed) {
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
