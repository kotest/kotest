package io.kotest.matchers

/**
 * A [MatcherResult] which is the product of one or more underlying matcher results.
 * If any of the other matchers have failed, then this result has
 */
class MultipleMatcherResult(private val results: List<MatcherResult>) : MatcherResult {

   override fun passed(): Boolean {
      return results.all { it.passed() }
   }

   override fun failureMessage(): String {
      return buildString {
         appendLine("Matcher failed due to:")
         results.forEachIndexed { index, value ->
            appendLine("$index) ${value.failureMessage()}")
         }
      }
   }

   override fun negatedFailureMessage(): String {
      return buildString {
         appendLine("Matcher failed due to:")
         results.forEachIndexed { index, value ->
            appendLine("$index) ${value.negatedFailureMessage()}")
         }
      }
   }
}
