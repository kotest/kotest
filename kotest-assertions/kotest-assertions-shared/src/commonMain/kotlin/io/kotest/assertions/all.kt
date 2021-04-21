package io.kotest.assertions

import io.kotest.common.ExperimentalKotest

/**
 * Runs multiple assertions and throw a composite error with all failures
 *
 * This method will run all the assertions inside [assertions] block, and will collect all failures that may happen.
 * It will then collect them into a single throwable and throw, or return the result if no assertions failed.
 *
 * ```
 *     // All assertions below are going to be executed, even when one or multiple fail.
 *     // All the failures are then collected and thrown in one single throwable.
 *     all {
 *         "foo" shouldBe "bar"
 *         "foo" shouldBe "foo
 *         "foo" shouldBe "baz"
 *     }
 * ```
 */
@ExperimentalKotest
suspend fun <T> all(assertions: suspend () -> T): T {
   // Handle the edge case of nested calls to this function by only calling throwCollectedErrors in the
   // outermost verifyAll block
   if (errorCollector.getCollectionMode() == ErrorCollectionMode.Soft) return assertions()
   errorCollector.setCollectionMode(ErrorCollectionMode.Soft)
   return try {
      assertions()
   } finally {
      // In case if any exception is thrown from assertions block setting errorCollectionMode back to hard
      // so that it won't remain soft for others tests. See https://github.com/kotest/kotest/issues/1932
      errorCollector.setCollectionMode(ErrorCollectionMode.Hard)
      errorCollector.throwCollectedErrors()
   }
}

inline fun <T> assertSoftly(assertions: () -> T): T {
   // Handle the edge case of nested calls to this function by only calling throwCollectedErrors in the
   // outermost verifyAll block
   if (errorCollector.getCollectionMode() == ErrorCollectionMode.Soft) return assertions()
   errorCollector.setCollectionMode(ErrorCollectionMode.Soft)
   return try {
      assertions()
   } finally {
      // In case if any exception is thrown from assertions block setting errorCollectionMode back to hard
      // so that it won't remain soft for others tests. See https://github.com/kotest/kotest/issues/1932
      errorCollector.setCollectionMode(ErrorCollectionMode.Hard)
      errorCollector.throwCollectedErrors()
   }
}

/**
 * Runs multiple assertions and throw a composite error with all failures.
 *
 * This method will run all the assertions inside [assertions] block, and will collect all failures that may happen.
 * It will then collect them into a single throwable and throw, or return the result if no assertions failed.
 *
 * ```
 *     // All assertions below are going to be executed, even when one or multiple fail.
 *     // All the failures are then collected and thrown in one single throwable.
 *     assertSoftly("foo") {
 *         this[2] shouldBe 'o'
 *         length shouldBeLessThan 5
 *     }
 * ```
 */
@ExperimentalKotest
suspend fun <T> all(t: T, assertions: suspend T.(T) -> Unit): T {
   return all {
      t.assertions(t)
      t
   }
}

inline fun <T> assertSoftly(t: T, assertions: T.(T) -> Unit): T {
   return assertSoftly {
      t.assertions(t)
      t
   }
}
