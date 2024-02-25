package io.kotest.similarity

import io.kotest.assertions.print.print
import io.kotest.similarity.Distance.Companion.CompleteMatch
import io.kotest.similarity.Distance.Companion.CompleteMismatch
import java.math.BigDecimal

internal sealed interface ComparisonResult {
    val match: Boolean
    fun description(): String
}

internal data class Match(
    val field: String,
    val value: Any?
): ComparisonResult {
    val distance
       get() = CompleteMatch
    override fun description() = "  $field = ${value.print().value}"
    override val match: Boolean
      get() = true
}

internal data class AtomicMismatch(
    val field: String,
    val expected: Any?,
    val actual: Any?,
    val distance: Distance = CompleteMismatch
): ComparisonResult {
    override fun description() = "    \"$field\" expected: <${expected.print().value}>, but was: <${actual.print().value}>"
    override val match: Boolean
      get() = false
}

internal data class MismatchByField(
   val field: String,
   val expected: Any,
   val actual: Any,
   val comparisonResults: List<ComparisonResult>,
   val distance: Distance
): ComparisonResult {
    override fun description() = """$field expected: $expected,
        |  but was: $actual,
        |  The following fields did not match:
        |${comparisonResults.filter{ !it.match }.joinToString("\n    ") { it.description() }}""".trimMargin()
    override val match: Boolean
      get() = comparisonResults.all { it.match }
}

internal fun possibleMatchDescription(possibleMatch: PossibleMatch): String = when(possibleMatch.comparisonResult) {
    is Match -> "actual[${possibleMatch.actual.index}] == expected[${possibleMatch.matchInExpected.index}], is: ${possibleMatch.actual.element}"
    is AtomicMismatch -> "actual[${possibleMatch.actual.index}] = ${possibleMatch.actual.element} is similar to\nexpected[${possibleMatch.matchInExpected.index}] = ${possibleMatch.matchInExpected.element}\n"
    is MismatchByField -> possibleMismatchByFieldDescription(possibleMatch)
}

internal fun possibleMismatchByFieldDescription(possibleMatch: PossibleMatch): String {
    val mismatchByField = (possibleMatch.comparisonResult as MismatchByField)
    val header = "actual[${possibleMatch.actual.index}] = ${possibleMatch.actual.element} is similar to\nexpected[${possibleMatch.matchInExpected.index}] = ${possibleMatch.matchInExpected.element}\n"
    val fields = mismatchByField.comparisonResults.joinToString("\n") {
        comparisonResultDescription(it)
    }
    return "$header\n$fields"
}

internal fun comparisonResultDescription(comparisonResult: ComparisonResult): String = when(comparisonResult) {
    is Match -> "\"${comparisonResult.field}\" = ${comparisonResult.value}"
    is AtomicMismatch -> "\"${comparisonResult.field}\" expected: <${comparisonResult.expected}>,\n but was: <${comparisonResult.actual}>"
    else -> "Unknown $comparisonResult"
}
