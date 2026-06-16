package com.sksamuel.kotest.matchers.date

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.date.after
import io.kotest.matchers.date.atSameZone
import io.kotest.matchers.date.before
import io.kotest.matchers.date.haveSameDay
import io.kotest.matchers.date.haveSameHours
import io.kotest.matchers.date.haveSameInstantAs
import io.kotest.matchers.date.haveSameMinutes
import io.kotest.matchers.date.haveSameMonth
import io.kotest.matchers.date.haveSameNanos
import io.kotest.matchers.date.haveSameSeconds
import io.kotest.matchers.date.haveSameYear
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.date.shouldBeBetween
import io.kotest.matchers.date.shouldBeToday
import io.kotest.matchers.date.shouldBeWithin
import io.kotest.matchers.date.shouldHaveDayOfMonth
import io.kotest.matchers.date.shouldHaveDayOfWeek
import io.kotest.matchers.date.shouldHaveDayOfYear
import io.kotest.matchers.date.shouldHaveHour
import io.kotest.matchers.date.shouldHaveMinute
import io.kotest.matchers.date.shouldHaveMonth
import io.kotest.matchers.date.shouldHaveNano
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.date.shouldHaveSameHoursAs
import io.kotest.matchers.date.shouldHaveSameInstantAs
import io.kotest.matchers.date.shouldHaveSameMinutesAs
import io.kotest.matchers.date.shouldHaveSameMonthAs
import io.kotest.matchers.date.shouldHaveSameNanosAs
import io.kotest.matchers.date.shouldHaveSameSecondsAs
import io.kotest.matchers.date.shouldHaveSameYearAs
import io.kotest.matchers.date.shouldHaveSecond
import io.kotest.matchers.date.shouldNotBeAfter
import io.kotest.matchers.date.shouldNotBeBefore
import io.kotest.matchers.date.shouldNotBeBetween
import io.kotest.matchers.date.shouldNotBeToday
import io.kotest.matchers.date.shouldNotBeWithin
import io.kotest.matchers.date.shouldNotHaveSameDayAs
import io.kotest.matchers.date.shouldNotHaveSameHoursAs
import io.kotest.matchers.date.shouldNotHaveSameInstantAs
import io.kotest.matchers.date.shouldNotHaveSameMinutesAs
import io.kotest.matchers.date.shouldNotHaveSameMonthAs
import io.kotest.matchers.date.shouldNotHaveSameNanosAs
import io.kotest.matchers.date.shouldNotHaveSameSecondsAs
import io.kotest.matchers.date.shouldNotHaveSameYearAs
import io.kotest.matchers.date.within
import io.kotest.matchers.shouldBe
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import java.time.DayOfWeek.SATURDAY
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DateMatchersTest : StringSpec() {
  init {

    "LocalTime should have same nanos ignoring other fields" {
      LocalTime.of(1, 2, 3, 4) should haveSameNanos(LocalTime.of(5, 6, 7, 4))
      LocalTime.of(1, 2, 3, 4) shouldNot haveSameNanos(LocalTime.of(1, 2, 3, 8))
      LocalTime.of(1, 2, 3, 4).shouldHaveSameNanosAs(LocalTime.of(5, 6, 7, 4))
      LocalTime.of(1, 2, 3, 4).shouldNotHaveSameNanosAs(LocalTime.of(1, 2, 3, 8))
    }

    "LocalTime should have same seconds ignoring other fields" {
      LocalTime.of(1, 2, 3, 4) should haveSameSeconds(LocalTime.of(5, 6, 3, 4))
      LocalTime.of(1, 2, 3, 4) shouldNot haveSameSeconds(LocalTime.of(1, 2, 5, 4))
      LocalTime.of(1, 2, 3, 4).shouldHaveSameSecondsAs(LocalTime.of(5, 6, 3, 4))
      LocalTime.of(1, 2, 3, 4).shouldNotHaveSameSecondsAs(LocalTime.of(1, 2, 5, 4))
    }

    "LocalTime should have same minutes ignoring other fields" {
      LocalTime.of(1, 2, 3, 4) should haveSameMinutes(LocalTime.of(5, 2, 7, 8))
      LocalTime.of(1, 2, 3, 4) shouldNot haveSameMinutes(LocalTime.of(1, 5, 3, 4))
      LocalTime.of(1, 2, 3, 4).shouldHaveSameMinutesAs(LocalTime.of(5, 2, 7, 8))
      LocalTime.of(1, 2, 3, 4).shouldNotHaveSameMinutesAs(LocalTime.of(1, 5, 3, 4))
    }

    "LocalTime should have same hours ignoring other fields" {
      LocalTime.of(12, 1, 2, 7777) should haveSameHours(LocalTime.of(12, 59, 58, 9999))
      LocalTime.of(3, 59, 58, 9999) shouldNot haveSameHours(LocalTime.of(12, 59, 58, 9999))
      LocalTime.of(12, 1, 2, 7777).shouldHaveSameHoursAs(LocalTime.of(12, 59, 58, 9999))
      LocalTime.of(3, 59, 58, 9999).shouldNotHaveSameHoursAs(LocalTime.of(12, 59, 58, 9999))
    }

    "LocalDate should have same year ignoring other fields" {
      LocalDate.of(2014, 1, 2) should haveSameYear(LocalDate.of(2014, 5, 6))
      LocalDate.of(2014, 1, 2) shouldNot haveSameYear(LocalDate.of(2018, 5, 6))
      LocalDate.of(2014, 1, 2).shouldHaveSameYearAs(LocalDate.of(2014, 5, 6))
      LocalDate.of(2014, 1, 2).shouldNotHaveSameYearAs(LocalDate.of(2018, 5, 6))
    }

    "LocalDateTime should have same year ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) should haveSameYear(LocalDateTime.of(2014, 5, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNot haveSameYear(LocalDateTime.of(2018, 5, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).shouldHaveSameYearAs(LocalDateTime.of(2014, 5, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).shouldNotHaveSameYearAs(LocalDateTime.of(2018, 5, 6, 3, 2, 1))
    }

    "ZonedDateTime should have same year ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) should haveSameYear(LocalDateTime.of(2014, 5, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNot haveSameYear(LocalDateTime.of(2018, 5, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldHaveSameYearAs(LocalDateTime.of(2014, 5, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotHaveSameYearAs(LocalDateTime.of(2018, 5, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
    }

    "OffsetDateTime should have same year ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) should haveSameYear(LocalDateTime.of(2014, 5, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNot haveSameYear(LocalDateTime.of(2018, 5, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC).shouldHaveSameYearAs(LocalDateTime.of(2014, 5, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC).shouldNotHaveSameYearAs(LocalDateTime.of(2018, 5, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }

    "LocalDate should have same month ignoring other fields" {
      LocalDate.of(2014, 1, 2) should haveSameMonth(LocalDate.of(2016, 1, 6))
      LocalDate.of(2014, 1, 2) shouldNot haveSameMonth(LocalDate.of(2018, 4, 6))
      LocalDate.of(2014, 1, 2).shouldHaveSameMonthAs(LocalDate.of(2016, 1, 6))
      LocalDate.of(2014, 1, 2).shouldNotHaveSameMonthAs(LocalDate.of(2018, 4, 6))
    }

    "LocalDateTime should have same month ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) should haveSameMonth(LocalDateTime.of(2014, 1, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNot haveSameMonth(LocalDateTime.of(2018, 2, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).shouldHaveSameMonthAs(LocalDateTime.of(2014, 1, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).shouldNotHaveSameMonthAs(LocalDateTime.of(2018, 2, 6, 3, 2, 1))
    }

    "ZonedDateTime should have same month ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) should haveSameMonth(LocalDateTime.of(2014, 1, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNot haveSameMonth(LocalDateTime.of(2018, 2, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldHaveSameMonthAs(LocalDateTime.of(2014, 1, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotHaveSameMonthAs(LocalDateTime.of(2018, 2, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
    }

    "OffsetDateTime should have same month ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) should haveSameMonth(LocalDateTime.of(2014, 1, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNot haveSameMonth(LocalDateTime.of(2018, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC).shouldHaveSameMonthAs(LocalDateTime.of(2014, 1, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC).shouldNotHaveSameMonthAs(LocalDateTime.of(2018, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }

    "LocalDate should have same day ignoring other fields" {
      LocalDate.of(2014, 1, 2) should haveSameDay(LocalDate.of(2014, 1, 2))
      LocalDate.of(2014, 1, 2) shouldNot haveSameDay(LocalDate.of(2014, 4, 6))
      LocalDate.of(2014, 1, 2).shouldHaveSameDayAs(LocalDate.of(2014, 1, 2))
      LocalDate.of(2014, 1, 2).shouldNotHaveSameDayAs(LocalDate.of(2014, 4, 6))

    }

    "LocalDateTime should have same day ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) should haveSameDay(LocalDateTime.of(2014, 1, 2, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNot haveSameDay(LocalDateTime.of(2014, 2, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).shouldHaveSameDayAs(LocalDateTime.of(2014, 1, 2, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).shouldNotHaveSameDayAs(LocalDateTime.of(2014, 2, 6, 3, 2, 1))
    }

    "ZonedDateTime should have same day ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) should haveSameDay(LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNot haveSameDay(LocalDateTime.of(2014, 2, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldHaveSameDayAs(LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotHaveSameDayAs(LocalDateTime.of(2014, 2, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
    }

    "OffsetDateTime should have same day ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) should haveSameDay(LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNot haveSameDay(LocalDateTime.of(2014, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC).shouldHaveSameDayAs(LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC).shouldNotHaveSameDayAs(LocalDateTime.of(2014, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }

    "LocalTime shouldBe before" {
      LocalTime.of(1, 2, 3, 1000) shouldBe before(LocalTime.of(1, 10, 3, 1000))
      LocalTime.of(1, 2, 3, 1000) shouldNotBe before(LocalTime.of(1, 1, 3, 1000))
      LocalTime.of(1, 2, 3, 1000).shouldBeBefore(LocalTime.of(5, 2, 3, 1000))
      LocalTime.of(1, 2, 3, 1000).shouldNotBeBefore(LocalTime.of(0, 2, 3, 1000))
    }

    "LocalDate shouldBe before" {
      LocalDate.of(2014, 1, 2) shouldBe before(LocalDate.of(2014, 1, 3))
      LocalDate.of(2014, 1, 2) shouldNotBe before(LocalDate.of(2014, 1, 1))
      LocalDate.of(2014, 1, 2).shouldBeBefore(LocalDate.of(2014, 1, 3))
      LocalDate.of(2014, 1, 2).shouldNotBeBefore(LocalDate.of(2014, 1, 1))
    }

    "LocalDateTime shouldBe before" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) shouldBe before(LocalDateTime.of(2014, 2, 2, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNotBe before(LocalDateTime.of(2014, 1, 1, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).shouldBeBefore(LocalDateTime.of(2014, 2, 2, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).shouldNotBeBefore(LocalDateTime.of(2014, 1, 1, 3, 2, 1))
    }

    "ZonedDateTime shouldBe before" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) shouldBe before(LocalDateTime.of(2014, 1, 3, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNotBe before(LocalDateTime.of(2014, 1, 1, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldBeBefore(LocalDateTime.of(2014, 1, 3, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotBeBefore(LocalDateTime.of(2014, 1, 1, 3, 2, 1).atZone(ZoneId.of("Z")))
    }

    "ZonedDateTime shouldBe equal" {
      ZonedDateTime.of(2019, 12, 10, 10, 0, 0, 0, ZoneOffset.UTC) shouldBe
        ZonedDateTime.of(2019, 12, 10, 4, 0, 0, 0, ZoneId.of("America/Chicago")).atSameZone()

      shouldThrow<AssertionError> {
        ZonedDateTime.of(2019, 12, 10, 10, 0, 0, 0, ZoneOffset.UTC) shouldBe
          ZonedDateTime.of(2019, 12, 10, 4, 1, 0, 0, ZoneId.of("America/Chicago")).atSameZone()
      }.message shouldBe "expected:<2019-12-10T10:01Z> but was:<2019-12-10T10:00Z>"
    }

    "OffsetDateTime shouldBe before" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) shouldBe before(LocalDateTime.of(2016, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNotBe before(LocalDateTime.of(2012, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC).shouldBeBefore(LocalDateTime.of(2016, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC).shouldNotBeBefore(LocalDateTime.of(2012, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }

    "LocalTime shouldBe after" {
      LocalTime.of(1, 2, 3, 9999) shouldBe after(LocalTime.of(1, 2, 3, 1111))
      LocalTime.of(1, 2, 3, 1000) shouldNotBe after(LocalTime.of(1, 2, 10, 1000))
      LocalTime.of(1, 2, 3, 9999).shouldBeAfter(LocalTime.of(1, 2, 3, 1111))
      LocalTime.of(1, 2, 3, 1000).shouldNotBeAfter(LocalTime.of(1, 2, 10, 1000))
    }

    "LocalDate shouldBe after" {
      LocalDate.of(2014, 1, 2) shouldBe after(LocalDate.of(2013, 1, 3))
      LocalDate.of(2014, 1, 2) shouldNotBe after(LocalDate.of(2014, 1, 3))
      LocalDate.of(2014, 1, 2).shouldBeAfter(LocalDate.of(2013, 1, 3))
      LocalDate.of(2014, 1, 2).shouldNotBeAfter(LocalDate.of(2014, 1, 3))
    }

    "LocalDateTime shouldBe after" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) shouldBe after(LocalDateTime.of(2014, 1, 1, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNotBe after(LocalDateTime.of(2014, 1, 3, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).shouldBeAfter(LocalDateTime.of(2014, 1, 1, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).shouldNotBeAfter(LocalDateTime.of(2014, 1, 3, 3, 2, 1))
    }

    "ZonedDateTime shouldBe after" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) shouldBe after(LocalDateTime.of(2014, 1, 1, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNotBe after(LocalDateTime.of(2014, 1, 3, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldBeAfter(LocalDateTime.of(2014, 1, 1, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotBeAfter(LocalDateTime.of(2014, 1, 3, 3, 2, 1).atZone(ZoneId.of("Z")))
    }

    "OffsetDateTime shouldBe after" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) shouldBe after(LocalDateTime.of(2014, 1, 1, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNotBe after(LocalDateTime.of(2014, 2, 1, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC).shouldBeAfter(LocalDateTime.of(2014, 1, 1, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC).shouldNotBeAfter(LocalDateTime.of(2014, 2, 1, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }


    "LocalDate shouldBe within(period, date)" {
      LocalDate.of(2014, 1, 2) shouldBe within(Period.ofDays(3), LocalDate.of(2014, 1, 1))
      LocalDate.of(2014, 1, 2) shouldBe within(Period.ofDays(3), LocalDate.of(2014, 1, 5))
      LocalDate.of(2014, 1, 2) shouldNotBe within(Period.ofDays(3), LocalDate.of(2014, 1, 6))
      LocalDate.of(2014, 1, 2).shouldBeWithin(Period.ofDays(3), LocalDate.of(2014, 1, 5))
      LocalDate.of(2014, 1, 2).shouldNotBeWithin(Period.ofDays(3), LocalDate.of(2014, 1, 6))
    }

    "LocalDateTime shouldBe within(period, date)" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) shouldBe within(Period.ofDays(1), LocalDateTime.of(2014, 1, 2, 9, 3, 2))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNotBe within(Period.ofDays(1), LocalDateTime.of(2014, 1, 3, 3, 2, 2))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).shouldBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 1, 2, 9, 3, 2))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).shouldNotBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 1, 3, 3, 2, 2))
    }

    "ZonedDateTime shouldBe within(period, date)" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) shouldBe within(Period.ofDays(1), LocalDateTime.of(2014, 1, 3, 4, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) shouldBe within(Period.ofDays(1), LocalDateTime.of(2014, 1, 1, 4, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) shouldBe within(Period.ofDays(1), LocalDateTime.of(2014, 1, 2, 9, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNotBe within(Period.ofDays(1), LocalDateTime.of(2014, 1, 3, 3, 2, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNotBe within(Period.ofDays(1), LocalDateTime.of(2014, 1, 1, 3, 2, 0).atZone(ZoneId.of("Z")))

      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 1, 3, 4, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 1, 1, 4, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 1, 2, 9, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 1, 3, 3, 2, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 1, 1, 3, 2, 0).atZone(ZoneId.of("Z")))
    }

    "ZonedDateTime shouldBe within(duration, date)" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) shouldBe within(Duration.ofDays(1), LocalDateTime.of(2014, 1, 3, 4, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) shouldBe within(Duration.ofDays(1), LocalDateTime.of(2014, 1, 1, 4, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) shouldBe within(Duration.ofDays(1), LocalDateTime.of(2014, 1, 2, 9, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNotBe within(Duration.ofDays(1), LocalDateTime.of(2014, 1, 3, 3, 2, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNotBe within(Duration.ofDays(1), LocalDateTime.of(2014, 1, 1, 3, 2, 0).atZone(ZoneId.of("Z")))

      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldBeWithin(Duration.ofDays(1), LocalDateTime.of(2014, 1, 3, 4, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldBeWithin(Duration.ofDays(1), LocalDateTime.of(2014, 1, 1, 4, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")).shouldBeWithin(Duration.ofDays(1), LocalDateTime.of(2014, 1, 2, 9, 3, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotBeWithin(Duration.ofDays(1), LocalDateTime.of(2014, 1, 3, 3, 2, 2).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")).shouldNotBeWithin(Duration.ofDays(1), LocalDateTime.of(2014, 1, 1, 3, 2, 0).atZone(ZoneId.of("Z")))
    }

    "OffsetDateTime shouldBe within(period, date)" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) shouldBe within(Period.ofDays(1), LocalDateTime.of(2014, 1, 3, 4, 3, 2).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNotBe within(Period.ofDays(1), LocalDateTime.of(2014, 2, 1, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC).shouldBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 1, 3, 4, 3, 2).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC).shouldNotBeWithin(Period.ofDays(1), LocalDateTime.of(2014, 2, 1, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }

    "OffsetDateTime shouldBe within(duration, date)" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) shouldBe within(Duration.ofDays(1), LocalDateTime.of(2014, 1, 3, 4, 3, 2).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNotBe within(Duration.ofDays(1), LocalDateTime.of(2014, 2, 1, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC).shouldBeWithin(Duration.ofDays(1), LocalDateTime.of(2014, 1, 3, 4, 3, 2).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC).shouldNotBeWithin(Duration.ofDays(1), LocalDateTime.of(2014, 2, 1, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }

    "LocalTime shouldBe between" {
      LocalTime.of(14, 20, 50, 1000).shouldBeBetween(LocalTime.of(14, 20, 49), LocalTime.of(14, 20, 51))
      LocalTime.of(14, 20, 50, 1000).shouldNotBeBetween(LocalTime.of(14, 20, 51), LocalTime.of(14, 20, 52))
    }

    "LocalDate shouldBe between" {
      LocalDate.of(2019, 2, 16).shouldBeBetween(LocalDate.of(2019, 2, 15), LocalDate.of(2019, 2, 17))
      LocalDate.of(2019, 2, 16).shouldNotBeBetween(LocalDate.of(2019, 2, 17), LocalDate.of(2019, 2, 18))
    }

    "LocalDateTime shouldBe between" {
      LocalDateTime.of(2019, 2, 16, 12, 0, 0).shouldBeBetween(LocalDateTime.of(2019, 2, 15, 12, 0, 0), LocalDateTime.of(2019, 2, 17, 12, 0, 0))
      LocalDateTime.of(2019, 2, 16, 12, 0, 0).shouldBeBetween(LocalDateTime.of(2019, 2, 16, 10, 0, 0), LocalDateTime.of(2019, 2, 16, 14, 0, 0))
      LocalDateTime.of(2019, 2, 16, 12, 0, 0).shouldNotBeBetween(LocalDateTime.of(2019, 2, 17, 12, 0, 0), LocalDateTime.of(2019, 2, 18, 12, 0, 0))
      LocalDateTime.of(2019, 2, 16, 12, 0, 0).shouldNotBeBetween(LocalDateTime.of(2019, 2, 16, 18, 0, 0), LocalDateTime.of(2019, 2, 16, 20, 0, 0))
    }

    "ZonedDateTime shouldBe between" {
      ZonedDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo")).shouldBeBetween(ZonedDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo")), ZonedDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo")))
      ZonedDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo")).shouldNotBeBetween(ZonedDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo")), ZonedDateTime.of(2019, 2, 18, 12, 0, 0, 0, ZoneId.of("America/Sao_Paulo")))
    }

    "OffsetDateTime shouldBe between" {
      OffsetDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneOffset.ofHours(-3)).shouldBeBetween(OffsetDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneOffset.ofHours(-3)), OffsetDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneOffset.ofHours(-3)))
      OffsetDateTime.of(2019, 2, 15, 12, 0, 0, 0, ZoneOffset.ofHours(-3)).shouldNotBeBetween(OffsetDateTime.of(2019, 2, 16, 12, 0, 0, 0, ZoneOffset.ofHours(-3)), OffsetDateTime.of(2019, 2, 17, 12, 0, 0, 0, ZoneOffset.ofHours(-3)))
    }

    "LocalDate.shouldBeToday() should match today" {
      LocalDate.now().shouldBeToday()
    }

    "LocalDateTime.shouldBeToday() should match today" {
      LocalDateTime.now().shouldBeToday()
    }

    "LocalDate.shouldBeToday() should not match the past" {
      shouldFail {
        LocalDate.of(2002, Month.APRIL, 1).shouldBeToday()
      }
    }

    "LocalDateTime.shouldBeToday() should not match the past" {
      shouldFail {
        LocalDateTime.of(2002, Month.APRIL, 1, 5, 2).shouldBeToday()
      }
      shouldFail {
        LocalDateTime.now().minusDays(1).shouldBeToday()
      }
    }

    "LocalDateTime.shouldNotBeToday()" {
      LocalDateTime.of(2002, Month.APRIL, 1, 5, 2).shouldNotBeToday()
      shouldFail {
        LocalDateTime.now().shouldNotBeToday()
      }
    }

    "LocalDate.shouldNotBeToday()" {
      LocalDate.of(2002, Month.APRIL, 2).shouldNotBeToday()
      shouldFail {
        LocalDateTime.now().shouldNotBeToday()
      }
    }

    "LocalDateTime should have day of month (day)" {
      LocalDateTime.of(2019, 2, 16, 12, 0, 0, 0) shouldHaveDayOfMonth 16
    }

    "LocalDateTime should have day of week" {
      LocalDateTime.of(2019, 2, 16, 12, 0, 0, 0) shouldHaveDayOfWeek  SATURDAY
      LocalDateTime.of(2019, 2, 16, 12, 0, 0, 0) shouldHaveDayOfWeek  6
    }

    "LocalDateTime should have day of year" {
      LocalDateTime.of(2019, 2, 16, 12, 0, 0, 0) shouldHaveDayOfYear  47
    }

    "LocalDateTime should have month" {
      LocalDateTime.of(2019, 2, 16, 12, 0, 0, 0) shouldHaveMonth 2
      LocalDateTime.of(2019, 2, 16, 12, 0, 0, 0) shouldHaveMonth Month.FEBRUARY
    }
    "LocalDateTime should have hour" {
      LocalDateTime.of(2019, 2, 16, 12, 10, 0, 0) shouldHaveHour 12
    }
    "LocalDateTime should have minute" {
      LocalDateTime.of(2019, 2, 16, 12, 10, 0, 0) shouldHaveMinute 10
    }
    "LocalDateTime should have second" {
      LocalDateTime.of(2019, 2, 16, 12, 10, 13, 0) shouldHaveSecond  13
    }
    "LocalDateTime should have nano" {
      LocalDateTime.of(2019, 2, 16, 12, 10, 0, 14) shouldHaveNano  14
    }

    "ZonedDateTime should be have same instant of the given ZonedDateTime irrespective of their timezone" {
       ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1)) should haveSameInstantAs(ZonedDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3)))
       ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1)) shouldHaveSameInstantAs ZonedDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3))
       ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1)) shouldNot haveSameInstantAs(ZonedDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-2)))
       ZonedDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1)) shouldNotHaveSameInstantAs ZonedDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-2))
    }

     "OffsetDateTime should be have same instant of the given OffsetDateTime irrespective of their timezone" {
        OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1)) should haveSameInstantAs(OffsetDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3)))
        OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1)) shouldHaveSameInstantAs OffsetDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-3))
        OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1)) shouldNot haveSameInstantAs(OffsetDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-2)))
        OffsetDateTime.of(2019, 2, 16, 11, 0, 0, 0, ZoneOffset.ofHours(-1)) shouldNotHaveSameInstantAs OffsetDateTime.of(2019, 2, 16, 9, 0, 0, 0, ZoneOffset.ofHours(-2))
     }
  }
}
