package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.JsonError
import io.kotest.assertions.json.JsonNode

internal fun compareNull(path: List<String>, b: JsonNode): JsonError? {
   return when (b) {
      is JsonNode.NullNode -> null
      else -> JsonError.ExpectedNull(path, b)
   }
}
