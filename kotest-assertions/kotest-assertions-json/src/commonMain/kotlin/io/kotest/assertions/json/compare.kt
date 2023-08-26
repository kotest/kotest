@file:Suppress("unused")

package io.kotest.assertions.json

@Deprecated("Json comparison options is now specified with `CompareJsonOptions`", ReplaceWith("TypeCoercion"))
enum class CompareMode {
   /**
    * Types must be identical and compare by value.
    *
    * For example, `"true"` and `true` will not match because one is a string, and the other is a boolean.
    * But `2.99E9` and `299000000` are considered equal as they are the same number, just in a different format.
    * `"100"` and `100` would not match as they are different types (string vs number).
    */
   @Deprecated(
      "Json comparison options is now specified with `CompareJsonOptions`",
      ReplaceWith("TypeCoercion.Disabled")
   )
   Strict,

   /**
    * Compare by value, coercing if possible.
    *
    * For example, "true" and true will match because the string value can be coerced into a valid boolean.
    * Similarly, "100" and 100 will match as the former can be coerced into an int.
    */
   @Deprecated(
      "Json comparison options is now specified with `CompareJsonOptions`",
      ReplaceWith("TypeCoercion.Enabled")
   )
   Lenient,
}

@Deprecated("Json comparison options is now specified with `CompareJsonOptions`", ReplaceWith("PropertyOrder"))
enum class CompareOrder {
   /**
    * All object properties must be in same order as expected.
    *
    * For example, { "x": 14.2, "y": 13.0 }` and `{ "y": 13.0, "x: 14.2 }` will NOT be considered equal.
    */
   @Deprecated(
      "Json comparison options is now specified with `CompareJsonOptions`",
      ReplaceWith("PropertyOrder.Strict")
   )
   Strict,

   @Deprecated(
      "Json comparison options is now specified with `CompareJsonOptions`",
      ReplaceWith("PropertyOrder.Lenient")
   )
   Lenient,
}

/**
 * helper method for bridging old compare options into new
 */
internal fun legacyOptions(mode: CompareMode, order: CompareOrder) =
   compareJsonOptions {
      typeCoercion = when (mode) {
         CompareMode.Strict -> TypeCoercion.Disabled
         CompareMode.Lenient -> TypeCoercion.Enabled
      }

      propertyOrder = when (order) {
         CompareOrder.Strict -> PropertyOrder.Strict
         CompareOrder.Lenient -> PropertyOrder.Lenient
      }
   }

internal val defaultCompareJsonOptions = CompareJsonOptions()

class CompareJsonOptions(

   /**
    * Controls whether property order must be identical
    */
   var propertyOrder: PropertyOrder = PropertyOrder.Lenient,

   /**
    * Controls whether array ordering must be identical.
    */
   var arrayOrder: ArrayOrder = ArrayOrder.Strict,

   /**
    * Controls whether the actual document may contain extra fields or not.
    */
   var fieldComparison: FieldComparison = FieldComparison.Strict,

   /**
    * Controls whether number formatting should be taken into consideration. For instance, comparing 1.00 to 1.0, or
    * 1E2 to 100
    */
   var numberFormat: NumberFormat = NumberFormat.Lenient,

   /**
    *  Controls whether types should be coerced when possible. For instance, when strings contain bool or numeric values.
    */
   var typeCoercion: TypeCoercion = TypeCoercion.Disabled
)

enum class PropertyOrder {
   /**
    * Default. Property order in objects does not matter.
    *
    * Example: `"""{ "a": 0, "b": 2 }""".shouldEqualJson("""{ "b": 2, "a": 1 }""", compareJsonOptions { propertyOrder = Lenient })` will pass
    */
   Lenient,

   /**
    * Properties must be in same order. E.g. `{ "a": 0, "b": 2 }` is not considered equal to `{ "b": 2, "a": 1 }`
    */
   Strict
}

enum class ArrayOrder {
   /**
    * Default. Arrays must contain the same elements in the same order.
    */
   Strict,

   /**
    * Arrays are allowed to be shuffled, but must still contain same items.
    */
   Lenient,
}

enum class FieldComparison {

   /**
    * Default. Objects in [expected] and [actual] must contain the same fields.
    */
   Strict,

   /**
    * Objects in the actual document may contain extraneous fields without causing comparison to fail.
    */
   Lenient,
}

enum class NumberFormat {
   /**
    * Default. Numbers will be interpreted before being compared. Meaning we can compare 0E3 to 1000 without fail
    */
   Lenient,

   /**
    * Numbers must also be formatted the same way to be considered equal.
    */
   Strict
}

enum class TypeCoercion {
   /**
    * Default. Types will not be converted. Meaning `"true"` and `true` are considered unequal.
    */
   Disabled,

   /**
    * Types may be coerced. Strings containing numbers will be considered equal to their numbers, and booleans in
    * strings will also be compared.
    *
    * For example: `"\"11\"".shouldEqualJson("12", compareJsonOptions { typeCoercion = TypeCoercion.Enabled })` will
    * succeed.
    */
   Enabled;

