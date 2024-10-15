package io.kotest.matchers.equality

import java.lang.AssertionError

sealed interface CustomComparisonResult {
   val comparable: Boolean
   data object NotComparable: CustomComparisonResult {
      override val comparable = false
   }
   data object Equal: CustomComparisonResult {
      override val comparable = true
   }
   data class Different(val assertionError: AssertionError): CustomComparisonResult {
      override val comparable = true
   }
}

fun interface Assertable {
   fun assert(expected: Any?, actual: Any?): CustomComparisonResult
}

inline fun<reified T: Any> customComparison(
   expected: Any?,
   actual: Any?,
   assertion: (expected: T, actual: T) -> Unit
): CustomComparisonResult = when {
   expected == null -> CustomComparisonResult.NotComparable
   actual == null -> CustomComparisonResult.NotComparable
   expected is T && actual is T -> {
      try {
         assertion(expected, actual)
         CustomComparisonResult.Equal
      } catch (e: AssertionError) {
         CustomComparisonResult.Different(e)
      }
   }
   else -> CustomComparisonResult.NotComparable
}

