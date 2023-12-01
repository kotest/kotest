package io.kotest.matchers.collections.detailed

fun<T> printMatches(leftList: List<T>, rightList: List<T>, matches: List<MatchResultsOfSubLists>): String {
    return with(StringBuilder()) {
        matches.forEach { append(if (it.match) printMatch(leftList, it) else printMismatch(leftList, rightList, it)) }
        toString()
    }
}

fun<T> printMatch(leftList: List<T>, match: MatchResultsOfSubLists): String{
    return with(StringBuilder("\nMatch:\n")) {
        for (index in 0 until match.leftRange.length()) {
            val leftIndex = match.leftRange.start + index
            append("expected[$leftIndex] == actual[${match.rightRange.start + index}]: ${leftList[leftIndex]}\n")
        }
        toString()
    }
}

fun<T> printMismatch(leftList: List<T>, rightList: List<T>, match: MatchResultsOfSubLists): String{
    return with(StringBuilder("\nMismatch:\n")) {
        if (!match.leftRange.isEmpty()) {
            for (index in 0 until match.leftRange.length()) {
                val leftIndex = match.leftRange.start + index
                append("expected[$leftIndex] = ${leftList[leftIndex]}\n")
            }
        }
        if (!match.rightRange.isEmpty()) {
            for (index in 0 until match.rightRange.length()) {
                val rightIndex = match.rightRange.start + index
                append("actual[$rightIndex] = ${rightList[rightIndex]}\n")
            }
        }
        toString()
    }
}

fun Iterable<String>.joinNotEmptyToString(separator: CharSequence): String {
   return this.filter{it.isNotEmpty()}.joinToString(separator)
}
