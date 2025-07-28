package io.kotest.matchers.concurrent

import io.kotest.assertions.AssertionErrorBuilder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

fun <A> shouldCompleteWithin(timeout: Long, unit: TimeUnit, thunk: () -> A): A {

   val ref = AtomicReference<A>(null)
   val latch = CountDownLatch(1)
   val t = thread {
      val a = thunk()
      ref.set(a)
      latch.countDown()
   }

   if (!latch.await(timeout, unit)) {
      t.interrupt()
      AssertionErrorBuilder.fail("Test should have completed within $timeout/$unit")
   }

   return ref.get()
}

fun <A> shouldTimeout(timeout: Long, unit: TimeUnit, thunk: () -> A) {

   val latch = CountDownLatch(1)

   val t = thread {
      thunk()
      latch.countDown()
   }

   // if the latch didn't complete in the time period then we did timeout
   val timedOut = !latch.await(timeout, unit)

   if (timedOut) {
      t.interrupt()
   } else {
      AssertionErrorBuilder.fail("Expected test to timeout for $timeout/$unit")
   }
}

