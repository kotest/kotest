package io.kotest.matchers.tuples

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <A> Pair<A, *>.shouldHaveFirst(a: A): Pair<A, *> {
   this should haveFirst(a)
   return this
}

fun <A> Pair<A, *>.shouldNotHaveFirst(a: A): Pair<A, *> {
   this shouldNot haveFirst(a)
   return this
}

fun <A> haveFirst(a: A) = object : Matcher<Pair<A, *>> {
   override fun test(value: Pair<A, *>): MatcherResult {
      return MatcherResult(
         value.first == a,
         { "Pair $value should have first value $a but was ${value.first}" },
         {
            "Pair $value should not have first value $a"
         })
   }
}

fun <B> Pair<*, B>.shouldHaveSecond(b: B): Pair<*, B> {
   this should haveSecond(b)
   return this
}

fun <B> Pair<*, B>.shouldNotHaveSecond(b: B): Pair<*, B> {
   this shouldNot haveSecond(b)
   return this
}

fun <B> haveSecond(b: B) = object : Matcher<Pair<*, B>> {
   override fun test(value: Pair<*, B>): MatcherResult {
      return MatcherResult(
         value.second == b,
         { "Pair $value should have second value $b but was ${value.second}" },
         {
            "Pair $value should not have second value $b"
         })
   }
}
