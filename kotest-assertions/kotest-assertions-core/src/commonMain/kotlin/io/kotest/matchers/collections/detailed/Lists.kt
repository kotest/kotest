package io.kotest.matchers.collections.detailed

internal fun <T> describeListsMismatch(expected: List<T>, actual: List<T>): String {
   val listMatcher = ListMatcher()
   val results = listMatcher.match(expected, actual)
   return if (results.size == 1 && results[0].match) {
      ""
   } else {
      printMatches(expected, actual, results)
   }
}

internal data class TailOfList<T>(val items: List<T>, val offset: Int = 0){
   init {
      require(0 <= offset && offset <= items.size) {
         "Offset should be between 0 and ${items.size}, was $offset"
      }
   }

    fun tail(): TailOfList<T> {
        return if(offset < items.size) TailOfList(items, offset+1)
        else this
    }

    fun isOver() = (offset == items.size)

    fun onLastItem() = (offset == (items.size-1))

    fun rangeOfIndexes() = offset until items.size

    fun currentElement(): T = if(isOver()) throw IllegalStateException("IsOver, no current item") else items[offset]
}

internal enum class BranchDirection { CHANGE_LEFT_TAIL, CHANGE_BOTH_TAILS, CHANGE_RIGHT_TAIL }

private val BRANCH_IN_ALL_DIRECTIONS = BranchDirection.values().toSet()

internal class ListMatcher {
    fun <T> match(expected: List<T>,
                     actual: List<T>,
                     matcher: (left: T, right: T) -> Boolean = {left, right -> left == right}): List<MatchResultsOfSubLists> {
        val matches = matches(TailOfList(expected), TailOfList(actual), matcher= matcher)
        return bestMatch(matches)
    }

    fun <T> matches(expected: TailOfList<T>,
                          actual: TailOfList<T>,
                          branchDirections: Set<BranchDirection> = BRANCH_IN_ALL_DIRECTIONS,
                          matcher: (left: T, right: T) -> Boolean):
            List<List<MatchResultsOfSubLists>> {
        return when {
            (expected.onLastItem() && actual.onLastItem()) ->
                getNewTailOffLastItems(expected, actual, matcher = matcher)

            (expected.isOver() || actual.isOver()) ->
                getMismatchedTails(expected, actual)

            matcher(expected.currentElement(), actual.currentElement()) ->
                matchCurrentItemsAndCompareTails(MATCH, expected, actual, matcher = matcher)

            else -> branchOnMismatch(expected, actual, branchDirections, matcher = matcher)
        }
    }

    private fun <T> branchOnMismatch(expected: TailOfList<T>, actual: TailOfList<T>,
                                           branchDirections: Set<BranchDirection>,
                                           matcher: (left: T, right: T) -> Boolean): List<List<MatchResultsOfSubLists>> {
        val allTails: MutableList<List<MatchResultsOfSubLists>> = mutableListOf()
        if(BranchDirection.CHANGE_LEFT_TAIL in branchDirections) {
            allTails.addAll(nextOnLeftAndCompareTails(expected, actual, matcher = matcher))
        }
        allTails.addAll(matchCurrentItemsAndCompareTails(MISMATCH, expected, actual, matcher = matcher))
        if(BranchDirection.CHANGE_RIGHT_TAIL in branchDirections) {
            allTails.addAll(nextOnRightAndCompareTails(expected, actual, matcher = matcher))
        }
        return bestTwoMatches(allTails)
    }

    private fun <T> matchCurrentItemsAndCompareTails(itemsMatch: ItemsMatch,
                                                           expected: TailOfList<T>,
                                                           actual: TailOfList<T>,
                                                           matcher: (left: T, right: T) -> Boolean): List<List<MatchResultsOfSubLists>> {
        val branchDirections = if(itemsMatch.match) BRANCH_IN_ALL_DIRECTIONS else setOf(BranchDirection.CHANGE_BOTH_TAILS)
        val matchesForTail = matches(expected.tail(), actual.tail(), branchDirections, matcher = matcher)
        return addItemsMatchToMatchesForTail(itemsMatch, matchesForTail)
    }

    private fun <T> nextOnLeftAndCompareTails(expected: TailOfList<T>,
                                                    actual: TailOfList<T>,
                                                    matcher: (left: T, right: T) -> Boolean): List<List<MatchResultsOfSubLists>> {
        val matchesForTail = matches(expected.tail(), actual, setOf(BranchDirection.CHANGE_LEFT_TAIL), matcher = matcher)
        return addItemsMatchToMatchesForTail(LEFT_ITEM_ONLY, matchesForTail)
    }

    private fun <T> nextOnRightAndCompareTails(expected: TailOfList<T>,
                                                     actual: TailOfList<T>,
                                                     matcher: (left: T, right: T) -> Boolean): List<List<MatchResultsOfSubLists>> {
        val matchesForTail = matches(expected, actual.tail(), setOf(BranchDirection.CHANGE_RIGHT_TAIL), matcher = matcher)
        return addItemsMatchToMatchesForTail(RIGHT_ITEM_ONLY, matchesForTail)
    }

    private fun <T> getMismatchedTails(expected: TailOfList<T>, actual: TailOfList<T>) =
            listOf(mutableListOf(MatchResultsOfSubLists(false, expected.rangeOfIndexes(), actual.rangeOfIndexes())))

    private fun <T> getNewTailOffLastItems(expected: TailOfList<T>, actual: TailOfList<T>,
                                                 matcher: (left: T, right: T) -> Boolean): List<List<MatchResultsOfSubLists>> {
        val matched = matcher(expected.currentElement(), actual.currentElement())
        return listOf(mutableListOf(MatchResultsOfSubLists(matched, expected.rangeOfIndexes(), actual.rangeOfIndexes())))
    }

    fun addItemsMatchToMatchesForTail(itemsMatch: ItemsMatch, matchesForTail: List<List<MatchResultsOfSubLists>>):
            List<List<MatchResultsOfSubLists>> = matchesForTail.map { addItemsMatchToOneTail(itemsMatch, it) }

    fun addItemsMatchToOneTail(itemsMatch: ItemsMatch, matchesForTail: List<MatchResultsOfSubLists>):
            List<MatchResultsOfSubLists>{
        require(matchesForTail.isNotEmpty()) { "matchesForTail cannot be empty" }
        return when{
            matchesForTail[0].match == itemsMatch.match -> {
                val extendedFirstRange = when(itemsMatch.matchType){
                    MatchResultType.LEFT_ELEMENT_ONLY -> matchesForTail[0].extendLeftRangeBack()
                    MatchResultType.RIGHT_ELEMENT_ONLY -> matchesForTail[0].extendRightRangeBack()
                    MatchResultType.BOTH_ELEMENTS_PRESENT -> matchesForTail[0].extendBothRangesBack()
                }
                listOf(extendedFirstRange, *matchesForTail.drop(1).toTypedArray())
            }
            else -> {
                val newRangeMatch = matchesForTail[0].spawnNewRange(itemsMatch)
                listOf(newRangeMatch, *matchesForTail.toTypedArray())
            }
        }
    }
}

internal data class AssertionResult(val success: Boolean, val message: String = "")
