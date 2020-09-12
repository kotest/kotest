package io.kotest.matchers.tuples

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <A> Triple<A, *, *>.shouldHaveFirst(a: A) = this should haveTripleFirst(a)
fun <A> Triple<A, *, *>.shouldNotHaveFirst(a: A) = this shouldNot haveTripleFirst(a)
fun <A> haveTripleFirst(a: A) = object : Matcher<Triple<A, *, *>> {
   override fun test(value: Triple<A, *, *>): MatcherResult {
      return MatcherResult(
         value.first == a,
         "Triple $value should have first value $a but was ${value.first}",
         "Triple $value should not have first value $a"
      )
   }
}

fun <B> Triple<*, B, *>.shouldHaveSecond(b: B) = this should haveTripleSecond(b)
fun <B> Triple<*, B, *>.shouldNotHaveSecond(b: B) = this shouldNot haveTripleSecond(b)
fun <B> haveTripleSecond(b: B) = object : Matcher<Triple<*, B, *>> {
   override fun test(value: Triple<*, B, *>): MatcherResult {
      return MatcherResult(
         value.second == b,
         "Triple $value should have second value $b but was ${value.second}",
         "Triple $value should not have second value $b"
      )
   }
}


fun <C> Triple<*, *, C>.shouldHaveThird(c: C) = this should haveTripleThird(c)
fun <C> Triple<*, *, C>.shouldNotHaveThird(c: C) = this shouldNot haveTripleThird(c)
fun <C> haveTripleThird(c: C) = object : Matcher<Triple<*, *, C>> {
   override fun test(value: Triple<*, *, C>): MatcherResult {
      return MatcherResult(
         value.third == c,
         "Triple $value should have third value $c but was ${value.third}",
         "Triple $value should not have third value $c"
      )
   }
}
