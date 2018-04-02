package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result

fun haveSameHashCode(other: Any) = object : Matcher<Any> {
  override fun test(value: Any): Result {
    return Result(
        value.hashCode() == other.hashCode(),
        "Value $value should have hash code ${other.hashCode()}",
        "Value $value should not have hash code ${other.hashCode()}"
    )
  }
}