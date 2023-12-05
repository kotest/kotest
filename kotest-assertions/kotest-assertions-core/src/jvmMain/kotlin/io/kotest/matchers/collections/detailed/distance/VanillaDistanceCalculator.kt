package io.kotest.matchers.collections.detailed.distance

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
    else -> AtomicMismatch(field, expected, actual)
}

