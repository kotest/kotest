package com.sksamuel.kotlintest.tests.matchers.date

import io.kotlintest.matchers.date.haveSameDay
import io.kotlintest.matchers.date.haveSameMonth
import io.kotlintest.matchers.date.haveSameYear
import io.kotlintest.matchers.should
import io.kotlintest.shouldNot
import io.kotlintest.specs.StringSpec
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class DateMatchersTest : StringSpec() {
  init {

    "LocalDate should have same year ignoring other fields" {
      LocalDate.of(2014, 1, 2) should haveSameYear(LocalDate.of(2014, 5, 6))
      LocalDate.of(2014, 1, 2) shouldNot haveSameYear(LocalDate.of(2018, 5, 6))
    }

    "LocalDateTime should have same year ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) should haveSameYear(LocalDateTime.of(2014, 5, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNot haveSameYear(LocalDateTime.of(2018, 5, 6, 3, 2, 1))
    }

    "ZonedDateTime should have same year ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) should haveSameYear(LocalDateTime.of(2014, 5, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNot haveSameYear(LocalDateTime.of(2018, 5, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
    }

    "OffsetDateTime should have same year ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) should haveSameYear(LocalDateTime.of(2014, 5, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNot haveSameYear(LocalDateTime.of(2018, 5, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }

    "LocalDate should have same month ignoring other fields" {
      LocalDate.of(2014, 1, 2) should haveSameMonth(LocalDate.of(2016, 1, 6))
      LocalDate.of(2014, 1, 2) shouldNot haveSameMonth(LocalDate.of(2018, 4, 6))
    }

    "LocalDateTime should have same month ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) should haveSameMonth(LocalDateTime.of(2014, 1, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNot haveSameMonth(LocalDateTime.of(2018, 2, 6, 3, 2, 1))
    }

    "ZonedDateTime should have same month ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) should haveSameMonth(LocalDateTime.of(2014, 1, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNot haveSameMonth(LocalDateTime.of(2018, 2, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
    }

    "OffsetDateTime should have same month ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) should haveSameMonth(LocalDateTime.of(2014, 1, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNot haveSameMonth(LocalDateTime.of(2018, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }

    "LocalDate should have same day ignoring other fields" {
      LocalDate.of(2014, 1, 2) should haveSameDay(LocalDate.of(2016, 1, 6))
      LocalDate.of(2014, 1, 2) shouldNot haveSameDay(LocalDate.of(2018, 4, 6))
    }

    "LocalDateTime should have same day ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2) should haveSameDay(LocalDateTime.of(2014, 1, 6, 3, 2, 1))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1) shouldNot haveSameDay(LocalDateTime.of(2018, 2, 6, 3, 2, 1))
    }

    "ZonedDateTime should have same day ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atZone(ZoneId.of("Z")) should haveSameDay(LocalDateTime.of(2014, 1, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atZone(ZoneId.of("Z")) shouldNot haveSameDay(LocalDateTime.of(2018, 2, 6, 3, 2, 1).atZone(ZoneId.of("Z")))
    }

    "OffsetDateTime should have same day ignoring other fields" {
      LocalDateTime.of(2014, 1, 2, 4, 3, 2).atOffset(ZoneOffset.UTC) should haveSameDay(LocalDateTime.of(2014, 1, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
      LocalDateTime.of(2014, 1, 2, 3, 2, 1).atOffset(ZoneOffset.UTC) shouldNot haveSameDay(LocalDateTime.of(2018, 2, 6, 3, 2, 1).atOffset(ZoneOffset.UTC))
    }
  }
}
