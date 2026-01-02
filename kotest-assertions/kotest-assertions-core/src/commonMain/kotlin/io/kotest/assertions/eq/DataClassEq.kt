package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.print
import io.kotest.common.reflection.Property
import io.kotest.common.reflection.bestName
import io.kotest.common.reflection.reflection

internal fun isDataClassInstance(obj: Any?): Boolean =
   obj != null && reflection.isDataClass(obj::class)

/**
 * Prints a detailed diff of data class instances, highlighting which properties/fields of the data class instances
 * differ.
 *
 * The Kotlin compiler derives the equals() implementation based on all properties declared in the primary constructor.
 * As such this feature becomes less useful if you want to override equals() in your data classes. If this is the case
 * you may wish to turn off this feature by setting the system property: "kotest.assertions.show-data-class-diffs" to
 * "false".
 *
 * E.g.:
 * ```
 *     -Dkotest.assertions.show-data-class-diffs=false
 * ```
 */
internal object DataClassEq : Eq<Any> {

   /**
    * Used to determine at what level of nesting we abort processing the diff.
    * To prevent stack overflows/cyclic dependencies.
    * Note: With cycle detection via EqContext, this provides defense in depth.
    */
   private const val MAX_NESTED_DEPTH = 10

   override fun equals(actual: Any, expected: Any,  context: EqContext): Throwable? {
      if (actual === expected) return null

      if (context.isVisited(actual, expected)) return null

      context.push(actual, expected)
      try {
         return if (test(actual, expected)) {
            null
         } else {
            val detailedDiffMsg = runCatching {
               dataClassDiff(actual, expected, context = context)?.let { diff -> formatDifferences(diff) + "\n\n" } ?: ""
            }.getOrElse { "" }

            AssertionErrorBuilder.create()
               .withMessage(detailedDiffMsg)
               .withValues(Expected(expected.print()), Actual(actual.print()))
               .build()
         }
      } finally {
         context.pop()
      }
   }

   private fun test(a: Any?, b: Any?): Boolean = makeComparable(a) == makeComparable(b)

   private fun dataClassDiff(actual: Any?, expected: Any?, depth: Int = 0, context: EqContext): DataClassDifference? {
      require(actual != null && expected != null) { "Actual and expected values cannot be null in a data class comparison" }
      require(depth < MAX_NESTED_DEPTH) { "Max depth reached" }
      val differences = computeMemberDifferences(expected, actual, depth, context)
      return when {
         differences.isEmpty() -> null
         else -> DataClassDifference(expected::class.bestName(), differences)
      }
   }

   private fun computeMemberDifferences(expected: Any, actual: Any, depth: Int, context: EqContext) =
      reflection.primaryConstructorMembers(expected::class).mapNotNull { prop ->
         val actualPropertyValue = prop.call(actual)
         val expectedPropertyValue = prop.call(expected)
         if (isDataClassInstance(actualPropertyValue) && isDataClassInstance(expectedPropertyValue))
            dataClassDiff(actualPropertyValue, expectedPropertyValue, depth + 1, context)?.let { diff ->
               Pair(prop, diff)
            }
         else {
            EqCompare.compare(actualPropertyValue, expectedPropertyValue, context)
               ?.let { Pair(prop, StandardDifference(it)) }
         }
      }

   private fun formatDifferences(dataClassDiff: DataClassDifference, indentStyle: List<Boolean> = emptyList()): String {
      val noOfDifferences = dataClassDiff.differences.size
      return dataClassDiff.differences.mapIndexed { index, (property, difference) ->
         val isLastProperty = index + 1 == noOfDifferences
         val diffMsg = when (difference) {
            is StandardDifference -> difference.differenceError.message
            is DataClassDifference -> formatDifferences(difference, indentStyle + isLastProperty)
         }
         buildString {
            append(indentStyle.joinToString(separator = "") { if (it) "   " else "│  " })
            append(if (isLastProperty) '└' else '├')
            append(" ${property.name}: $diffMsg")
         }
      }.joinToString(separator = "\n", prefix = "data class diff for ${dataClassDiff.dataClassName}\n")
   }
}

private sealed class PropertyDifference
private data class StandardDifference(val differenceError: Throwable) : PropertyDifference()
private data class DataClassDifference(
   val dataClassName: String,
   val differences: List<Pair<Property, PropertyDifference>>
) : PropertyDifference()
