package io.kotest.engine

import kotlinx.coroutines.delay
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

fun main() {
   launch2 {
      repeat(10) {
         println("hello on " + Thread.currentThread())
         delay(10)
      }
   }
   println("world on " + Thread.currentThread())
}

fun launch2(block: suspend () -> Unit) {
   block.startCoroutine(object : Continuation<Unit> {
      override val context: CoroutineContext get() = EmptyCoroutineContext
      override fun resumeWith(result: Result<Unit>) {
         result.fold({ println("resumed with result") }, { println("Coroutine failed: $it") })
      }
   })
}
