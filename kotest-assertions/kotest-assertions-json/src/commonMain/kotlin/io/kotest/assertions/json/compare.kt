@file:Suppress("unused")

package io.kotest.assertions.json

import kotlin.math.abs

enum class CompareMode {
   Strict, Lenient
}

enum class CompareOrder {
   /**
    * All object properties and array items must be in same order as expected.
    *
    * For example, { "x": 14.2, "y": 13.0 }` and `{ "y": 13.0, "x: 14.2 }` will NOT be considered equal.
    */
   Strict,

   /**
    * See [IgnoreProperties]
    */
   @Deprecated(
      replaceWith = ReplaceWith("CompareOrder.IgnoreProperties"),
      message = "Will be renamed to `IgnoreProperties` in 5.0"
   )
   Lenient,

   /**
    * Ignore the order of object properties and arrays.
    *
    * For example, { "x": 14.2, "y": 13.0 }` and `{ "y": 13.0, "x: 14.2 }` would also be considered equal,
    * since they have the same properties and values.
    * However, `[1, 2]` and `[2, 1]` would NOT be considered equal
    */
   IgnoreProperties,

   /**
    * Ignore the order of object properties and arrays.
    *
    * For example, `[1, 2]` and `[2, 1]` will be considered equal, since they contain the same items.
    * `{ "x": 14.2, "y": 13.0 }` and `{ "y": 13.0, "x: 14.2 }` would also be considered equal, since they have the
    * same properties and values.
    */
   IgnoreAll,
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
      is JsonNode.LongNode -> compareLong(path, expected, actual, mode)
      is JsonNode.DoubleNode -> compareDouble(path, expected, actual, mode)
      is JsonNode.FloatNode -> compareFloat(path, expected, actual, mode)
      is JsonNode.IntNode -> compareInt(path, expected, actual, mode)
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

   when (order) {
      CompareOrder.Strict -> {
         expected.elements.withIndex().zip(actual.elements.withIndex()).forEach { (a, b) ->
            val error = compare(path + "[${a.index}]", a.value, b.value, mode, order)
            if (error != null) return error
         }
      }
      /**
       * Perhaps this can be optimized somehow?
       * If content was [Comparable], perhaps both could be sorted and compared normally afterwards.
       * This solution simply tries to find a match for each element in [actual], in the [expected] array,
       * flagging used matches so they can't be used twice.
       */
      CompareOrder.Lenient -> {

         val consumedIndexes = BooleanArray(expected.elements.size) { false }

         fun availableIndexes() = consumedIndexes
            .mapIndexed { index, isConsumed -> if (!isConsumed) index else null }
            .filterNotNull()

         fun findMatchingIndex(element: JsonNode): Int? {
            for (i in availableIndexes()) {
               val error = compare(path + "[$i]", element, expected.elements[i], mode, order)

               if (error == null) {
                  // Comparison success, use index
                  return i
               }
            }

            return null
         }

         for ((i, element) in actual.elements.withIndex()) {
            val match = findMatchingIndex(element)
               ?: return JsonError.UnequalArrayContent(path + "[$i]", expected, element)

            consumedIndexes[match] = true
         }
      }
   }

   return null
}

/**
 * When comparing a string, if the [mode] is [CompareMode.Lenient] we can convert the actual node to a string.
 */
internal fun compareString(path: List<String>, expected: JsonNode.StringNode, actual: JsonNode, mode: CompareMode): JsonError? {
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

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
internal fun compareLong(path: List<String>, expected: JsonNode.LongNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.LongNode -> compareLongs(path, expected.value, actual.value)
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val l = actual.value.toLongOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareLongs(path, expected.value, l)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareLongs(path: List<String>, expected: Long, actual: Long): JsonError? {
   return when (expected) {
      actual -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

/**
 * When comparing a boolean, if the [mode] is [CompareMode.Lenient] and the actual node is a text
 * node with "true" or "false", then we convert.
 */
internal fun compareDouble(path: List<String>, expected: JsonNode.DoubleNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.DoubleNode -> compareDoubles(path, expected.value, actual.value)
      actual is JsonNode.LongNode -> compareDoubles(path, expected.value, actual.value.toDouble())
      actual is JsonNode.FloatNode -> compareDoubles(path, expected.value, actual.value.toDouble())
      actual is JsonNode.IntNode -> compareDoubles(path, expected.value, actual.value.toDouble())
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val d = actual.value.toDoubleOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareDoubles(path, expected.value, d)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareDoubles(path: List<String>, expected: Double, actual: Double): JsonError? {
   return when {
      abs(expected - actual) <= Double.MIN_VALUE -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

internal fun compareFloat(path: List<String>, expected: JsonNode.FloatNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.FloatNode -> compareFloats(path, expected.value, actual.value)
      actual is JsonNode.LongNode -> compareFloats(path, expected.value, actual.value.toFloat())
      actual is JsonNode.DoubleNode -> compareFloats(path, expected.value, actual.value.toFloat())
      actual is JsonNode.IntNode -> compareFloats(path, expected.value, actual.value.toFloat())
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val d = actual.value.toFloatOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareFloats(path, expected.value, d)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareFloats(path: List<String>, expected: Float, actual: Float): JsonError? {
   return when {
      abs(expected - actual) <= Float.MIN_VALUE -> null
      else -> JsonError.UnequalValues(path, expected, actual)
   }
}

internal fun compareInt(path: List<String>, expected: JsonNode.IntNode, actual: JsonNode, mode: CompareMode): JsonError? {
   return when {
      actual is JsonNode.IntNode -> compareInts(path, expected.value, actual.value)
      actual is JsonNode.FloatNode -> compareInts(path, expected.value, actual.value.toInt())
      actual is JsonNode.LongNode -> compareInts(path, expected.value, actual.value.toInt())
      actual is JsonNode.DoubleNode -> compareInts(path, expected.value, actual.value.toInt())
      mode == CompareMode.Lenient && actual is JsonNode.StringNode -> when (val d = actual.value.toIntOrNull()) {
         null -> JsonError.IncompatibleTypes(path, expected, actual)
         else -> compareInts(path, expected.value, d)
      }
      else -> JsonError.IncompatibleTypes(path, expected, actual)
   }
}

internal fun compareInts(path: List<String>, expected: Int, actual: Int): JsonError? {
   return when (expected) {
       actual -> null
       else -> JsonError.UnequalValues(path, expected, actual)
   }
}

internal fun compareNull(path: List<String>, b: JsonNode): JsonError? {
   return when (b) {
      is JsonNode.NullNode -> null
      else -> JsonError.ExpectedNull(path, b)
   }
}
