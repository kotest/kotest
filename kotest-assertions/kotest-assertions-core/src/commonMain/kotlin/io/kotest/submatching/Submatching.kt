package io.kotest.submatching

fun findPartialMatchesInString(expected: String, value: String, minLength: Int) =
   findPartialMatches(expected.toList(), value.toList(), minLength)

fun<T> findPartialMatches(expected: List<T>, value: List<T>, minLength: Int): List<PartialCollectionMatch<T>> {
   val indexes = toCharIndex(value)
   val matches = expected.asSequence().mapIndexed { index, char ->
      index to char
   }.filter { pair -> pair.first + minLength <= expected.size }
      .flatMap { pair ->
         matchedElements(indexes, pair)
      }.mapNotNull { matchedCharacter ->
         extendPartialMatchToRequiredLength(expected, value, matchedCharacter, minLength)
      }.toList()
   return removeShorterMatchesWithSameEnd(matches)
}

internal fun<T> toCharIndex(value: Collection<T>): Map<T, List<Int>> {
   return value.mapIndexed { index, element ->
      index to element
   }.groupBy(keySelector = { it.second }, valueTransform = { it.first })
}

internal fun <T> matchedElements(
   indexes: Map<T, List<Int>>,
   elementAtIndex: Pair<Int, T>
) = indexes[elementAtIndex.second]?.map { index ->
   MatchedCollectionElement(
      startIndexInExpected = elementAtIndex.first,
      startIndexInValue = index
   )
} ?: listOf()

internal fun <T> extendPartialMatchToRequiredLength(
   value: List<T>,
   target: List<T>,
   matchedElement: MatchedCollectionElement,
   minLength: Int
): PartialCollectionMatch<T>? {
   val lengthOfMatch = lengthOfMatch(value, target, matchedElement)
   return if (lengthOfMatch >= minLength) {
      PartialCollectionMatch(
         matchedElement,
         lengthOfMatch,
         value
      )
   } else null
}

internal fun<T> removeShorterMatchesWithSameEnd(
   matches: List<PartialCollectionMatch<T>>
): List<PartialCollectionMatch<T>> {
   val matchesGroupedByEnd = matches.groupBy {
      it.endOfMatchAtTarget
   }
   return matchesGroupedByEnd.values.map { matchesWithSameEnd ->
      matchesWithSameEnd.maxBy { it.length }
   }
}

internal fun<T> lengthOfMatch(
   value: List<T>, target: List<T>, matchedElement: MatchedCollectionElement
): Int {
   val maxLengthOfMatch = minOf(value.size - matchedElement.startIndexInExpected, target.size - matchedElement.startIndexInValue)
   return (1..maxLengthOfMatch).takeWhile { offset ->
      value[matchedElement.startIndexInExpected + offset - 1] == target[matchedElement.startIndexInValue + offset - 1]
   }.lastOrNull() ?: 0
}

data class MatchedCollectionElement(
   val startIndexInExpected: Int,
   val startIndexInValue: Int
)

data class PartialCollectionMatch<T>(
   val matchedElement: MatchedCollectionElement,
   val length: Int,
   val value: List<T>
) {
   val endOfMatchAtTarget: Int
      get() = matchedElement.startIndexInValue + length - 1
   val partOfValue: List<T>
      get() = value.subList(matchedElement.startIndexInExpected, matchedElement.startIndexInExpected + length)
   val rangeOfExpected: IntRange = rangeOfLength(matchedElement.startIndexInExpected, length)
   val rangeOfValue: IntRange = rangeOfLength(matchedElement.startIndexInValue, length)

   fun indexIsInValue(index: Int) = index in rangeOfValue
   fun indexInExpected(indexInValue: Int) = matchedElement.startIndexInExpected +
      (indexInValue - matchedElement.startIndexInValue)

   companion object {
      fun rangeOfLength(start: Int, length: Int): IntRange = (start..<start+length)
   }
}

