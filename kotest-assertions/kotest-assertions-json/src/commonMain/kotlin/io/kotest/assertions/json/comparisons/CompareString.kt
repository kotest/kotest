package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.JsonError
import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.TypeCoercion

/**
 * When comparing a string, if [CompareJsonOptions.typeCoercion] is [TypeCoercion.Enabled]
 * we can convert the actual node to a string.
 */
internal fun compareString(
   path: List<String>,
   expected: JsonNode.StringNode,
   actual: JsonNode,
   options: CompareJsonOptions
): JsonError? {
   return when {
      actual is JsonNode.StringNode -> compareStrings(path, expected.value, actual.value)
      options.typeCoercion.isEnabled() -> when {
         actual is JsonNode.BooleanNode -> compareStrings(path, expected.value, actual.value.toString())
         actual is JsonNode.NumberNode && expected.contentIsNumber() -> compareNumberNodes(
            path,
            expected.toNumberNode(),
            actual
         )

         else -> JsonError.IncompatibleTypes(path, expected, actual)
      }

      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}
