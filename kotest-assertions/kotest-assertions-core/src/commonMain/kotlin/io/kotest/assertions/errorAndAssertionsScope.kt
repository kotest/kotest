package io.kotest.assertions

import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.ErrorCollectionMode
import io.kotest.matchers.assertionCounter
import io.kotest.matchers.errorCollector
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

internal typealias Failures = List<Throwable>
internal typealias Assertions = Int

internal class AssertionBlockContextElement : AbstractCoroutineContextElement(Key) {
   companion object Key : CoroutineContext.Key<AssertionBlockContextElement>
}

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
internal suspend fun <T> errorAndAssertionsScope(block: suspend () -> T): Triple<Result<T>, Failures, Assertions> {
   if (coroutineContext[AssertionBlockContextElement] != null) {
      throw IllegalStateException("Assertion block functions one, any, and all are limited to a depth of 1")
   }

   val originalFailures = errorCollector.getAndReplace(listOf())
   val originalAssertions = assertionCounter.getAndReset()
   val originalMode = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Soft)

   val result = runCatching {
      withContext(AssertionBlockContextElement()) {
         block()
      }
   }

   errorCollector.setCollectionMode(originalMode)
   val resultFailures = errorCollector.getAndReplace(originalFailures)
   val resultAssertions = assertionCounter.getAndReset()

   repeat(originalAssertions) { assertionCounter.inc() }

   return Triple(result, resultFailures, resultAssertions)
}

