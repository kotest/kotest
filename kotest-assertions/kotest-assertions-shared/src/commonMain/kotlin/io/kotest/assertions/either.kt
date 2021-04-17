package io.kotest.assertions

/**
 * Runs multiple assertions and expects exactly one to succeed, will suppress all exceptions otherwise.
 *
 * ```
 *   either {
 *      "foo" shouldBe "bar"
 *      "foo" shouldBe "foo
 *      "foo" shouldBe "baz"
 *   }
 * ```
 */
suspend inline fun <T> either(crossinline assertions: suspend () -> T): T? {
   val (result, failures, assertionsCounter) = errorScope { assertions() }

   if (assertionsCounter < 2) {
      errorCollector.collectOrThrow(failures + failure("Either cannot ensure a mutual exclusion with less than two assertions"))
   }

   if (assertionsCounter == failures.size + 1) {
      return result.getOrNull()
   }

   assertionCounter.set(assertionsCounter)

   when {
      assertionsCounter > failures.size + 1 ->
         errorCollector.collectOrThrow(failures + failure("Either expected a single assertion to succeed but more than one succeeded"))
      assertionsCounter < failures.size - 1 ->
         errorCollector.collectOrThrow(failures + failure("Either expected a single assertion to succeed but they all failed"))
      else ->
         errorCollector.collectOrThrow(failures)
   }

   return null
}

suspend inline fun <T> either(t: T, crossinline assertions: suspend T.(T) -> Unit) = either {
   t.assertions(t)
   t
}
