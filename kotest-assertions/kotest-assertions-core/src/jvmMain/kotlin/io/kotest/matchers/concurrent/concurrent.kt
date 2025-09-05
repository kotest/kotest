package io.kotest.matchers.concurrent

import io.kotest.assertions.AssertionErrorBuilder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.time.Duration

fun <A> shouldCompleteWithin(duration: Duration, thunk: () -> A): A {
   return shouldCompleteWithin(duration.inWholeMilliseconds, TimeUnit.MILLISECONDS, thunk)
}

fun <A> shouldCompleteWithin(timeout: Long, unit: TimeUnit, thunk: () -> A): A {

   val ref = AtomicReference<A>(null)
   val throwableRef = AtomicReference<Throwable>(null)

   val latch = CountDownLatch(1)
   val t = thread {
      try {
         val a = thunk()
         ref.set(a)
      } catch (t: Throwable) {
         throwableRef.set(t)
      } finally {
         latch.countDown()
      }
   }

   if (!latch.await(timeout, unit)) {
      t.interrupt()
      AssertionErrorBuilder.fail("Test should have completed within $timeout/$unit")
   }

   throwableRef.get()?.let { throw it }
   return ref.get()
}

fun <A> shouldTimeout(duration: Duration, thunk: () -> A) {
   return shouldTimeout(duration.inWholeMilliseconds, TimeUnit.MILLISECONDS, thunk)
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

