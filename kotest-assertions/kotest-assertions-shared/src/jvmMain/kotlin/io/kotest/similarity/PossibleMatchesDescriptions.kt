package io.kotest.similarity

actual fun<T> possibleMatchesDescriptions(expected: Set<T>, actual: T): List<String>  {
   val possibleMatches = closestMatches(expected, actual)
   return possibleMatches.map {
      it.comparisonResult.description()
   }.filter { it.isNotEmpty() }
}

