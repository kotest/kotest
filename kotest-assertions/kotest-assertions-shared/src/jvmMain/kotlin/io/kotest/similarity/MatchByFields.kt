package io.kotest.similarity

import java.math.BigDecimal
import java.math.MathContext

internal fun matchByFields(field: String, expected: Any, actual: Any): ComparisonResult {
    require(expected::class == actual::class) {
        "Expected and Actual should be of same type, were ${expected::class.qualifiedName} and ${actual::class.qualifiedName}"
    }
    val fieldsReader = FieldsReader()
    val expectedFields = fieldsReader.fieldsOf(expected)
    val actualFields = fieldsReader.fieldsOf(actual)
    val comparisons = expectedFields.mapIndexed{
        index, expectedField ->
        val actualField = actualFields[index]
       VanillaDistanceCalculator.compare(expectedField.name, expectedField.value, actualField.value)
    }
    val matches = comparisons.count { it is Match }
    val distance = Distance(BigDecimal(matches)
        .divide(BigDecimal(expectedFields.size), MathContext(2)))
    return MismatchByField(field, expected, actual, comparisons, distance)
}
