package com.sksamuel.kotlintest.matchers.date

import io.kotlintest.matchers.date.after
import io.kotlintest.matchers.date.before
import io.kotlintest.matchers.date.haveSameDay
import io.kotlintest.matchers.date.haveSameMonth
import io.kotlintest.matchers.date.haveSameYear
import io.kotlintest.matchers.date.shouldBeAfter
import io.kotlintest.matchers.date.shouldBeBefore
import io.kotlintest.matchers.date.shouldBeWithin
import io.kotlintest.matchers.date.shouldHaveSameDayAs
import io.kotlintest.matchers.date.shouldHaveSameMonthAs
import io.kotlintest.matchers.date.shouldHaveSameYearAs
import io.kotlintest.matchers.date.shouldBeBetween
import io.kotlintest.matchers.date.shouldNotBeAfter
import io.kotlintest.matchers.date.shouldNotBeBefore
import io.kotlintest.matchers.date.shouldNotBeWithin
import io.kotlintest.matchers.date.shouldNotHaveSameDayAs
import io.kotlintest.matchers.date.shouldNotHaveSameMonthAs
import io.kotlintest.matchers.date.shouldNotHaveSameYearAs
import io.kotlintest.matchers.date.shouldNotBeBetween
import io.kotlintest.matchers.date.within
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset

class DateMatchersTest : StringSpec() {
  init {

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

    "OffsetDateTime shouldBe before" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) shouldBe before(LocalDateTime.of(2016, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNotBe before(LocalDateTime.of(2012, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC).shouldBeBefore(LocalDateTime.of(2016, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC).shouldNotBeBefore(LocalDateTime.of(2012, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
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
  }
}
