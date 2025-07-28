package io.kotest.assertions.eq

import io.kotest.assertions.AssertionsConfig

/**
 * An [EqResolver] is used to resolve the appropriate [Eq] instance for two values being compared.
 *
 * See [EqCompare].
 */
@Suppress("DEPRECATION")
object EqResolver {

   /**
    * Returns the [Eq] to use for comparison for the given values.
    * If both values are nullable, then [NullEq] will be returned.
    */
   fun resolve(actual: Any?, expected: Any?): Eq<out Any?> {
      // if we have null and non null, usually that's a failure, but people can override equals to allow it
      return when {
         actual == null || expected == null -> NullEq
         actual is Map<*, *> && expected is Map<*, *> -> MapEq
         actual is Map.Entry<*, *> && expected is Map.Entry<*, *> -> MapEntryEq
         actual is Regex && expected is Regex -> RegexEq
         actual is String && expected is String -> StringEq
         actual is Number && expected is Number -> NumberEq
         actual is Collection<*> && expected is Collection<*> -> CollectionEq
         actual is Array<*> && expected is Array<*> -> ArrayEq
         actual is Sequence<*> || expected is Sequence<*> -> SequenceEq
         actual is Throwable && expected is Throwable -> ThrowableEq
         shouldShowDataClassDiff(actual, expected) -> DataClassEq
         else -> DefaultEq
      }
   }

   private fun <T> shouldShowDataClassDiff(actual: T, expected: T) =
      AssertionsConfig.showDataClassDiff && isDataClassInstance(actual) && isDataClassInstance(expected)
}
