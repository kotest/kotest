package io.kotest.matchers.reflection

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.reflect.KProperty1

@Deprecated("Replaced with io.kotest.matchers.compose.Matcher.all Deprecated since 5.7")
@Suppress("UNCHECKED_CAST")
fun <T : Any?> Matcher.Companion.compose(
   vararg pairs: Pair<Matcher<*>, KProperty1<T, *>>
): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val results = pairs.map { (matcher, prop) ->
         (matcher as Matcher<Any?>).test(prop.get(value))
      }
      return MatcherResult(
         results.all { it.passed() },
         {
            results.map { it.failureMessage() }.fold("") { acc: String, s: String -> acc + s + "\n" }
               .trimIndent()
         },
         {
            results.map { it.negatedFailureMessage() }.fold("") { acc: String, s: String -> acc + s + "\n" }
               .trimIndent()
         },
      )
   }
}
