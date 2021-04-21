package io.kotest.assertions

import io.kotest.common.ExperimentalKotest
import io.kotest.fp.Try

internal typealias Failures = List<Throwable>
internal typealias Assertions = Int

/**
 * [errorAndAssertionsScope] runs [block] in a "clean" scope.
 * The original error and assertion counts are stored and set to empty before the [block] is run.
 * Once the block is executed, the result [T], the [Failures], and the number of [Assertions] are stored for return.
 * The original error and assertion counts are replaced into their respective tracking systems [errorCollector] and [assertionCounter].
 *
 * The calling function is responsible for inserting the resultant [Failures] and [Assertions] into [errorCollector] and [assertionCounter] if appropriate.
 *
 * @return The result [T] of the block function, the [Failures] that block had, and the number of [Assertions] executed.
 */
@ExperimentalKotest
internal suspend fun <T> errorAndAssertionsScope(block: suspend () -> T): Triple<Try<T>, Failures, Assertions> {
   val originalFailures = errorCollector.getAndReplace(listOf())
   val originalAssertions = assertionCounter.getAndReset()
   val originalMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Soft)

   val result = Try { block() }

   errorCollector.setCollectionMode(originalMode)
   val resultFailures = errorCollector.getAndReplace(originalFailures)
   val resultAssertions = assertionCounter.getAndReset()

   repeat(originalAssertions) { assertionCounter.inc() }

   return Triple(result, resultFailures, resultAssertions)
}

/**
 * Pushes the provided [error] onto the [errorCollector] and throws if the configured collection mode is [ErrorCollectionMode.Hard]
 */
@ExperimentalKotest
internal fun ErrorCollector.pushErrorAndMaybeThrow(error: Throwable) {
   pushError(error)

   if (getCollectionMode() == ErrorCollectionMode.Hard) {
      throwCollectedErrors()
   }
}
