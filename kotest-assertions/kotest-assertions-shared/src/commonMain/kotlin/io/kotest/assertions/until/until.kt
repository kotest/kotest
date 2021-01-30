package io.kotest.assertions.until

import io.kotest.assertions.SuspendingPredicate
import io.kotest.assertions.SuspendingProducer
import io.kotest.assertions.timing.eventually
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@Deprecated("Use NondeterministicListener")
interface UntilListener<in T> {
   fun onEval(t: T)

   companion object {
      val noop = object : UntilListener<Any?> {
         override fun onEval(t: Any?) {}
      }
   }
}

@Deprecated("Use nondeterministicListener")
fun <T> untilListener(f: (T) -> Unit) = object : UntilListener<T> {
   override fun onEval(t: T) {
      f(t)
   }
}

@OptIn(ExperimentalTime::class)
@Deprecated(
   "Use eventually or Eventually.invoke",
   ReplaceWith(
      "eventually(duration, interval, f = f)",
      "io.kotest.assertions.timing.eventually"
   )
)
suspend fun until(duration: Duration, interval: Interval = 1.seconds.fixed(), f: suspend () -> Boolean) =
   eventually(duration, interval, f = f)

@OptIn(ExperimentalTime::class)
@Deprecated(
   "Use eventually or Eventually.invoke",
   ReplaceWith(
      "eventually(duration, 1.seconds.fixed(), predicate = predicate, f = f)",
      "io.kotest.assertions.timing.eventually",
      "kotlin.time.seconds"
   )
)
suspend fun <T> until(duration: Duration, predicate: SuspendingPredicate<T>, f: SuspendingProducer<T>): T =
   eventually(duration, 1.seconds.fixed(), predicate = predicate, f = f)

@OptIn(ExperimentalTime::class)
@Deprecated(
   "Use eventually or Eventually.invoke",
   ReplaceWith(
      "eventually(duration, interval, predicate = predicate, f = f)",
      "io.kotest.assertions.timing.eventually"
   )
)
suspend fun <T> until(
   duration: Duration,
   interval: Interval,
   predicate: SuspendingPredicate<T>,
   f: SuspendingProducer<T>
): T =
   eventually(duration, interval, predicate = predicate, f = f)

@OptIn(ExperimentalTime::class)
@Deprecated(
   "Use eventually", ReplaceWith(
      "eventually(duration, interval, listener = { it, _ -> listener.onEval(it) }, predicate = predicate, f = f)",
      "io.kotest.assertions.timing.eventually"
   )
)
suspend fun <T> until(
   duration: Duration,
   interval: Interval,
   predicate: SuspendingPredicate<T>,
   listener: UntilListener<T>,
   f: SuspendingProducer<T>
): T =
   eventually(duration, interval, listener = { it, _ -> listener.onEval(it) }, predicate = predicate, f = f)
