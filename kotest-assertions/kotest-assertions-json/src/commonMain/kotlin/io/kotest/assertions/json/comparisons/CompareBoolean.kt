package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.JsonError
import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.TypeCoercion

/**
 * When comparing a boolean, if [CompareJsonOptions.typeCoercion] is [TypeCoercion.Enabled]
 * and the actual node is a text node with `"true"` or `"false"`, then we convert.
 */
internal fun compareBoolean(
   path: List<String>,
   expected: JsonNode.BooleanNode,
   actual: JsonNode,
   options: CompareJsonOptions
): JsonError? {
   return when {
      actual is JsonNode.BooleanNode -> compareBooleans(path, expected.value, actual.value)
      options.typeCoercion.isEnabled() && actual is JsonNode.StringNode -> when (actual.value) {
         "true" -> compareBooleans(path, expected.value, true)
         "false" -> compareBooleans(path, expected.value, false)
         else -> JsonError.UnequalValues(path, expected, actual)
      }

      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}