   internal fun isEnabled(): Boolean =
      this == Enabled
}

fun compareJsonOptions(builder: CompareJsonOptions.() -> Unit): CompareJsonOptions =
   CompareJsonOptions().apply(builder)

/**
 * Compares two json trees, returning a detailed error message if they differ.
 */
internal fun compare(
   path: List<String>,
   expected: JsonNode,
   actual: JsonNode,
   options: CompareJsonOptions
): JsonError? {
   return when (expected) {
      is JsonNode.ObjectNode -> when (actual) {
         is JsonNode.ObjectNode -> compareObjects(path, expected, actual, options)
         else -> JsonError.ExpectedObject(path, actual)
      }
      is JsonNode.ArrayNode -> when (actual) {
         is JsonNode.ArrayNode -> compareArrays(path, expected, actual, options)
         else -> JsonError.ExpectedArray(path, actual)
      }
      is JsonNode.BooleanNode -> compareBoolean(path, expected, actual, options)
      is JsonNode.StringNode -> compareString(path, expected, actual, options)
      is JsonNode.NumberNode -> compareNumbers(path, expected, actual, options)
      JsonNode.NullNode -> compareNull(path, actual)
   }
}

internal fun compareObjects(
   path: List<String>,
   expected: JsonNode.ObjectNode,
   actual: JsonNode.ObjectNode,
   options: CompareJsonOptions,
): JsonError? {

   if (FieldComparison.Strict == options.fieldComparison) {
      val expectedKeys = expected.elements.keys
      val actualKeys = actual.elements.keys

      if (actualKeys.size > expectedKeys.size) {
         val extra = actualKeys - expectedKeys
         return JsonError.ObjectExtraKeys(path, extra)
      }

      if (actualKeys.size < expectedKeys.size) {
         val missing = expectedKeys - actualKeys
         return JsonError.ObjectMissingKeys(path, missing)
      }
   }

   // when using strict order mode, the order of elements in json matters, normally, we don't care
   when (options.propertyOrder) {
      PropertyOrder.Strict ->
         expected.elements.entries.withIndex().zip(actual.elements.entries).forEach { (e, a) ->
            if (a.key != e.value.key) return JsonError.NameOrderDiff(path, e.index, e.value.key, a.key)
            val error = compare(path + a.key, e.value.value, a.value, options)
            if (error != null) return error
         }
      PropertyOrder.Lenient ->
         expected.elements.entries.forEach { (name, e) ->
            val a = actual.elements[name] ?: return JsonError.ObjectMissingKeys(path, setOf(name))
            val error = compare(path + name, e, a, options)
            if (error != null) return error
         }
   }

   return null
}

internal fun compareArrays(
   path: List<String>,
   expected: JsonNode.ArrayNode,
   actual: JsonNode.ArrayNode,
   options: CompareJsonOptions,
): JsonError? {

   if (expected.elements.size != actual.elements.size)
      return JsonError.UnequalArrayLength(path, expected.elements.size, actual.elements.size)

   when (options.arrayOrder) {
      ArrayOrder.Strict -> {
         expected.elements.withIndex().zip(actual.elements.withIndex()).forEach { (a, b) ->
            val error = compare(path + "[${a.index}]", a.value, b.value, options)
            if (error != null) return error
         }
      }

      /**
       * In [ArrayOrder.Lenient], we try to allow array contents to be out-of-order.
       * We do this by searching for a match for each element in [actual], in the [expected] array,
       * flagging used matches so they can't be used twice. This will probably be slow for very big arrays.
       */
      ArrayOrder.Lenient -> {

         val consumedIndexes = BooleanArray(expected.elements.size) { false }

         fun availableIndexes() = consumedIndexes
            .mapIndexed { index, isConsumed -> if (!isConsumed) index else null }
            .filterNotNull()

         fun findMatchingIndex(element: JsonNode): Int? {
            for (i in availableIndexes()) {
               // Comparison with no error -> matching element
               val isMatch = compare(path + "[$i]", expected.elements[i], element, options) == null

               if (isMatch) {
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
   options: CompareJsonOptions
): JsonError? {
   return when {
      actual is JsonNode.BooleanNode -> compareBooleans(path, expected.value, actual.value)
      options.typeCoercion.isEnabled() && actual is JsonNode.StringNode -> when (actual.value) {
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

private fun compareNumbers(
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


private fun compareNumberNodes(
   path: List<String>,
   expected: JsonNode.NumberNode,
   actual: JsonNode.NumberNode
): JsonError? {

   return when {
      expected.lenientEquals(actual) -> null
      else -> JsonError.UnequalValues(path, expected.content, actual.content)
   }
}

internal fun compareNull(path: List<String>, b: JsonNode): JsonError? {
   return when (b) {
      is JsonNode.NullNode -> null
      else -> JsonError.ExpectedNull(path, b)
   }
}
