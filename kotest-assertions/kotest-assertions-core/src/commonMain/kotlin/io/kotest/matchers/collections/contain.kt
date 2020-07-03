package io.kotest.matchers.collections

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T> Iterable<T>.shouldNotContain(t: T) = toList().shouldNotContain(t)
infix fun <T> Array<T>.shouldNotContain(t: T) = asList().shouldNotContain(t)
infix fun <T, C : Collection<T>> C.shouldNotContain(t: T) = this shouldNot contain(t)

infix fun <T> Iterable<T>.shouldContain(t: T) = toList().shouldContain(t)
infix fun <T> Array<T>.shouldContain(t: T) = asList().shouldContain(t)
infix fun <T, C : Collection<T>> C.shouldContain(t: T) = this should contain(t)

fun <T, C : Collection<T>> contain(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.contains(t),
      { "Collection should contain element ${t.show().value}; listing some elements ${value.take(5)}" },
      { "Collection should not contain element ${t.show().value}" }
   )
}
