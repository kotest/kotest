package io.kotest.assertions

import io.kotest.common.ExperimentalKotest
import io.kotest.fp.Try

internal typealias Failures = List<Throwable>
internal typealias Assertions = Int

@ExperimentalKotest
suspend fun <T> errorScope(block: suspend () -> T): Triple<Try<T>, Failures, Assertions> {
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

fun ErrorCollector.pushErrorAndMaybeThrow(error: Throwable) {
   pushError(error)

   if (getCollectionMode() == ErrorCollectionMode.Hard) {
      throwCollectedErrors()
   }
}
