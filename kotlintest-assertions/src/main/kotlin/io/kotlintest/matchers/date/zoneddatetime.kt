package io.kotlintest.matchers.date

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import java.time.ZonedDateTime

fun beInTodayZDT() = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result {
    val passed = value.toLocalDate() == ZonedDateTime.now().toLocalDate()
    return Result(
      passed,
      "$value should be today",
      "$value should not be today"
    )
  }
}

/**
 * Asserts that the ZonedDateTime does not have a date component of today
 *
 * ```
 *      ZonedDateTime.of(2009, Month.APRIL, 2,2,2).shouldNotBeToday() // Assertion passes
 * ```
 */
fun ZonedDateTime.shouldNotBeToday() = this shouldNot beInTodayZDT()

/**
 * Asserts that the ZonedDateTime has a date component of today
 *
 * ```
 *      ZonedDateTime.now().shouldBeToday() // Assertion passes
 * ```
 */
fun ZonedDateTime.shouldBeToday() = this should beInTodayZDT()
