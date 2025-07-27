package io.kotest.similarity

import io.kotest.equals.Equality

actual fun <T> possibleMatchesDescription(actual: Set<T>, expected: T): String = ""

actual fun <T> possibleMatchesForSet(
   passed: Boolean,
   expected: Set<T>,
   actual: Set<T>,
   verifier: Equality<T>?
): String = ""
