package io.kotest.similarity

import io.kotest.equals.Equality
import io.kotest.submatching.topNWithTiesBy

actual fun<T> possibleMatchesForSet(
   passed: Boolean,
   expected: Set<T>,
   actual: Set<T>,
   verifier: Equality<T>?
): String {
   return when {
      passed -> ""
      actual.isEmpty() -> ""
      Equality.default<T>().name() == (verifier?.name() ?: Equality.default<T>().name()) -> {
         val possibleMatches = expected.flatMap {
            closestMatches(actual, it)
               .topNWithTiesBy(1) { it.comparisonResult.distance.distance }
               .map { it.comparisonResult.description() }
         }
         if(possibleMatches.isEmpty()) ""
         else "\nPossible matches:${possibleMatches.joinToString("\n")}"
      }
      else -> ""
   }
}
