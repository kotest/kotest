package io.kotest.assertions

import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

typealias SuspendingPredicate<T> = suspend (T) -> Boolean

typealias SuspendingProducer<T> = suspend () -> T

@OptIn(ExperimentalTime::class)
data class NondeterministicState (
   val start: TimeMark, val end: TimeMark, val times: Int, val firstError: Throwable?, val lastError: Throwable?
)

fun interface NondeterministicListener<in T> {
   fun onEval(t: T, state: NondeterministicState)

   companion object {
      val noop = NondeterministicListener<Any?> { _, _ -> }
   }
}
