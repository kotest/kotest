package io.kotest.matchers.compose

import io.kotest.assertions.fail
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.reflect.KProperty1

/**
 * Compose matchers along with the property to extract to test against.
 * All matchers must be the same type `T`. If any matcher fails, composed matcher fails, too.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> Matcher.Companion.compose(
   vararg pairs: Pair<Matcher<*>, KProperty1<T, *>>
): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val results = pairs.mapIndexed {i , (matcher, prop) ->
         runCatching {
            (matcher as Matcher<Any?>).test(prop(value))
         }.onFailure {
            fail("Mismatching types in argument ${i + 1} for composed matcher:\n\n${it.message}")
         }.getOrThrow()
      }

      return MatcherResult(
         results.all { it.passed() },
         { results.joinToString(separator = "\n") { it.failureMessage() } },
         { results.joinToString(separator = "\n") { it.negatedFailureMessage() } },
      )
   }
}

/**
 * Compose matchers. All matchers must be the same type `T`. If any matcher fails, composed matcher fails, too.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> Matcher.Companion.compose(
   vararg matchers: Matcher<T>
): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val results = matchers.mapIndexed { i, matcher  ->
         runCatching {
            (matcher as Matcher<Any?>).test(value)
         }.onFailure {
            fail("Mismatching types in argument ${i + 1} for composed matcher:\n\n${it.message}")
         }.getOrThrow()
      }

      return MatcherResult(
         results.all { it.passed() },
         { results.joinToString(separator = "\n") { it.failureMessage() } },
         { results.joinToString(separator = "\n") { it.negatedFailureMessage() } },
      )
   }
}

