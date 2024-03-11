package io.kotest.similarity

import io.kotest.equals.Equality

fun<T> possibleMatchesForSet(
   passed: Boolean,
   expected: Set<T>,
   actual: Set<T>,
   verifier: Equality<T>?
): String {
   return when {
      passed -> ""
      actual.isEmpty() -> ""
      Equality.default<T>().name() == (verifier?.name() ?: Equality.default<T>().name()) -> {
         val possibleMatches = actual.map {
            possibleMatchesDescription(expected, it)
         }.filter { it.isNotEmpty() }
         if(possibleMatches.isEmpty()) ""
         else "Possible matches:${possibleMatches.joinToString("\n")}"
      }
      else -> ""
   }
}
