package io.kotest.matchers.date

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.time.OffsetDateTime

fun beInTodayODT() = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): MatcherResult {
    val passed = value.toLocalDate() == OffsetDateTime.now().toLocalDate()
    return MatcherResult(
      passed,
      "$value should be today",
      "$value should not be today"
    )
  }
}

/**
 * Asserts that the OffsetDateTime has a date component of today
 *
 * ```
 *      OffsetDateTime.now().shouldBeToday() // Assertion passes
 * ```
 */
fun OffsetDateTime.shouldBeToday() = this should beInTodayODT()


/**
 * Asserts that the OffsetDateTime does not have a date component of today
 *
 * ```
 *      OffsetDateTime.of(2009, Month.APRIL, 2,2,2).shouldNotBeToday() // Assertion passes
 * ```
 */
fun OffsetDateTime.shouldNotBeToday() = this shouldNot beInTodayODT()

