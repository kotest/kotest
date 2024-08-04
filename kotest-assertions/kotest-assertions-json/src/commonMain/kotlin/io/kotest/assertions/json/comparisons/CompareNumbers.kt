package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.JsonError
import io.kotest.assertions.json.JsonNode
import io.kotest.assertions.json.NumberFormat

internal fun compareNumbers(
  path: List<String>,
  expected: JsonNode.NumberNode,
  actual: JsonNode,
  options: CompareJsonOptions
): JsonError? {
   return when (actual) {
      is JsonNode.NumberNode -> {
         when (options.numberFormat) {
            NumberFormat.Strict -> {
               if (expected.content != actual.content) JsonError.UnequalValues(path, expected.content, actual.content)
               else null
            }

            NumberFormat.Lenient -> compareNumberNodes(path, expected, actual)
         }
      }

      is JsonNode.StringNode -> {
         if (options.typeCoercion.isEnabled() && actual.contentIsNumber()) compareNumberNodes(
           path,
           expected,
           actual.toNumberNode()
         )
         else JsonError.IncompatibleTypes(path, expected, actual)
      }

      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}
