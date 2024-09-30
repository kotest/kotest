package io.kotest.similarity

import io.kotest.equals.Equality

expect fun<T> possibleMatchesForSet(
   passed: Boolean,
   expected: Set<T>,
   actual: Set<T>,
   verifier: Equality<T>?
): String

