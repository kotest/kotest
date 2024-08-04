package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.JsonError
import io.kotest.assertions.json.JsonNode

/**
 * Compares two JSON trees, returning a detailed error message if they differ.
 */
internal fun compare(
  path: List<String>,
  expected: JsonNode,
  actual: JsonNode,
  options: CompareJsonOptions
): JsonError? {
   return when (expected) {
      is JsonNode.ObjectNode -> when (actual) {
         is JsonNode.ObjectNode -> compareObjects(path, expected, actual, options)
         else -> JsonError.ExpectedObject(path, actual)
      }

      is JsonNode.ArrayNode -> when (actual) {
         is JsonNode.ArrayNode -> compareArrays(path, expected, actual, options)
         else -> JsonError.ExpectedArray(path, actual)
      }

      is JsonNode.BooleanNode -> compareBoolean(path, expected, actual, options)
      is JsonNode.StringNode -> compareString(path, expected, actual, options)
      is JsonNode.NumberNode -> compareNumbers(path, expected, actual, options)
      JsonNode.NullNode -> compareNull(path, actual)
   }
}
