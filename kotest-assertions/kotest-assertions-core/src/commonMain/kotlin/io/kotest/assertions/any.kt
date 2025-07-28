package io.kotest.assertions

import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.assertionCounter
import io.kotest.matchers.errorCollector

/**
 * Runs multiple assertions and expects at least one to succeed, will suppress all exceptions otherwise.
 *
 * ```
 *   any {
 *      "foo" shouldBe "bar"
 *      "foo" shouldBe "foo
 *      "foo" shouldBe "baz"
 *   }
 * ```
 */
@ExperimentalKotest
suspend fun <T> any(assertions: suspend () -> T): T {
   val (result, failures, assertionCount) = errorAndAssertionsScope { assertions() }
   assertionCounter.inc(assertionCount)

   if (assertionCount > failures.size || failures.isEmpty() && assertionCount == 0) {
      return result.getOrThrow()
   }

   val error = AssertionErrorBuilder.create()
      .withMessage("Any expected at least one assertion to succeed but they all failed")
      .build()

   errorCollector.pushErrors(failures)
   errorCollector.pushErrorAndMaybeThrow(error)
   throw error
}

@ExperimentalKotest
suspend fun <T> any(t: T, assertions: suspend T.(T) -> Unit) = any {
   t.assertions(t)
   t
}
