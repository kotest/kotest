package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.JsonError

internal fun compareBooleans(path: List<String>, expected: Boolean, actual: Boolean): List<JsonError> {
   return when (expected) {
      actual -> emptyList()
      else -> listOf(JsonError.UnequalBooleans(path, expected, actual))
   }
}
