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
sealed class EventuallyConfig<T> {
   abstract val patience: PatienceConfig
   abstract val exceptions: Set<KClass<out Throwable>>
   abstract val suppressExceptionIf: ThrowablePredicate?
   abstract val initialDelay: Millis
   abstract val retries: Int
}

@ExperimentalKotest
data class BasicEventuallyConfig(
   override val patience: PatienceConfig = PatienceConfig(),
   override val exceptions: Set<KClass<out Throwable>> = setOf(),
   override val suppressExceptionIf: ThrowablePredicate? = null,
   override val initialDelay: Millis = 0L,
   override val retries: Int = Int.MAX_VALUE,
) : EventuallyConfig<Any>() {
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
   fun withInterval(interval: Interval) = copy(patience = patience.copy(interval = interval))
   fun withSupressExceptionIf(suppressExceptionIf: ThrowablePredicate) = copy(suppressExceptionIf = suppressExceptionIf)
   fun withInitialDelay(initialDelay: Millis) = copy(initialDelay = initialDelay)
   fun withRetries(retries: Int) = copy(retries = retries)
   fun <T> withListener(listener: EventuallyListener<T>) = GenericEventuallyConfig(patience, exceptions, suppressExceptionIf, initialDelay, retries, listener = listener)
   fun <T> withShortCircuit(shortCircuit: EventuallyListener<T>) = GenericEventuallyConfig(patience, exceptions, suppressExceptionIf, initialDelay, retries, shortCircuit = shortCircuit)
}

