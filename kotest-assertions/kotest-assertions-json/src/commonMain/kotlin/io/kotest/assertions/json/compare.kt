@file:Suppress("unused")

package io.kotest.assertions.json

enum class CompareMode {
   Strict, Lenient
}

enum class CompareOrder {
   Strict, Lenient
}

/**
 * Compares two json trees, returning a detailed error message if they differ.
 */
internal fun compare(
   path: List<String>,
   expected: JsonNode,
   actual: JsonNode,
   mode: CompareMode,
   order: CompareOrder
): JsonError? {
   return when (expected) {
      is JsonNode.ObjectNode -> when (actual) {
         is JsonNode.ObjectNode -> compareObjects(path, expected, actual, mode, order)
         else -> JsonError.ExpectedObject(path, actual)
      }
      is JsonNode.ArrayNode -> when (actual) {
         is JsonNode.ArrayNode -> compareArrays(path, expected, actual, mode, order)
         else -> JsonError.ExpectedArray(path, actual)
      }
      is JsonNode.BooleanNode -> compareBoolean(path, expected, actual, mode)
      is JsonNode.StringNode -> compareString(path, expected, actual, mode)
      is JsonNode.NumberNode -> compareNumbers(path, expected, actual, mode)
      JsonNode.NullNode -> compareNull(path, actual)
   }
}

internal fun compareObjects(
   path: List<String>,
   expected: JsonNode.ObjectNode,
   actual: JsonNode.ObjectNode,
   mode: CompareMode,
   order: CompareOrder,
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

   // when using strict order mode, the order of elements in json matters, normally, we don't care
   when (order) {
      CompareOrder.Strict ->
         expected.elements.entries.withIndex().zip(actual.elements.entries).forEach { (e, a) ->
            if (a.key != e.value.key) return JsonError.NameOrderDiff(path, e.index, e.value.key, a.key)
            val error = compare(path + a.key, e.value.value, a.value, mode, order)
            if (error != null) return error
         }
      CompareOrder.Lenient ->
         expected.elements.entries.forEach { (name, e) ->
            val a = actual.elements[name] ?: return JsonError.ObjectMissingKeys(path, setOf(name))
            val error = compare(path + name, e, a, mode, order)
            if (error != null) return error
         }
   }

   return null
}

internal fun compareArrays(
   path: List<String>,
   expected: JsonNode.ArrayNode,
   actual: JsonNode.ArrayNode,
   mode: CompareMode,
   order: CompareOrder,
): JsonError? {

   if (expected.elements.size != actual.elements.size)
      return JsonError.UnequalArrayLength(path, expected.elements.size, actual.elements.size)

   expected.elements.withIndex().zip(actual.elements.withIndex()).forEach { (a, b) ->
      val error = compare(path + "[${a.index}]", a.value, b.value, mode, order)
      if (error != null) return error
   }

   return null
}

/**
 * When comparing a string, if the [mode] is [CompareMode.Lenient] we can convert the actual node to a string.
 */
internal fun compareString(path: List<String>, expected: JsonNode.StringNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.StringNode -> compareStrings(path, expected.value, actual.value)
      mode == CompareMode.Lenient -> when {
         actual is JsonNode.BooleanNode -> compareStrings(path, expected.value, actual.value.toString())
         actual is JsonNode.NumberNode && expected.containsNumber() -> compareNumbers(path, expected.toNumberNode(), actual, mode)
         else -> JsonError.IncompatibleTypes(path, expected, actual)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareStrings(path: List<String>, expected: String, actual: String): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalStrings(path, expected, actual)
   }
}

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
internal fun compareBoolean(
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

internal fun compareBooleans(path: List<String>, expected: Boolean, actual: Boolean): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalBooleans(path, expected, actual)
   }
}

internal fun compareNumbers(path: List<String>, expected: JsonNode.NumberNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when(mode) {
      CompareMode.Strict -> {
         if (actual is JsonNode.NumberNode) compareNumbersStrictly(path, expected.content, actual.content)
         else JsonError.IncompatibleTypes(path, expected,actual)
      }

      CompareMode.Lenient -> {
         when (actual) {
            is JsonNode.NumberNode -> compareNumbersLeniently(path, expected, actual)
            is JsonNode.StringNode -> {
               if (actual.containsNumber()) compareNumbersLeniently(path, expected, JsonNode.NumberNode(actual.content))
               else JsonError.IncompatibleTypes(path, expected, actual)
            }
            else -> JsonError.IncompatibleTypes(path, expected, actual)
         }
      }
   }
}

internal fun compareNumbersStrictly(path: List<String>, expected: String, actual: String): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

internal fun compareNumbersLeniently(path: List<String>, expected: JsonNode.NumberNode, actual: JsonNode.NumberNode): JsonError? {
   fun leastSpecific(value: String): Number =
      with(value.trimEnd('0', '.')) {
         if (contains(".")) toDouble()
         else toInt()
      }

   return when {
      leastSpecific(expected.content) == leastSpecific(actual.content) -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

internal fun compareNull(path: List<String>, b: JsonNode): JsonError? {
   return when (b) {
      is JsonNode.NullNode -> null
      else -> JsonError.ExpectedNull(path, b)
   }
}
