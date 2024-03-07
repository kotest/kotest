package io.kotest.matchers.compose

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

/**
 * Compose matchers. All matchers must be the same type `T`. If any matcher fails, composed matcher fails, too.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> Matcher.Companion.all(
   vararg matchers: Matcher<T>
): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val results: List<MatcherResult> = matchers.map { matcher -> matcher.test(value) }
      return MatcherResult(
         results.all { it.passed() },
         { results.filterNot { it.passed() }.joinToString(separator = "\n") { it.failureMessage() } },
         { results.filter { it.passed() }.joinToString(separator = "\n") { it.negatedFailureMessage() } },
      )
   }
}
