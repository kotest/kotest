package io.kotlintest.matchers.concurrent

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

fun <A> shouldCompleteWithin(timeout: Long, unit: TimeUnit, thunk: () -> A) {

  val ref = AtomicReference<A>(null)
  val latch = CountDownLatch(1)
  val t = thread {
    val a = thunk()
    ref.set(a)
    latch.countDown()
  }

  if (!latch.await(timeout, unit)) {
    t.interrupt()
    throw AssertionError("Test should have completed within $timeout/$unit")
  }

  ref.get()
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
    throw AssertionError("Expected test to timeout for $timeout/$unit")
  }
}