package io.kotest.matchers.collections

import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun <T> Iterable<T>.shouldNotContain(t: T): Iterable<T> {
   toList().shouldNotContain(t)
   return this
}

infix fun <T> Array<T>.shouldNotContain(t: T): Array<T> {
   asList().shouldNotContain(t)
   return this
}

infix fun <T, C : Collection<T>> C.shouldNotContain(t: T): C {
   this shouldNot contain(t)
   return this
}

infix fun <T> Iterable<T>.shouldContain(t: T): Iterable<T> {
   toList().shouldContain(t)
   return this
}

infix fun <T> Array<T>.shouldContain(t: T): Array<T> {
   asList().shouldContain(t)
   return this
}

infix fun <T, C : Collection<T>> C.shouldContain(t: T): C {
   this should contain(t)
   return this
}

fun <T, C : Collection<T>> contain(t: T) = object : Matcher<C> {
   override fun test(value: C) = MatcherResult(
      value.contains(t),
      { "Collection should contain element ${t.print().value}; listing some elements ${value.take(5)}" },
      { "Collection should not contain element ${t.print().value}" }
   )
}
