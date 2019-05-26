@file:Suppress("DEPRECATION")

package io.kotlintest.matchers

import io.kotlintest.*

fun <ReturnType> withClue(clue: Any, thunk: () -> ReturnType): ReturnType {
    return clue.asClue { thunk() }
}

fun <ClueType, ReturnType> ClueType.asClue(body: (ClueType) -> ReturnType): ReturnType {
  try {
    ErrorCollector.clueContext.get().push(this)
    return body(this)
  } finally {
    ErrorCollector.clueContext.get().pop()
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
