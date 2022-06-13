package io.kotest.matchers.concurrent.suspension

import io.kotest.assertions.failure
import io.kotest.common.measureTimeMillisCompat
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Asserts that the given suspendable lambda completes within the given time.
 *
 *
 * Note: It does not works well within [assertSoftly]. If used within [assertSoftly]
 * and this assertion failed any subsequent assertion won't run.
 * */
suspend fun <A> shouldCompleteWithin(timeout: Long, unit: TimeUnit, thunk: suspend () -> A): A {
   val ref = AtomicReference<A>(null)

   try {
      withTimeout(unit.toMillis(timeout)) {
         val a = thunk()
         ref.set(a)
      }
      return ref.get()
   } catch (ex: TimeoutCancellationException) {
      throw failure("Test should have completed within $timeout/$unit")
   }
}

/**
 * Asserts that the given suspendable lambda completes within the given time range(inclusive of [from] and [to]).
 *
 *
 * Note: It does not works well within [assertSoftly]. If used within [assertSoftly]
 * and this assertion failed any subsequent assertion won't run.
 * */
suspend fun <A> shouldCompleteBetween(from: Long, to: Long, unit: TimeUnit, thunk: suspend () -> A): A {
   val ref = AtomicReference<A>(null)

   try {
      val timeElapsed = measureTimeMillisCompat {
         withTimeout(unit.toMillis(to)) {
            val a = thunk()
            ref.set(a)
         }
      }

      if (unit.toMillis(from) > timeElapsed) {
         throw failure("Test should not have completed before $from/$unit")
      }

      return ref.get()

   } catch (ex: TimeoutCancellationException) {
      throw failure("Test should have completed within $from/$unit to $to/$unit")
   }
}

/**
 * Asserts that the given suspendable lambda completes does not complete within given time).
 *
 *
 * Note: It does not works well within [assertSoftly]. If used within [assertSoftly]
 * and this assertion failed any subsequent assertion won't run.
 * */
suspend fun <A> shouldTimeout(timeout: Long, unit: TimeUnit, thunk: suspend () -> A) {
   try {
      withTimeout(unit.toMillis(timeout)) {
         thunk()
      }
      throw failure("Test should not have completed before $timeout/$unit")
   } catch (ex: TimeoutCancellationException) {

   }
}
