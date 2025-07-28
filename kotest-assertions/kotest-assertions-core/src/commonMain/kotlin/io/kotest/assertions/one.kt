package io.kotest.assertions

import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.assertionCounter
import io.kotest.matchers.errorCollector

/**
 * Executes the given lambda and expects exactly one assertion to succeed.
 * If zero, two or more assertions succeed then this will cause this block to fail.
 *
 * ```
 *   one {
 *      "foo" shouldBe "bar"
 *      "foo" shouldBe "foo
 *      "foo" shouldBe "baz"
 *   }
 * ```
 */
@ExperimentalKotest
suspend fun <T> one(assertions: suspend () -> T): T {
   val (result, failures, assertionsCount) = errorAndAssertionsScope { assertions() }
   assertionCounter.inc(assertionsCount)

   if (assertionsCount < 2) {
      errorCollector.collectOrThrow(
         failures + AssertionErrorBuilder.create()
            .withMessage("One cannot ensure a mutual exclusion with less than two assertions")
            .build()
      )
   }

   if (assertionsCount == failures.size + 1) {
      return result.getOrThrow()
   }

   errorCollector.pushErrors(failures)

   val f = when {
      assertionsCount == failures.size -> AssertionErrorBuilder.create()
         .withMessage("One expected a single assertion to succeed, but none succeeded.")
         .build()

      assertionsCount > failures.size + 1 -> AssertionErrorBuilder.create()
         .withMessage("One expected a single assertion to succeed, but more than one succeeded.")
         .build()

      else -> AssertionErrorBuilder.create()
         .withMessage("One expected a single assertion to succeed, but there were more failures than assertions.")
         .build()
   }

   errorCollector.pushErrorAndMaybeThrow(f)
   throw f // one/all/any won't respect softly for now
}

/**
 * Executes the given lambda and expects exactly one assertion to succeed.
 * If zero, two or more assertions suceed then this will cause an exception.
 *
 * Returns the original value [t] on success for use in subsequent assertions.
 */
@ExperimentalKotest
suspend fun <T> one(t: T, assertions: suspend T.(T) -> Unit) = one {
   t.assertions(t)
   t
}
