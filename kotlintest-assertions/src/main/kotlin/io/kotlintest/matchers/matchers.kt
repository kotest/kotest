@file:Suppress("DEPRECATION")

package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result

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