package io.kotest.assertions

import io.kotest.fp.Try

internal typealias Failures = List<Throwable>
internal typealias Assertions = Int

suspend fun <T> errorScope(block: suspend () -> T): Triple<Try<T>, Failures, Assertions> {
   val originalFailures = errorCollector.getAndReplace(listOf())
   val originalAssertions = assertionCounter.getAndReset()

   val result = Try { block() }

   val resultFailures = errorCollector.getAndReplace(originalFailures)
   val resultAssertions = assertionCounter.getAndReset()

   repeat(originalAssertions) { assertionCounter.inc() }

   return Triple(result, resultFailures, resultAssertions)
}
