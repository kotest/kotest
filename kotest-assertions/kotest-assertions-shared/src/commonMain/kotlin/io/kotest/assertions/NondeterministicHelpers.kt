package io.kotest.assertions

typealias SuspendingPredicate<T> = suspend (T) -> Boolean

typealias SuspendingProducer<T> = suspend () -> T

interface NondeterministicListener<in T> {
   suspend fun onEval(t: T)

   companion object {
      val noop = object : NondeterministicListener<Any?> {
         override suspend fun onEval(t: Any?) { }
      }
   }
}

fun <T> nondeterministicListener(f: suspend (T) -> Unit) = object : NondeterministicListener<T> {
   override suspend fun onEval(t: T) = f(t)
}
