package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.JsonError

internal fun compareStrings(path: List<String>, expected: String, actual: String): List<JsonError> {
   return when (expected) {
      actual -> emptyList()
      else -> listOf(JsonError.UnequalStrings(path, expected, actual))
   }
}
