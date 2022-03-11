package io.kotest.matchers.reflection

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import kotlin.reflect.KProperty1

fun <T : Any, V : Any> Matcher.Companion.compose(
   vararg pairs: Pair<Matcher<V>, KProperty1<T, V>>
): Matcher<T> = object : Matcher<T> {
   override fun test(value: T): MatcherResult {
      val all = pairs.toList()
      val (firstMatcher, firstProp) = all.first()
      val tail = all.drop(1)

      return tail.fold(firstMatcher.test(firstProp.get(value))) { _, (matcher, prop) ->
         matcher.test(prop.get(value))
      }
   }
}
