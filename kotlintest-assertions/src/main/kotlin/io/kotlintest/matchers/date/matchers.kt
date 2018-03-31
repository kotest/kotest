package io.kotlintest.matchers.date

import io.kotlintest.Matcher
import io.kotlintest.Result
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

fun haveSameYear(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

fun haveSameYear(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

fun haveSameYear(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

fun haveSameYear(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.year == date.year, "$value should have year ${date.year}", "$value should not have year ${date.year}")
}

fun haveSameMonth(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

fun haveSameMonth(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

fun haveSameMonth(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

fun haveSameMonth(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.month == date.month, "$value should have month ${date.month}", "$value should not have month ${date.month}")
}

fun haveSameDay(date: LocalDate): Matcher<LocalDate> = object : Matcher<LocalDate> {
  override fun test(value: LocalDate): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

fun haveSameDay(date: LocalDateTime): Matcher<LocalDateTime> = object : Matcher<LocalDateTime> {
  override fun test(value: LocalDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

fun haveSameDay(date: ZonedDateTime): Matcher<ZonedDateTime> = object : Matcher<ZonedDateTime> {
  override fun test(value: ZonedDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}

fun haveSameDay(date: OffsetDateTime): Matcher<OffsetDateTime> = object : Matcher<OffsetDateTime> {
  override fun test(value: OffsetDateTime): Result =
      Result(value.dayOfMonth == date.dayOfMonth, "$value should have day ${date.dayOfMonth} but had ${value.dayOfMonth}", "$value should not have day ${date.dayOfMonth}")
}