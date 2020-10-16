package io.kotest.matchers.date

import com.soywiz.klock.Date
import com.soywiz.klock.DateTime
import com.soywiz.klock.Time
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

class DateMatchersTest : StringSpec() {
   init {

      // Time tests

      "Time should have same millis ignoring other fields" {
         Time(1, 2, 3, 4) should haveSameMilliseconds(Time(5, 6, 7, 4))
         Time(1, 2, 3, 4) shouldNot haveSameMilliseconds(Time(1, 2, 3, 8))
         Time(1, 2, 3, 4) shouldHaveSameMillisecondsAs (Time(5, 6, 7, 4))
         Time(1, 2, 3, 4) shouldNotHaveSameMillisecondsAs (Time(1, 2, 3, 8))

         Time(1, 2, 3, 4) should haveMilliseconds(4.toLong())
         Time(1, 2, 3, 4) shouldNot haveMilliseconds(8.toLong())
         Time(1, 2, 3, 4) shouldHaveMilliseconds (4.toLong())
         Time(1, 2, 3, 4) shouldNotHaveMilliseconds (8.toLong())
      }

      "Time should have same seconds ignoring other fields" {
         Time(1, 2, 3, 4) should haveSameSeconds(Time(5, 6, 3, 4))
         Time(1, 2, 3, 4) shouldNot haveSameSeconds(Time(1, 2, 5, 4))
         Time(1, 2, 3, 4).shouldHaveSameSecondsAs(Time(5, 6, 3, 4))
         Time(1, 2, 3, 4).shouldNotHaveSameSecondsAs(Time(1, 2, 5, 4))

         Time(1, 2, 3, 4) should haveSeconds(3)
         Time(1, 2, 3, 4) shouldNot haveSeconds(5)
         Time(1, 2, 3, 4) shouldHaveSeconds (3)
         Time(1, 2, 3, 4) shouldNotHaveSeconds (5)
      }

      "Time should have same minutes ignoring other fields" {
         Time(1, 2, 3, 4) should haveSameMinutes(Time(5, 2, 7, 8))
         Time(1, 2, 3, 4) shouldNot haveSameMinutes(Time(1, 5, 3, 4))
         Time(1, 2, 3, 4) shouldHaveSameMinutesAs Time(5, 2, 7, 8)
         Time(1, 2, 3, 4) shouldNotHaveSameMinutesAs Time(1, 5, 3, 4)

         Time(1, 2, 3, 4) should haveMinutes(2)
         Time(1, 2, 3, 4) shouldNot haveMinutes(5)
         Time(1, 2, 3, 4) shouldHaveMinutes (2)
         Time(1, 2, 3, 4) shouldNotHaveMinutes (5)
      }

      "Time should have same hours ignoring other fields" {
         Time(12, 1, 2, 0) should haveSameHours(Time(12, 59, 58, 0))
         Time(3, 59, 58, 0) shouldNot haveSameHours(Time(12, 59, 58, 0))
         Time(12, 1, 2, 0) shouldHaveSameHoursAs Time(12, 59, 58, 0)
         Time(3, 59, 58, 0) shouldNotHaveSameHoursAs Time(12, 59, 58, 0)

         Time(12, 1, 2, 0) should haveHours(12)
         Time(3, 59, 58, 0) shouldNot haveHours(12)
         Time(12, 1, 2, 0) shouldHaveHours (12)
         Time(3, 59, 58, 0) shouldNotHaveHours (12)
      }

      "Time should be after" {
         Time(12, 1, 2, 0) should after(Time(10, 1, 2, 0))
         Time(7, 1, 2, 0) shouldNot after(Time(10, 1, 2, 0))
         Time(12, 1, 2, 0) shouldBeAfter (Time(10, 1, 2, 0))
         Time(7, 1, 2, 0) shouldNotBeAfter (Time(10, 1, 2, 0))
      }

      "Time should be before" {
         Time(7, 1, 2, 0) should before(Time(10, 1, 2, 0))
         Time(12, 1, 2, 0) shouldNot before(Time(10, 1, 2, 0))
         Time(7, 1, 2, 0) shouldBeBefore (Time(10, 1, 2, 0))
         Time(12, 1, 2, 0) shouldNotBeBefore (Time(10, 1, 2, 0))
      }

      "Time is between" {
         Time(11, 1, 2, 0) should between(Time(10, 1, 2, 0), Time(12, 1, 2, 0))
         Time(7, 1, 2, 0) shouldNot between(Time(10, 1, 2, 0), Time(12, 1, 2, 0))
         Time(11, 1, 2, 0).shouldBeBetween(Time(10, 1, 2, 0), Time(12, 1, 2, 0))
         Time(7, 1, 2, 0).shouldNotBeBetween(Time(10, 1, 2, 0), Time(12, 1, 2, 0))
      }

      // Date tests

      "Date should have same year ignoring other fields" {
         Date(2014, 1, 2) should haveSameYear(Date(2014, 5, 6))
         Date(2014, 1, 2) shouldNot haveSameYear(Date(2018, 5, 6))
         Date(2014, 1, 2) shouldHaveSameYearAs (Date(2014, 5, 6))
         Date(2014, 1, 2) shouldNotHaveSameYearAs (Date(2018, 5, 6))

         Date(2014, 1, 2) should haveYear(2014)
         Date(2014, 1, 2) shouldNot haveYear(2018)
         Date(2014, 1, 2) shouldHaveYear (2014)
         Date(2014, 1, 2) shouldNotHaveYear (2018)
      }

      "Date should have same month ignoring other fields" {
         Date(2014, 1, 2) should haveSameMonth(Date(2016, 1, 6))
         Date(2014, 1, 2) shouldNot haveSameMonth(Date(2018, 4, 6))
         Date(2014, 1, 2) shouldHaveSameMonthAs (Date(2016, 1, 6))
         Date(2014, 1, 2) shouldNotHaveSameMonthAs (Date(2018, 4, 6))

         Date(2014, 1, 2) should haveMonth(1)
         Date(2014, 1, 2) shouldNot haveMonth(4)
         Date(2014, 1, 2) shouldHaveMonth (1)
         Date(2014, 1, 2) shouldNotHaveMonth (4)
      }

      "Date should have same day ignoring other fields" {
         Date(2014, 1, 2) should haveSameDay(Date(2014, 1, 2))
         Date(2014, 1, 2) shouldNot haveSameDay(Date(2014, 4, 6))
         Date(2014, 1, 2) shouldHaveSameDayAs (Date(2014, 1, 2))
         Date(2014, 1, 2) shouldNotHaveSameDayAs (Date(2014, 4, 6))

         Date(2014, 1, 2) should haveDay(2)
         Date(2014, 1, 2) shouldNot haveDay(6)
         Date(2014, 1, 2) shouldHaveDay (2)
         Date(2014, 1, 2) shouldNotHaveDay (6)
      }

      "Date.shouldBeToday() should match today" {
         DateTime.now().date.shouldBeToday()
      }

      "Date.shouldBeToday() should not match the past" {
         shouldFail {
            Date(2002, 4, 1).shouldBeToday()
         }
      }

      "Date.shouldNotBeToday()" {
         Date(2002, 4, 2).shouldNotBeToday()
         shouldFail {
            DateTime.now().date.shouldNotBeToday()
         }
      }

      "Date should be after" {
         Date(2014, 1, 2) should after(Date(2012, 1, 2))
         Date(2014, 1, 2) shouldNot after(Date(2016, 4, 6))
         Date(2014, 1, 2) shouldBeAfter (Date(2012, 1, 2))
      }

      "Date should be before" {
         Date(2014, 1, 2) should before(Date(2016, 4, 6))
         Date(2014, 1, 2) shouldNot before(Date(2012, 1, 2))
         Date(2014, 1, 2) shouldBeBefore (Date(2016, 1, 2))
      }

      "Date should be between" {
         Date(2014, 1, 2) should between(Date(2012, 4, 6), Date(2016, 4, 6))
         Date(2010, 1, 2) shouldNot between(Date(2012, 4, 6), Date(2016, 4, 6))
         Date(2014, 1, 2).shouldBeBetween(Date(2012, 4, 6), Date(2016, 4, 6))
         Date(2010, 1, 2).shouldNotBeBetween(Date(2012, 4, 6), Date(2016, 4, 6))
      }

   }
}
