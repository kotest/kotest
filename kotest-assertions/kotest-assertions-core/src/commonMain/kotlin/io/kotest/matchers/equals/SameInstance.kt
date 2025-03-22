package io.kotest.matchers.equals

import io.kotest.assertions.getFailureWithTypeInformation
import io.kotest.assertions.print.print
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.math.exp

infix fun <A : Any> A.shouldBeSameInstance(expected: A): A {
   this should beSameInstance(expected)
   return this
}

infix fun <A : Any> A.shouldNotBeSameInstance(expected: A): A {
   this shouldNot beSameInstance(expected)
   return this
}

/**
 * Verifies that two values are the same using [equals].
 */
fun <A> beSameInstance(expected: A): Matcher<A> = object : Matcher<A> {
   override fun test(value: A): MatcherResult {
      val passed = value === expected
      val differenceMessage =  {
         when {
            passed -> ""
            value == expected -> "the instances were equal yet different."
            else -> getFailureWithTypeInformation(expected, value, "they were different: ").message ?: ""
         }
      }
      return MatcherResult(
         passed,
         { "<${value.print().value}> should be same instance as <${expected.print().value}>, but ${differenceMessage()}" },
         { "<${value.print().value}> should not be same instance as <${expected.print().value}>, but it was." })
   }
}
