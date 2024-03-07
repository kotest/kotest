package io.kotest.matchers.types

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun Any.shouldHaveSameHashCodeAs(other: Any) = this should haveSameHashCodeAs(other)
infix fun Any.shouldNotHaveSameHashCodeAs(other: Any) = this shouldNot haveSameHashCodeAs(other)

fun haveSameHashCodeAs(other: Any) = object : Matcher<Any> {
   override fun test(value: Any): MatcherResult {
      return MatcherResult(
         value.hashCode() == other.hashCode(),
         { "Value $value should have hash code ${other.hashCode()}" },
         {
            "Value $value should not have hash code ${other.hashCode()}"
         })
   }
}
