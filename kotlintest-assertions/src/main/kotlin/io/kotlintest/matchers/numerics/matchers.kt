package io.kotlintest.matchers.numerics

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

infix  fun Int.shouldBeInRange(range: IntRange) = this should beInRange(range)
infix  fun Int.shouldNotBeInRange(range: IntRange) = this shouldNot beInRange(range)
fun beInRange(range: IntRange) = object : Matcher<Int> {
  override fun test(value: Int): Result =
      Result(
          value in range,
          "$value should be in range $range",
          "$value should not be in range $range"
      )
}

infix fun Long.shouldBeInRange(range: LongRange) = this should beInRange(range)
infix fun Long.shouldNotBeInRange(range: LongRange) = this shouldNot beInRange(range)
fun beInRange(range: LongRange) = object : Matcher<Long> {
  override fun test(value: Long): Result =
      Result(
          value in range,
          "$value should be in range $range",
          "$value should not be in range $range"
      )
}