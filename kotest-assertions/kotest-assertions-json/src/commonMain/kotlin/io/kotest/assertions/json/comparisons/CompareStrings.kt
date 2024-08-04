package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.JsonError

internal fun compareStrings(path: List<String>, expected: String, actual: String): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalStrings(path, expected, actual)
   }
}
