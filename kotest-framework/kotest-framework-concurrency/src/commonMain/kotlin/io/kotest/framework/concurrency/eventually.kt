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
sealed class EventuallyConfig<out T> {
   abstract val patience: PatienceConfig
   abstract val exceptions: Set<KClass<out Throwable>>
   abstract val suppressExceptionIf: ThrowablePredicate?
   abstract val initialDelay: Long
   abstract val retries: Int
}

@ExperimentalKotest
data class BasicEventuallyConfig(
   override val patience: PatienceConfig = PatienceConfig(),
   override val exceptions: Set<KClass<out Throwable>> = setOf(),
   override val suppressExceptionIf: ThrowablePredicate? = null,
   override val initialDelay: Long = 0L,
   override val retries: Int = Int.MAX_VALUE,
) : EventuallyConfig<Nothing>() {
   constructor(
      duration: Long,
      interval: Interval = PatienceConfig.defaultInterval,
      exceptions: Set<KClass<out Throwable>> = setOf(),
      allowExceptionIf: ThrowablePredicate? = null,
      initialDelay: Long = 0L,
      retries: Int = Int.MAX_VALUE,
   ) : this(PatienceConfig(duration, interval), exceptions, allowExceptionIf, initialDelay, retries)

   init {
      require(initialDelay >= 0L) { "Value 'initialDelay' should be a non-negative number" }
      require(retries >= 0) { "Value 'retries' should be a non-negative number" }
   }

   fun withDuration(duration: Long) = copy(patience = patience.copy(duration = duration))
   suspend fun <T> withDuration(duration: Long, f: suspend () -> T): T = withDuration(duration).invoke(f)

   fun withInterval(interval: Interval) = copy(patience = patience.copy(interval = interval))
   suspend fun <T> withInterval(interval: Interval, f: suspend () -> T): T = withInterval(interval).invoke(f)

   fun withSuppressExceptionIf(suppressExceptionIf: ThrowablePredicate) = copy(suppressExceptionIf = suppressExceptionIf)
   suspend fun <T> withSuppressExceptionIf(suppressExceptionIf: ThrowablePredicate, f: suspend () -> T): T = withSuppressExceptionIf(suppressExceptionIf).invoke(f)

   fun withInitialDelay(initialDelay: Long) = copy(initialDelay = initialDelay)
   suspend fun <T> withInitialDelay(initialDelay: Long, f: suspend () -> T): T = withInitialDelay(initialDelay).invoke(f)

   fun withRetries(retries: Int) = copy(retries = retries)
   suspend fun <T> withRetries(retries: Int, f: suspend () -> T): T = withRetries(retries).invoke(f)

   fun suppressExceptions(vararg exceptions: KClass<out Throwable>) = copy(exceptions = this.exceptions + exceptions)
   suspend fun <T> suppressExceptions(vararg exceptions: KClass<out Throwable>, f: suspend () -> T): T = copy(exceptions = this.exceptions + exceptions).invoke(f)

   fun <T> withListener(listener: EventuallyListener<T>) = GenericEventuallyConfig(patience, exceptions, suppressExceptionIf, initialDelay, retries, listener = listener)
   suspend fun <T> withListener(listener: EventuallyListener<T>, f: suspend () -> T): T = withListener(listener).invoke(f)

   fun <T> withShortCircuit(shortCircuit: EventuallyListener<T>) = GenericEventuallyConfig(patience, exceptions, suppressExceptionIf, initialDelay, retries, shortCircuit = shortCircuit)
   suspend fun <T> withShortCircuit(shortCircuit: EventuallyListener<T>, f: suspend () -> T): T = withShortCircuit(shortCircuit).invoke(f)
}

