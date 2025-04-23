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
    * If the two values are nullable, then null will be returned.
    */
   // todo iterables will be added in here
   fun resolve(actual: Any?, expected: Any?): Eq<out Any?> {
      // if we have null and non null, usually that's a failure, but people can override equals to allow it
      return when {
         actual == null || expected == null -> NullEq
         actual is Map<*, *> && expected is Map<*, *> -> MapEq
         actual is Map.Entry<*, *> && expected is Map.Entry<*, *> -> MapEntryEq
         actual is Regex && expected is Regex -> RegexEq
         actual is String && expected is String -> StringEq
         actual is Number && expected is Number -> NumberEq
         actual is Sequence<*> || expected is Sequence<*> -> SequenceEq
         shouldShowDataClassDiff(actual, expected) -> DataClassEq
         actual is Throwable && expected is Throwable -> ThrowableEq
         IterableEq.isValidIterable(actual) && IterableEq.isValidIterable(expected) -> IterableEq
         else -> DefaultEq
      }
   }

   private fun <T> shouldShowDataClassDiff(actual: T, expected: T) =
      AssertionsConfig.showDataClassDiff && isDataClassInstance(actual) && isDataClassInstance(expected)
}
