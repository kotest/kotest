package io.kotest.assertions

import io.kotest.common.ExperimentalKotest

/**
 * Runs multiple assertions and expects at least one to succeed, will suppress all exceptions otherwise.
 *
 * TODO: add experimental annotation
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
suspend inline fun <T> any(crossinline assertions: suspend () -> T): T? {
   val (result, failures, assertionCount) = errorScope { assertions() }
   assertionCounter.inc(assertionCount)

   if (assertionCount > failures.size || failures.isEmpty() && assertionCount == 0) {
      return result.getOrNull()
   }

   errorCollector.pushErrors(failures)
   errorCollector.collectOrThrow(failure("Any expected at least one assertion to succeed but they all failed"))
   return null
}

@ExperimentalKotest
suspend inline fun <T> any(t: T, crossinline assertions: suspend T.(T) -> Unit) = any {
   t.assertions(t)
   t
}
