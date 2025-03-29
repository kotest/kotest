package io.kotest.matchers.kotlinx.datetime

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Duration.Companion.days

class LocalDateTimeTests : StringSpec({

    "LocalDateTime should have same year ignoring other fields" {
        LocalDateTime(2014, 1, 2, 4, 3, 2) should haveSameYear(LocalDateTime(2014, 5, 6, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1) shouldNot haveSameYear(LocalDateTime(2018, 5, 6, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 4, 3, 2).shouldHaveSameYearAs(LocalDateTime(2014, 5, 6, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1).shouldNotHaveSameYearAs(LocalDateTime(2018, 5, 6, 3, 2, 1))
    }

    "LocalDate should have same month ignoring other fields" {
        LocalDate(2014, 1, 2) should haveSameMonth(LocalDate(2016, 1, 6))
        LocalDate(2014, 1, 2) shouldNot haveSameMonth(LocalDate(2018, 4, 6))
        LocalDate(2014, 1, 2).shouldHaveSameMonthAs(LocalDate(2016, 1, 6))
        LocalDate(2014, 1, 2).shouldNotHaveSameMonthAs(LocalDate(2018, 4, 6))
    }

    "LocalDateTime should have same month ignoring other fields" {
        LocalDateTime(2014, 1, 2, 4, 3, 2) should haveSameMonth(LocalDateTime(2014, 1, 6, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1) shouldNot haveSameMonth(LocalDateTime(2018, 2, 6, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 4, 3, 2).shouldHaveSameMonthAs(LocalDateTime(2014, 1, 6, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1).shouldNotHaveSameMonthAs(LocalDateTime(2018, 2, 6, 3, 2, 1))
    }

    "LocalDate should have same day ignoring other fields" {
        LocalDate(2014, 1, 2) should haveSameDay(LocalDate(2014, 1, 2))
        LocalDate(2014, 1, 2) shouldNot haveSameDay(LocalDate(2014, 4, 6))
        LocalDate(2014, 1, 2).shouldHaveSameDayAs(LocalDate(2014, 1, 2))
        LocalDate(2014, 1, 2).shouldNotHaveSameDayAs(LocalDate(2014, 4, 6))

    }

    "LocalDateTime should have same day ignoring other fields" {
        LocalDateTime(2014, 1, 2, 4, 3, 2) should haveSameDay(LocalDateTime(2014, 1, 2, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1) shouldNot haveSameDay(LocalDateTime(2014, 2, 6, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 4, 3, 2).shouldHaveSameDayAs(LocalDateTime(2014, 1, 2, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1).shouldNotHaveSameDayAs(LocalDateTime(2014, 2, 6, 3, 2, 1))
    }


    "LocalDate shouldBe before" {
        LocalDate(2014, 1, 2) shouldBe before(LocalDate(2014, 1, 3))
        LocalDate(2014, 1, 2) shouldNotBe before(LocalDate(2014, 1, 1))
        LocalDate(2014, 1, 2).shouldBeBefore(LocalDate(2014, 1, 3))
        LocalDate(2014, 1, 2).shouldNotBeBefore(LocalDate(2014, 1, 1))
    }

    "LocalDateTime shouldBe before" {
        LocalDateTime(2014, 1, 2, 4, 3, 2) shouldBe before(LocalDateTime(2014, 2, 2, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1) shouldNotBe before(LocalDateTime(2014, 1, 1, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 4, 3, 2).shouldBeBefore(LocalDateTime(2014, 2, 2, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1).shouldNotBeBefore(LocalDateTime(2014, 1, 1, 3, 2, 1))
    }

    "LocalDate shouldBe after" {
        LocalDate(2014, 1, 2) shouldBe after(LocalDate(2013, 1, 3))
        LocalDate(2014, 1, 2) shouldNotBe after(LocalDate(2014, 1, 3))
        LocalDate(2014, 1, 2).shouldBeAfter(LocalDate(2013, 1, 3))
        LocalDate(2014, 1, 2).shouldNotBeAfter(LocalDate(2014, 1, 3))
    }

    "LocalDateTime shouldBe after" {
        LocalDateTime(2014, 1, 2, 4, 3, 2) shouldBe after(LocalDateTime(2014, 1, 1, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1) shouldNotBe after(LocalDateTime(2014, 1, 3, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 4, 3, 2).shouldBeAfter(LocalDateTime(2014, 1, 1, 3, 2, 1))
        LocalDateTime(2014, 1, 2, 3, 2, 1).shouldNotBeAfter(LocalDateTime(2014, 1, 3, 3, 2, 1))
    }

    "LocalDateTime shouldBe between" {
        LocalDateTime(2019, 2, 16, 12, 0, 0).shouldBeBetween(LocalDateTime(2019, 2, 15, 12, 0, 0), LocalDateTime(2019, 2, 17, 12, 0, 0))
        LocalDateTime(2019, 2, 16, 12, 0, 0).shouldBeBetween(LocalDateTime(2019, 2, 16, 10, 0, 0), LocalDateTime(2019, 2, 16, 14, 0, 0))
        LocalDateTime(2019, 2, 16, 12, 0, 0).shouldNotBeBetween(LocalDateTime(2019, 2, 17, 12, 0, 0), LocalDateTime(2019, 2, 18, 12, 0, 0))
        LocalDateTime(2019, 2, 16, 12, 0, 0).shouldNotBeBetween(LocalDateTime(2019, 2, 16, 18, 0, 0), LocalDateTime(2019, 2, 16, 20, 0, 0))
    }


    "LocalDate.shouldBeToday() should match today" {
        Clock.System.todayIn(TimeZone.UTC).shouldBeToday(TimeZone.UTC)
    }

    "LocalDateTime.shouldBeToday() should match today" {
        Clock.System.todayIn(TimeZone.UTC).shouldBeToday(TimeZone.UTC)
    }

    "LocalDate.shouldBeToday() should not match the past" {
        shouldFail {
            LocalDate(2002, Month.APRIL, 1).shouldBeToday(TimeZone.UTC)
        }
    }

    "LocalDateTime.shouldBeToday() should not match the past" {
        shouldFail {
            LocalDateTime(2002, Month.APRIL, 1, 5, 2).shouldBeToday(TimeZone.UTC)
        }
        shouldFail {
            Clock.System.now().minus(1.days).toLocalDateTime(TimeZone.UTC).shouldBeToday(TimeZone.UTC)
        }
    }

    "LocalDateTime.shouldNotBeToday()" {
        LocalDateTime(2002, Month.APRIL, 1, 5, 2).shouldNotBeToday(TimeZone.UTC)
        shouldFail {
            Clock.System.todayIn(TimeZone.UTC).shouldNotBeToday(TimeZone.UTC)
        }
    }

    "LocalDate.shouldNotBeToday()" {
        LocalDate(2002, Month.APRIL, 2).shouldNotBeToday()
        shouldFail {
            Clock.System.todayIn(TimeZone.UTC).shouldNotBeToday(TimeZone.UTC)
        }
    }

    "LocalDateTime should have day of month (day)" {
        LocalDateTime(2019, 2, 16, 12, 0, 0, 0) shouldHaveDayOfMonth 16
    }

    "LocalDateTime should have day of week" {
        LocalDateTime(2019, 2, 16, 12, 0, 0, 0) shouldHaveDayOfWeek DayOfWeek.SATURDAY
        LocalDateTime(2019, 2, 16, 12, 0, 0, 0) shouldHaveDayOfWeek  6
    }

    "LocalDateTime should have day of year" {
        LocalDateTime(2019, 2, 16, 12, 0, 0, 0) shouldHaveDayOfYear  47
    }

    "LocalDateTime should have month" {
        LocalDateTime(2019, 2, 16, 12, 0, 0, 0) shouldHaveMonth 2
        LocalDateTime(2019, 2, 16, 12, 0, 0, 0) shouldHaveMonth Month.FEBRUARY
    }
    "LocalDateTime should have hour" {
        LocalDateTime(2019, 2, 16, 12, 10, 0, 0) shouldHaveHour 12
    }
    "LocalDateTime should have minute" {
        LocalDateTime(2019, 2, 16, 12, 10, 0, 0) shouldHaveMinute 10
    }
    "LocalDateTime should have second" {
        LocalDateTime(2019, 2, 16, 12, 10, 13, 0) shouldHaveSecond  13
    }
    "LocalDateTime should have nano" {
        LocalDateTime(2019, 2, 16, 12, 10, 0, 14) shouldHaveNano  14
    }

})
