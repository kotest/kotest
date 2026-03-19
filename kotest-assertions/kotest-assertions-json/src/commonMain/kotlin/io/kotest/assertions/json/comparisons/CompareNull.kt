package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.JsonError
import io.kotest.assertions.json.JsonNode

internal fun compareNull(path: List<String>, b: JsonNode): List<JsonError> {
   return when (b) {
      is JsonNode.NullNode -> emptyList()
      else -> listOf(JsonError.ExpectedNull(path, b))
   }
}
