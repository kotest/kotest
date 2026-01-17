package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.JsonError
import io.kotest.assertions.json.JsonNode

internal fun compareNumberNodes(
  path: List<String>,
  expected: JsonNode.NumberNode,
  actual: JsonNode.NumberNode
): List<JsonError> {

   return when {
      expected.lenientEquals(actual) -> emptyList()
      else -> listOf(JsonError.UnequalValues(path, expected.content, actual.content))
   }
}
