package io.kotest.assertions.similarity

import io.kotest.equals.Equality

internal expect fun <T> possibleMatchesDescription(actual: Set<T>, expected: T): String

internal expect fun <T> possibleMatchesForSet(
   passed: Boolean,
   expected: Set<T>,
   actual: Set<T>,
   verifier: Equality<T>?
): String

