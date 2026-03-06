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
      // when there's only one, just output it directly
      if (results.count { !it.passed() } == 1) return results.first { !it.passed() }.failureMessage()
      return buildString {
         appendLine("Matcher failed due to:")
         results.filterNot { it.passed() }.forEachIndexed { index, value ->
            appendLine("$index) ${value.failureMessage()}")
         }
      }.trim()
   }

   override fun negatedFailureMessage(): String {
      // when there's only one, just output it directly
      if (results.count { !it.passed() } == 1) return results.first { !it.passed() }.negatedFailureMessage()
      return buildString {
         appendLine("Matcher failed due to:")
         results.filter { it.passed() }.forEachIndexed { index, value ->
            appendLine("$index) ${value.negatedFailureMessage()}")
         }
      }.trim()
   }
}
