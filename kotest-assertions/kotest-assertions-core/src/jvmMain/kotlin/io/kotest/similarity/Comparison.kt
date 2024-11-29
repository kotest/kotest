package io.kotest.similarity

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.print.print
import io.kotest.similarity.Distance.Companion.CompleteMatch
import io.kotest.similarity.Distance.Companion.CompleteMismatch
import java.math.BigDecimal

internal sealed interface ComparisonResult {
    val match: Boolean
    fun description(): String
    val canBeSimilar: Boolean
    val distance: Distance
}

internal data class Match(
    val field: String,
    val value: Any?
): ComparisonResult {
    override val distance
       get() = CompleteMatch
    override fun description() = "  $field = ${value.print().value}"
    override val match: Boolean
      get() = true
    override val canBeSimilar: Boolean
      get() = false
}

internal data class AtomicMismatch(
    val field: String,
    val expected: Any?,
    val actual: Any?,
    override val distance: Distance = CompleteMismatch
): ComparisonResult {
    override fun description() = "    \"$field\" expected: <${expected.print().value}>, but was: <${actual.print().value}>"
    override val match: Boolean
      get() = false
    override val canBeSimilar: Boolean
      get() = false
}

internal data class StringMismatch(
   val field: String,
   val expected: String,
   val actual: String,
   val mismatchDescription: String,
   override val distance: Distance,
): ComparisonResult {
   override fun description() = if(distance.distance >
      BigDecimal(AssertionsConfig.similarityThresholdInPercentForStrings.value) * Distance.PERCENT_TO_DISTANCE)
      "    ${quotedIfNotEmpty(field)} expected: <${expected.print().value}>, found a similar value: <${actual.print().value}>\n$mismatchDescription"
   else "    ${quotedIfNotEmpty(field)} expected: <${expected.print().value}>, but was: <${actual.print().value}>"

   override val match: Boolean
      get() = false
   override val canBeSimilar: Boolean
      get() = true
}

internal fun quotedIfNotEmpty(value: String) = if(value.isEmpty()) "" else "\"$value\""

internal data class MismatchByField(
    val field: String,
    val expected: Any,
    val actual: Any,
    val comparisonResults: List<ComparisonResult>,
    override val distance: Distance
): ComparisonResult {
    override fun description() = """$field expected: $expected,
        |  but was: $actual,
        |  The following fields did not match:
        |${comparisonResults.filter{ !it.match }.joinToString("\n    ") { it.description() }}""".trimMargin()
    override val match: Boolean
      get() = comparisonResults.all { it.match }
    override val canBeSimilar: Boolean
      get() = true
}

