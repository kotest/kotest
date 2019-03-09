@file:Suppress("DEPRECATION")

package io.kotlintest.matchers

import io.kotlintest.*

fun withClue(clue: String, thunk: () -> Any) {
  val currentClue = ErrorCollector.clueContext.get()
  try {
    ErrorCollector.clueContext.set("$clue ")
    thunk()
  } finally {
    ErrorCollector.clueContext.set(currentClue)
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
