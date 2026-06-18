package io.kotest.matchers.reflection

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.reflect.KProperty1

/**
 * Compose matchers on class of type [T] along with the property to extract to test against.
 * KProperty1 and Matcher must be the same type.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any?> havingProperty(
   pair: Pair<Matcher<*>, KProperty1<T, *>>
): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val (matcher: Matcher<*>, prop: KProperty1<T, *>) = pair
      val result = runCatching {
         (matcher as Matcher<Any?>).test(prop(value))
      }.onFailure {
         AssertionErrorBuilder.fail("Mismatching type of matcher for property ${prop.name}: ${it.message}")
      }.getOrThrow()

      return MatcherResult(
         result.passed(),
         { result.failureMessage() },
         { result.negatedFailureMessage() }
      )
   }
}

