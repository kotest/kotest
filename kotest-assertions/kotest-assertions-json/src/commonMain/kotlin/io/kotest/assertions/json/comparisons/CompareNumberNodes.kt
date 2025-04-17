package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.JsonError
import io.kotest.assertions.json.JsonNode

internal fun compareNumberNodes(
  path: List<String>,
  expected: JsonNode.NumberNode,
  actual: JsonNode.NumberNode
): JsonError? {

   return when {
      expected.lenientEquals(actual) -> null
      else -> JsonError.UnequalValues(path, expected.content, actual.content)
   }
}
