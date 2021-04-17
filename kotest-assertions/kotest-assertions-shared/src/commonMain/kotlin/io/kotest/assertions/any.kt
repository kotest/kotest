package io.kotest.assertions

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
suspend inline fun <T> any(crossinline assertions: suspend () -> T): T? {
   val (result, failures, assertionCount) = errorScope { assertions() }

   if (failures.isEmpty() && assertionCount == 0 || assertionCount > failures.size) {
      return result.getOrNull()
   }

   assertionCounter.set(assertionCount)
   errorCollector.collectOrThrow(failures + failure("Any expected at least one assertion to succeed but they all failed"))

   return null
}

suspend inline fun <T> any(t: T, crossinline assertions: suspend T.(T) -> Unit) = any {
   t.assertions(t)
   t
}
