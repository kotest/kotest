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
data class BasicEventuallyConfig(
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

   fun withDuration(duration: Millis) = copy(patience = patience.copy(duration = duration))
   suspend fun <T> withDuration(duration: Millis, f: () -> T) = withDuration(duration).invoke(f = f)

   fun withInterval(interval: Interval) = copy(patience = patience.copy(interval = interval))
   suspend fun <T> withInterval(interval: Interval, f: () -> T) = withInterval(interval).invoke(f = f)

   fun suppressExceptions(vararg exceptions: KClass<out Throwable>): BasicEventuallyConfig =
      copy(exceptions = this.exceptions + exceptions)

   suspend fun <T> suppressExceptions(vararg exceptions: KClass<out Throwable>, f: () -> T): T =
      copy(exceptions = this.exceptions + exceptions).invoke(f = f)

   fun suppressExceptionIf(allowExceptionIf: ThrowablePredicate) = copy(allowExceptionIf = allowExceptionIf)
   suspend fun <T> suppressExceptionIf(allowExceptionIf: ThrowablePredicate, f: () -> T): T =
      suppressExceptionIf(allowExceptionIf).invoke(f = f)

   fun withInitialDelay(initialDelay: Millis) = copy(initialDelay = maxOf(initialDelay, 0L))
   suspend fun <T> withInitialDelay(initialDelay: Millis, f: () -> T): T =
      withInitialDelay(initialDelay).invoke(f = f)

   fun withRetries(retries: Int) = copy(retries = maxOf(retries, 0))
   suspend fun <T> withRetries(retries: Int, f: () -> T): T = withRetries(retries).invoke(f = f)

   fun <T> withShortCircuit(shortCircuit: ConcurrencyConsumer<T>) = // todo finish rest of params
      GenericEventuallyConfig<T>(shortCircuit = shortCircuit)

   suspend fun <T> withShortCircuit(shortCircuit: ConcurrencyConsumer<T>, f: ConcurrencyProducer<T>) =
      GenericEventuallyConfig<T>(shortCircuit = shortCircuit).invoke(f = f)

   fun <T> withListener(listener: EventuallyListener<T>) = // todo finish rest of params
      GenericEventuallyConfig<T>(patience = patience, listener = listener)

   fun <T> withListener(listener: EventuallyListener<T>, f: () -> T) =  // todo finish rest of params
      // todo finish rest of params
      GenericEventuallyConfig<T>(patience = patience, listener = listener)
}

@ExperimentalKotest
data class GenericEventuallyConfig<T>(
   val patience: PatienceConfig = PatienceConfig(),
   val exceptions: Set<KClass<out Throwable>> = setOf(),
   val allowExceptionIf: ThrowablePredicate? = null,
   val initialDelay: Millis = 0L,
   val retries: Int = Int.MAX_VALUE,
   val listener: EventuallyListener<T>? = null,
   val shortCircuit: ConcurrencyConsumer<T>? = null,
) {
   constructor(
      duration: Millis,
      interval: Interval = PatienceConfig.defaultInterval,
      exceptions: Set<KClass<out Throwable>> = setOf(),
      allowExceptionIf: ThrowablePredicate? = null,
      initialDelay: Millis = 0L,
      retries: Int = Int.MAX_VALUE,
      listener: EventuallyListener<T>? = null,
      shortCircuit: ConcurrencyConsumer<T>? = null,
   ) : this(
      PatienceConfig(duration, interval),
      exceptions,
      allowExceptionIf,
      initialDelay,
      retries,
      listener,
      shortCircuit
   )

   init {
      require(initialDelay >= 0L) { "Value 'initialDelay' should be a non-negative number" }
      require(retries >= 0) { "Value 'retries' should be a non-negative number" }
   }

   fun withShortCircuit(shortCircuit: ConcurrencyConsumer<T>) = copy(shortCircuit = shortCircuit)

   suspend fun withShortCircuit(shortCircuit: ConcurrencyConsumer<T>, f: ConcurrencyProducer<T>) =
      copy(shortCircuit = shortCircuit).invoke(f = f)

   fun withListener(listener: EventuallyListener<T>) =
      copy(listener = listener)

   fun withListener(listener: EventuallyListener<T>, f: () -> T) =
      copy(listener = listener)
}

@ExperimentalKotest
fun eventually(patience: PatienceConfig) = BasicEventuallyConfig(patience).suppressExceptions(AssertionError::class)

@ExperimentalKotest
fun eventually(duration: Millis) =
   eventually(PatienceConfig(duration)).suppressExceptions(AssertionError::class)

@ExperimentalKotest
fun eventually(duration: Millis, f: ConcurrencyProducer<Unit>) =
   eventually(PatienceConfig(duration)).suppressExceptions(AssertionError::class)

@ExperimentalKotest
fun eventually(duration: Millis, interval: Interval) =
   eventually(PatienceConfig(duration, interval)).suppressExceptions(AssertionError::class)

@ExperimentalKotest
fun eventually(duration: Millis, interval: Interval, f: ConcurrencyProducer<Unit>) =
   eventually(PatienceConfig(duration, interval)).suppressExceptions(AssertionError::class)

@ExperimentalKotest
suspend fun until(patience: PatienceConfig, booleanProducer: ConcurrencyProducer<Boolean>) =
   BasicEventuallyConfig(patience).withListener<Boolean>({ it.result == true }).invoke(f = booleanProducer)

@ExperimentalKotest
suspend fun until(duration: Millis = PatienceConfig.defaultDuration, interval: Interval = PatienceConfig.defaultInterval, booleanProducer: ConcurrencyProducer<Boolean>) =
   until(PatienceConfig(duration, interval), booleanProducer)

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
private class EventuallyControl<T>(val config: EventuallyConfig<T>) {
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
suspend operator fun <T> BasicEventuallyConfig.invoke(f: () -> T): T {
   delay(initialDelay) // TODO: should the initialDelay count against the patienceConfig.duration?

   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   val control = EventuallyControl(this)

   try {
      while (control.attemptsRemaining() || control.isLongWait()) {
         try {
            val result = f()
            val state = EventuallyState(result, control.start, control.end, control.times, control.firstError, control.lastError)

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

@ExperimentalKotest
suspend operator fun <T> GenericEventuallyConfig<T>.invoke(f: ConcurrencyProducer<T>): T {
   delay(initialDelay) // TODO: should the initialDelay count against the patienceConfig.duration?

   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   val control = EventuallyControl(this)

   try {
      while (control.attemptsRemaining() || control.isLongWait()) {
         try {
            val result = f()
            val state =
               EventuallyState(result, control.start, control.end, control.times, control.firstError, control.lastError)

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
            listener?.onEval(
               EventuallyState(
                  null,
                  control.start,
                  control.end,
                  control.times,
                  control.firstError,
                  control.lastError
               )
            )

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
