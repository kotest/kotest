package io.kotest.assertions

import io.kotest.common.ExperimentalKotest

/**
 * Runs multiple assertions and expects exactly one to succeed, will suppress all exceptions otherwise.
 *
 * TODO: add experimental annotation
 *
 * ```
 *   either {
 *      "foo" shouldBe "bar"
 *      "foo" shouldBe "foo
 *      "foo" shouldBe "baz"
 *   }
 * ```
 */
@ExperimentalKotest
suspend inline fun <T> either(crossinline assertions: suspend () -> T): T? {
   val (result, failures, assertionsCount) = errorScope { assertions() }
   assertionCounter.inc(assertionsCount)

   if (assertionsCount < 2) {
      errorCollector.collectOrThrow(failures + failure("Either cannot ensure a mutual exclusion with less than two assertions"))
   }

   if (assertionsCount == failures.size + 1) {
      return result.getOrNull()
   }

   errorCollector.pushErrors(failures)

   val f = when {
      assertionsCount == failures.size -> failure("Either expected a single assertion to succeed, but none succeeded.")
      assertionsCount > failures.size + 1 -> failure("Either expected a single assertion to succeed, but more than one succeeded.")
      else -> failure("Either expected a single assertion to succeed, but there were more failures than assertions.")
   }

   errorCollector.pushErrorAndMaybeThrow(f)
   return null
}

/**
 * Runs multiple assertions and expects exactly one to succeed, will suppress all exceptions otherwise.
 * Returns the original value [t] on success for use in subsequent assertions.
 */
@ExperimentalKotest
suspend inline fun <T> either(t: T, crossinline assertions: suspend T.(T) -> Unit) = either {
   t.assertions(t)
   t
}
