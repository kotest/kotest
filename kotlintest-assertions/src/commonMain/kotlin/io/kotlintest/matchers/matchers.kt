@file:Suppress("DEPRECATION")

package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

fun withClue(clue: String, thunk: () -> Any) {
  val currentClue = AssertionErrorCollector.getClueContext()
  try {
    AssertionErrorCollector.setClueContext("$clue ")
    thunk()
  } finally {
    AssertionErrorCollector.setClueContext(currentClue)
  }
}

fun Any.shouldHaveSameHashCodeAs(other: Any) = this should haveSameHashCodeAs(other)
fun Any.shouldNotHaveSameHashCodeAs(other: Any) = this shouldNot haveSameHashCodeAs(other)

fun haveSameHashCodeAs(other: Any) = object : Matcher<Any> {
  override fun test(value: Any): Result {
    return Result(
        value.hashCode() == other.hashCode(),
        "Value $value should have hash code ${other.hashCode()}",
        "Value $value should not have hash code ${other.hashCode()}"
    )
  }
}
