package io.kotest.similarity

internal object VanillaDistanceCalculator: IDistanceCalculator {
    override fun compare(field: String, expected: Any?, actual: Any?): ComparisonResult = when {
        expected == null && actual == null -> Match(field, null)
        expected != null && actual != null -> matchNotNull(field, expected, actual)
        else -> AtomicMismatch(field, expected, actual)
    }
}

internal fun matchNotNull(field: String, expected: Any, actual: Any): ComparisonResult = when {
    expected == actual -> Match(field, expected)
    expected::class.isData && expected.javaClass == actual.javaClass -> matchByFields(field, expected, actual)
    expected is String && actual is String -> matchNotNullStrings(field, expected, actual)
    else -> AtomicMismatch(field, expected, actual)
}

