package io.kotlintest.matchers.numerics

import io.kotlintest.Matcher
import io.kotlintest.Result

fun beInRange(range: IntRange) = object : Matcher<Int> {
  override fun test(value: Int): Result =
      Result(
          value in range,
          "$value should be in range $range",
          "$value should not be in range $range"
      )
}

fun beInRange(range: LongRange) = object : Matcher<Long> {
  override fun test(value: Long): Result =
      Result(
          value in range,
          "$value should be in range $range",
          "$value should not be in range $range"
      )
}