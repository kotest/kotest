package io.kotest.assertions.json

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
    * Controls whether number formatting should be taken into consideration.
    *
    * For instance, comparing `1.00` to `1.0`, or `1E2` to `100`.
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
    * Example: The following will pass
    * ```kotlin
    * """{ "a": 1, "b": 2 }"""
    *    .shouldEqualJson(
    *       """{ "b": 2, "a": 1 }""",
    *       compareJsonOptions { propertyOrder = Lenient }
    *    )
    * ```
    */
   Lenient,

   /**
    * Properties must be in same order.
    *
    * E.g.
    *
    * ```json
    * { "a": 1, "b": 2 }
    * ```
    * is not considered equal to
    * ```json
    * { "b": 2, "a": 1 }
    * ```
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
    * Default. Objects in `expected` and `actual` must contain the same fields.
    */
   Strict,

   /**
    * Objects in the actual document may contain extraneous fields without causing comparison to fail.
    */
   Lenient,
}

enum class NumberFormat {
   /**
    * Default. Numbers will be interpreted before being compared. Meaning we can compare `0E3` to `1000` without fail.
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
    * For example, the following will succeed:
    *
    * ```kotlin
    * "\"12\"".shouldEqualJson("12", compareJsonOptions { typeCoercion = TypeCoercion.Enabled })
    * ```
    */
   Enabled;

   internal fun isEnabled(): Boolean =
      this == Enabled
}

fun compareJsonOptions(builder: CompareJsonOptions.() -> Unit): CompareJsonOptions =
   CompareJsonOptions().apply(builder)


