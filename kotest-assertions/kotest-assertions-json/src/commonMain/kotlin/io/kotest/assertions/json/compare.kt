package io.kotest.assertions.json

import kotlin.math.abs

enum class CompareMode {
   @Suppress("unused")
   Strict, Lenient
}

enum class CompareOrder {
   Strict, Lenient
}

/**
 * Compares two json trees, returning a detailed error message if they differ.
 */
fun compare(a: JsonNode, b: JsonNode, mode: CompareMode) = compare(emptyList(), a, b, mode)

fun compare(path: List<String>, a: JsonNode, b: JsonNode, mode: CompareMode): JsonError? {
   return when (a) {
      is JsonNode.ObjectNode -> when (b) {
         is JsonNode.ObjectNode -> compareObjects(path, a, b, mode)
         else -> JsonError.ExpectedObject(path, b)
      }
      is JsonNode.ArrayNode -> when (b) {
         is JsonNode.ArrayNode -> compareArrays(path, a, b, mode)
         else -> JsonError.ExpectedArray(path, b)
      }
      is JsonNode.BooleanNode -> compareBoolean(path, a, b, mode)
      is JsonNode.StringNode -> compareString(path, a, b, mode)
      is JsonNode.LongNode -> compareLong(path, a, b, mode)
      is JsonNode.DoubleNode -> compareDouble(path, a, b, mode)
      JsonNode.NullNode -> compareNull(path, b)
   }
}

fun compareObjects(
   path: List<String>,
   expected: JsonNode.ObjectNode,
   actual: JsonNode.ObjectNode,
   mode: CompareMode,
): JsonError? {
   val keys1 = expected.elements.keys
   val keys2 = actual.elements.keys
   if (keys1.size < keys2.size) {
      val missing = keys2 - keys1
      return JsonError.ObjectMissingKeys(path, missing)
   }

   if (keys2.size < keys1.size) {
      val extra = keys1 - keys2
      return JsonError.ObjectExtraKeys(path, extra)
   }

   expected.elements.entries.zip(actual.elements.entries).forEach { (a, b) ->
      val error = compare(path + a.key, a.value, b.value, mode)
      if (error != null) return error
   }

   return null
}

fun compareArrays(
   path: List<String>,
   expected: JsonNode.ArrayNode,
   actual: JsonNode.ArrayNode,
   mode: CompareMode
): JsonError? {

   if (expected.elements.size != actual.elements.size)
      return JsonError.UnequalArrayLength(path, expected.elements.size, actual.elements.size)

   expected.elements.withIndex().zip(actual.elements.withIndex()).forEach { (a, b) ->
      val error = compare(path, a.value, b.value, mode)
      if (error != null) return JsonError.ArrayElementsDoNotMatch(path, a.index, a.value, b.value)
   }

   return null
}

/**
 * When comparing a string, if the [mode] is [CompareMode.Lenient] we can convert the actual node to a string.
 */
fun compareString(path: List<String>, expected: JsonNode.StringNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.StringNode -> compareStrings(path, expected.value, actual.value)
      mode == CompareMode.Lenient -> when (actual) {
         is JsonNode.BooleanNode -> compareStrings(path, expected.value, actual.value.toString())
         is JsonNode.DoubleNode -> compareStrings(path, expected.value, actual.value.toString())
         is JsonNode.LongNode -> compareStrings(path, expected.value, actual.value.toString())
         else -> JsonError.IncompatibleTypes(path, expected, actual)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

fun compareStrings(path: List<String>, expected: String, actual: String): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalStrings(path, expected, actual)
   }
}

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
fun compareBoolean(
   path: List<String>,
   expected: JsonNode.BooleanNode,
   actual: JsonNode,
   mode: CompareMode
): JsonError? {
   return when {
      actual is JsonNode.BooleanNode -> compareBooleans(path, expected.value, actual.value)
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (actual.value) {
         "true" -> compareBooleans(path, expected.value, true)
         "false" -> compareBooleans(path, expected.value, false)
         else -> JsonError.UnequalValues(path, expected, actual)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

fun compareBooleans(path: List<String>, expected: Boolean, actual: Boolean): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalBooleans(path, expected, actual)
   }
}

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
fun compareLong(path: List<String>, expected: JsonNode.LongNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.LongNode -> compareLongs(path, expected.value, actual.value)
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val l = actual.value.toLongOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareLongs(path, expected.value, l)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

fun compareLongs(path: List<String>, expected: Long, actual: Long): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
fun compareDouble(path: List<String>, expected: JsonNode.DoubleNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.DoubleNode -> compareDoubles(path, expected.value, actual.value)
      actual is JsonNode.LongNode -> compareDoubles(path, expected.value, actual.value.toDouble())
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val d = actual.value.toDoubleOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareDoubles(path, expected.value, d)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

fun compareDoubles(path: List<String>, expected: Double, actual: Double): JsonError? {
   return when {
      abs(expected - actual) <= Double.MIN_VALUE -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

fun compareNull(path: List<String>, b: JsonNode): JsonError? {
   return when (b) {
      is JsonNode.NullNode -> null
      else -> JsonError.ExpectedNull(path, b)
   }
}
