package io.kotest.framework.concurrency

import io.kotest.common.ExperimentalKotest

@ExperimentalKotest
fun interface UntilListener<in T> {
   fun onEval(t: T): Boolean

   companion object {
      val noop = UntilListener<Any?> { true }
   }
}