@ExperimentalKotest
data class GenericEventuallyConfig<T>(
   override val patience: PatienceConfig = PatienceConfig(),
   override val exceptions: Set<KClass<out Throwable>> = setOf(),
   override val suppressExceptionIf: ThrowablePredicate? = null,
   override val initialDelay: Long = 0L,
   override val retries: Int = Int.MAX_VALUE,
   val listener: EventuallyListener<T>? = null,
   val shortCircuit: EventuallyListener<T>? = null,
) : EventuallyConfig<T>() {
   constructor(
      duration: Long,
      interval: Interval = PatienceConfig.defaultInterval,
      exceptions: Set<KClass<out Throwable>> = setOf(),
      allowExceptionIf: ThrowablePredicate? = null,
      initialDelay: Long = 0L,
      retries: Int = Int.MAX_VALUE,
      listener: EventuallyListener<T>? = null,
      shortCircuit: EventuallyListener<T>? = null,
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

   fun withDuration(duration: Long) = copy(patience = patience.copy(duration = duration))
   suspend fun withDuration(duration: Long, f: suspend () -> T): T = withDuration(duration).invoke(f)

   fun withInterval(interval: Interval) = copy(patience = patience.copy(interval = interval))
   suspend fun withInterval(interval: Interval, f: suspend () -> T): T = withInterval(interval).invoke(f)

   fun withSuppressExceptionIf(suppressExceptionIf: ThrowablePredicate) = copy(suppressExceptionIf = suppressExceptionIf)
   suspend fun withSuppressExceptionIf(suppressExceptionIf: ThrowablePredicate, f: suspend () -> T): T = withSuppressExceptionIf(suppressExceptionIf).invoke(f)

   fun withInitialDelay(initialDelay: Long) = copy(initialDelay = initialDelay)
   suspend fun withInitialDelay(initialDelay: Long, f: suspend () -> T): T = withInitialDelay(initialDelay).invoke(f)

   fun withRetries(retries: Int) = copy(retries = retries)
   suspend fun withRetries(retries: Int, f: suspend () -> T): T = withRetries(retries).invoke(f)

   fun suppressExceptions(vararg exceptions: KClass<out Throwable>) = copy(exceptions = this.exceptions + exceptions)
   suspend fun suppressExceptions(vararg exceptions: KClass<out Throwable>, f: suspend () -> T): T = copy(exceptions = this.exceptions + exceptions).invoke(f)

   fun withListener(listener: EventuallyListener<T>) = copy(listener = listener)
   suspend fun withListener(listener: EventuallyListener<T>, f: suspend () -> T): T = withListener(listener).invoke(f)

   fun withShortCircuit(shortCircuit: EventuallyListener<T>) = copy(shortCircuit = shortCircuit)
   suspend fun withShortCircuit(shortCircuit: EventuallyListener<T>, f: suspend () -> T): T = withShortCircuit(shortCircuit).invoke(f)
}

@ExperimentalKotest
fun eventually(patience: PatienceConfig) = BasicEventuallyConfig(patience).suppressExceptions(AssertionError::class)

@ExperimentalKotest
suspend fun <T> eventually(patience: PatienceConfig, f: suspend () -> T) = eventually(patience).invoke(f)

@ExperimentalKotest
fun eventually(duration: Long) = eventually(PatienceConfig(duration)).suppressExceptions(AssertionError::class)

@ExperimentalKotest
suspend fun <T> eventually(duration: Long, f: suspend () -> T) = eventually(duration).invoke(f)

@ExperimentalKotest
fun eventually(duration: Long, interval: Interval) = eventually(PatienceConfig(duration, interval)).suppressExceptions(AssertionError::class)

@ExperimentalKotest
suspend fun <T> eventually(duration: Long, interval: Interval, f: suspend () -> T) = eventually(duration, interval).invoke(f)

@ExperimentalKotest
suspend fun until(patience: PatienceConfig, booleanProducer: suspend () -> Boolean) = eventually(patience).withListener<Boolean>({ it.result == true }).invoke(f = booleanProducer)

@ExperimentalKotest
suspend fun until(
   duration: Long,
   interval: Interval = PatienceConfig.defaultInterval,
   booleanProducer: suspend () -> Boolean
) =
   until(PatienceConfig(duration, interval), booleanProducer)

@ExperimentalKotest
class EventuallyShortCircuitException(override val message: String) : Throwable()

@ExperimentalKotest
data class EventuallyState<T>(
   val result: T?,
   val start: Long,
   val end: Long,
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
private class EventuallyControl(val config: EventuallyConfig<*>) {
   val start = timeInMillis()
   val end = start + config.patience.duration

   var times = 0
   var predicateFailedTimes = 0

   var firstError: Throwable? = null
   var lastError: Throwable? = null

   var lastDelayPeriod: Long = 0L
   var lastInterval: Long = 0L

   fun exceptionIsNotSuppressable(e: Throwable): Boolean {
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
   fun isLongWait() = times == 1 && lastDelayPeriod > lastInterval

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
suspend operator fun <T> EventuallyConfig<T>.invoke(f: suspend () -> T): T {
   delay(initialDelay)

   val originalAssertionMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   val control = EventuallyControl(this)

   try {
      while (control.attemptsRemaining() || control.isLongWait()) {
         try {
            val result = f()
            val state =
               EventuallyState(result, control.start, control.end, control.times, control.firstError, control.lastError)

            when (this) {
               is BasicEventuallyConfig -> return result
               is GenericEventuallyConfig -> {
                  when (shortCircuit?.onEval(state)) {
                     null, false -> Unit
                     true -> throw EventuallyShortCircuitException("The provided 'shortCircuit' function caused eventually to exit early: $state")
                  }

                  when (listener?.onEval(state)) {
                     null, true -> return result
                     false -> control.predicateFailedTimes++
                  }
               }
            }
         } catch (e: Throwable) {
            val notSuppressable = control.exceptionIsNotSuppressable(e)
            when (this) {
               is BasicEventuallyConfig -> Unit
               is GenericEventuallyConfig -> {
                  val state = EventuallyState<T>(
                     null,
                     control.start,
                     control.end,
                     control.times,
                     control.firstError,
                     control.lastError
                  )
                  listener?.onEval(state)
               }
            }

            if (notSuppressable) {
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
