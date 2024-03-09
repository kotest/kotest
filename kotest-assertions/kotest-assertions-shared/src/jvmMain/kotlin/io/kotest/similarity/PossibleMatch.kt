package io.kotest.similarity

internal data class IndexedElement(
   val index: Int,
   val element: Any?
)

internal data class PossibleMatch(
   val matchInExpected: IndexedElement,
   val actual: IndexedElement,
   val comparisonResult: ComparisonResult
)

