package io.kotest.assertions.yaml

class CompareYamlOptions(

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
    *
    * Note: YAML-specific numeric forms that are not valid JSON numbers,
    * such as `0x1F`, `0o17`, `1_000` or `.5`, are always compared literally.
    */
   var numberFormat: NumberFormat = NumberFormat.Lenient,
)

enum class PropertyOrder {
   /**
    * Default. Property order in mappings does not matter.
    *
    * Example: The following will pass
    * ```kotlin
    * """
    *    a: 1
    *    b: 2
    * """.shouldEqualYaml(
    *    """
    *    b: 2
    *    a: 1
    * """,
    *    yamlCompareOptions { propertyOrder = Lenient }
    * )
    * ```
    */
   Lenient,

   /**
    * Properties must be in the same order.
    *
    * E.g.
    * ```yaml
    * a: 1
    * b: 2
    * ```
    * is not considered equal to
    * ```yaml
    * b: 2
    * a: 1
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
    * Default. Mappings in `expected` and `actual` must contain the same fields.
    */
   Strict,

   /**
    * Mappings in the actual document may contain extraneous fields without causing comparison to fail.
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

fun yamlCompareOptions(builder: CompareYamlOptions.() -> Unit): CompareYamlOptions =
   CompareYamlOptions().apply(builder)
