package io.kotest.similarity

actual fun<T> possibleMatchesDescription(expected: Set<T>, actual: T): String {
   val possibleMatches = closestMatches(expected, actual)
   return if(possibleMatches.isEmpty()) ""
   else {
      "\n${possibleMatches.joinToString("\n\n"){it.comparisonResult.description()}}"
   }
}

