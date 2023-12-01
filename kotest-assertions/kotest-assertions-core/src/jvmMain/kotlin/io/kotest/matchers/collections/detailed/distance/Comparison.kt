package io.kotest.matchers.collections.detailed.distance

import io.kotest.matchers.collections.detailed.PossibleMatch
import io.kotest.matchers.collections.detailed.distance.Distance.Companion.CompleteMatch
import io.kotest.matchers.collections.detailed.distance.Distance.Companion.CompleteMismatch


sealed interface ComparisonResult {
    fun description(): String
}

data class Match(
    val field: String,
    val value: Any?
): ComparisonResult {
    val distance
       get() = CompleteMatch
    override fun description() = "  $field = $value"
}

data class AtomicMismatch(
    val field: String,
    val expected: Any?,
    val actual: Any?,
    val distance: Distance = CompleteMismatch
): ComparisonResult {
    override fun description() = "  $field expected: $expected, but was: $actual"
}

data class MismatchByField(
   val field: String,
   val expected: Any,
   val actual: Any,
   val comparisonResults: List<ComparisonResult>,
   val distance: Distance
): ComparisonResult {
    override fun description() = """$field expected: $expected,
        |  but was: $actual,
        |  distance: ${distance.distance},
        |  fields:
        |${comparisonResults.joinToString("\n") { it.description() }}""".trimMargin()
}

fun possibleMatchDescription(possibleMatch: PossibleMatch): String = when(possibleMatch.comparisonResult) {
    is Match -> "actual[${possibleMatch.actual.index}] == expected[${possibleMatch.matchInExpected.index}], is: ${possibleMatch.actual.element}"
    is AtomicMismatch -> "actual[${possibleMatch.actual.index}] = ${possibleMatch.actual.element} is similar to\nexpected[${possibleMatch.matchInExpected.index}] = ${possibleMatch.matchInExpected.element}\n"
    is MismatchByField -> possibleMismatchByFieldDescription(possibleMatch)
}

fun possibleMismatchByFieldDescription(possibleMatch: PossibleMatch): String {
    val mismatchByField = (possibleMatch.comparisonResult as MismatchByField)
    val header = "actual[${possibleMatch.actual.index}] = ${possibleMatch.actual.element} is similar to\nexpected[${possibleMatch.matchInExpected.index}] = ${possibleMatch.matchInExpected.element}\n"
    val fields = mismatchByField.comparisonResults.joinToString("\n") {
        comparisonResultDescription(it)
    }
    return "$header\n$fields"
}

fun comparisonResultDescription(comparisonResult: ComparisonResult): String = when(comparisonResult) {
    is Match -> "\"${comparisonResult.field}\" = ${comparisonResult.value}"
    is AtomicMismatch -> "\"${comparisonResult.field}\" expected: ${comparisonResult.expected},\n but was: ${comparisonResult.actual}"
    else -> "Unknown $comparisonResult"
}
