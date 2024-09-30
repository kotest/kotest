package io.kotest.similarity

import io.kotest.equals.Equality

actual fun<T> possibleMatchesForSet(
   passed: Boolean,
   expected: Set<T>,
   actual: Set<T>,
   verifier: Equality<T>?
): String = ""