@ExperimentalKotest
data class GenericEventuallyConfig<T>(
   override val patience: PatienceConfig = PatienceConfig(),
   override val exceptions: Set<KClass<out Throwable>> = setOf(),
   override val suppressExceptionIf: ThrowablePredicate? = null,
   override val initialDelay: Millis = 0L,
   override val retries: Int = Int.MAX_VALUE,
   val listener: EventuallyListener<in T>? = null,
   val shortCircuit: EventuallyListener<in T>? = null,
) : EventuallyConfig<T>() {
   constructor(
      duration: Millis,
      interval: Interval = PatienceConfig.defaultInterval,
      exceptions: Set<KClass<out Throwable>> = setOf(),
      allowExceptionIf: ThrowablePredicate? = null,
      initialDelay: Millis = 0L,
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

   fun withDuration(duration: Millis) = copy(patience = patience.copy(duration = duration))
   fun withInterval(interval: Interval) = copy(patience = patience.copy(interval = interval))
   fun withSupressExceptionIf(suppressExceptionIf: ThrowablePredicate) = copy(suppressExceptionIf = suppressExceptionIf)
   fun withInitialDelay(initialDelay: Millis) = copy(initialDelay = initialDelay)
   fun withRetries(retries: Int) = copy(retries = retries)
   fun withListener(listener: EventuallyListener<T>) = copy(listener = listener)
   fun withShortCircuit(shortCircuit: EventuallyListener<T>) = copy(shortCircuit = shortCircuit)
}

fun <T> EventuallyConfig<T>.withDuration(duration: Millis, f: suspend () -> T): T = when (this) {
   is BasicEventuallyConfig -> withDuration(duration).invoke(f)
}

@ExperimentalKotest
fun <T> EventuallyConfig<T>.withDuration(duration: Millis):  = when (this) {
   is BasicEventuallyConfig -> copy(patience = patience.copy(duration = duration))
   is GenericEventuallyConfig<T> -> copy(patience = patience.copy(duration = duration))
}

fun withInterval(interval: Interval) = copy(patience = patience.copy(interval = interval))
suspend fun <T> withInterval(interval: Interval, f: suspend () -> T) = withInterval(interval).invoke(f = f)

fun suppressExceptions(vararg exceptions: KClass<out Throwable>): BasicEventuallyConfig =
   copy(exceptions = this.exceptions + exceptions)

suspend fun <T> suppressExceptions(vararg exceptions: KClass<out Throwable>, f: suspend () -> T): T =
   copy(exceptions = this.exceptions + exceptions).invoke(f = f)

fun suppressExceptionIf(allowExceptionIf: ThrowablePredicate) = copy(allowExceptionIf = allowExceptionIf)
suspend fun <T> suppressExceptionIf(allowExceptionIf: ThrowablePredicate, f: suspend () -> T): T =
   suppressExceptionIf(allowExceptionIf).invoke(f = f)

fun withInitialDelay(initialDelay: Millis) = copy(initialDelay = maxOf(initialDelay, 0L))
suspend fun <T> withInitialDelay(initialDelay: Millis, f: suspend () -> T): T =
   withInitialDelay(initialDelay).invoke(f = f)

fun withRetries(retries: Int) = copy(retries = maxOf(retries, 0))
suspend fun <T> withRetries(retries: Int, f: suspend () -> T): T = withRetries(retries).invoke(f = f)

fun <T> withShortCircuit(shortCircuit: ConcurrencyConsumer<T>) =
   GenericEventuallyConfig<T>(shortCircuit = shortCircuit)

suspend fun <T> withShortCircuit(shortCircuit: ConcurrencyConsumer<T>, f: ConcurrencyProducer<T>) =
   GenericEventuallyConfig<T>(shortCircuit = shortCircuit).invoke(f = f)

fun <T> withListener(listener: EventuallyListener<T>) =
   GenericEventuallyConfig<T>(patience = patience, listener = listener)

suspend fun <T> withListener(listener: EventuallyListener<T>, f: suspend () -> T) =
   GenericEventuallyConfig(listener = listener).invoke(f = f)


// region stuff

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
private class EventuallyControl(val config: EventuallyConfig<*>) {
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

      if (config.suppressExceptionIf?.invoke(e)==false) {
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

// endregion

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
            val state = EventuallyState<T>(result, control.start, control.end, control.times, control.firstError, control.lastError)

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
            val notAllowed = control.exceptionIsNotAllowed(e)
            when (this) {
               is BasicEventuallyConfig -> Unit
               is GenericEventuallyConfig -> {
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
               }
            }

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
//
//@ExperimentalKotest
//suspend operator fun <T> BasicEventuallyConfig.invoke(f: suspend () -> T): T {
//   delay(initialDelay) // TODO: should the initialDelay count against the patienceConfig.duration?
//
//   val originalAssertionMode = errorCollector.getCollectionMode()
//   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)
//
//   val control = EventuallyControl(this)
//
//   try {
//      while (control.attemptsRemaining() || control.isLongWait()) {
//         try {
//            return f()
//         } catch (e: Throwable) {
//            val notAllowed = control.exceptionIsNotAllowed(e)
//
//            if (notAllowed) {
//               throw e
//            }
//         }
//
//         control.step()
//      }
//   } finally {
//      errorCollector.setCollectionMode(originalAssertionMode)
//   }
//
//   throw failure(control.buildFailureMessage())
//}
//
//@ExperimentalKotest
//suspend operator fun <T> GenericEventuallyConfig<T>.invoke(f: ConcurrencyProducer<T>): T {
//   delay(initialDelay) // TODO: should the initialDelay count against the patienceConfig.duration?
//
//   val originalAssertionMode = errorCollector.getCollectionMode()
//   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)
//
//   val control = EventuallyControl(this)
//
//   try {
//      while (control.attemptsRemaining() || control.isLongWait()) {
//         try {
//            val result = f()
//            val state =
//               EventuallyState(result, control.start, control.end, control.times, control.firstError, control.lastError)
//
//            when (shortCircuit?.invoke(result)) {
//               null, false -> Unit
//               true -> throw EventuallyShortCircuitException("The provided 'shortCircuit' function was triggered: $state")
//            }
//
//            when (listener?.onEval(state)) {
//               null, true -> return result
//               false -> control.predicateFailedTimes++
//            }
//         } catch (e: Throwable) {
//            val notAllowed = control.exceptionIsNotAllowed(e)
//            listener?.onEval(
//               EventuallyState(
//                  null,
//                  control.start,
//                  control.end,
//                  control.times,
//                  control.firstError,
//                  control.lastError
//               )
//            )
//
//            if (notAllowed) {
//               throw e
//            }
//         }
//
//         control.step()
//      }
//   } finally {
//      errorCollector.setCollectionMode(originalAssertionMode)
//   }
//
//   throw failure(control.buildFailureMessage())
//}
