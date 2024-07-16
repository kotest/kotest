package io.kotest.submatching

fun findPartialMatches(value: String, target: String, minLength: Int) =
   findPartialMatches(value.toList(), target.toList(), minLength)

fun<T> findPartialMatches(value: List<T>, target: List<T>, minLength: Int): List<PartialCollectionMatch<T>> {
   val indexes = toCharIndex(target)
   val matches = value.asSequence().mapIndexed { index, char ->
      index to char
   }.filter { pair -> pair.first + minLength <= value.size }
      .flatMap { pair ->
         matchedElements(indexes, pair)
      }.mapNotNull { matchedCharacter ->
         extendPartialMatchToRequiredLength(value, target, matchedCharacter, minLength)
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
      indexInValue = elementAtIndex.first,
      indexInTarget = index
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
   val maxLengthOfMatch = minOf(value.size - matchedElement.indexInValue, target.size - matchedElement.indexInTarget)
   return (1..maxLengthOfMatch).takeWhile { offset ->
      value[matchedElement.indexInValue + offset - 1] == target[matchedElement.indexInTarget + offset - 1]
   }.lastOrNull() ?: 0
}

data class MatchedCollectionElement(
   val indexInValue: Int,
   val indexInTarget: Int
)

data class PartialCollectionMatch<T>(
   val matchedElement: MatchedCollectionElement,
   val length: Int,
   val value: List<T>
) {
   val endOfMatchAtTarget: Int
      get() = matchedElement.indexInTarget + length - 1
   val partOfValue: List<T>
      get() = value.subList(matchedElement.indexInValue, matchedElement.indexInValue + length)
   val rangeOfValue: IntRange = rangeOfLength(matchedElement.indexInValue, length)
   val rangeOfTarget: IntRange = rangeOfLength(matchedElement.indexInTarget, length)

   companion object {
      fun rangeOfLength(start: Int, length: Int): IntRange = (start..<start+length)
   }
}

