package io.kotest.matchers.date

import com.soywiz.klock.Time
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

class DateMatchersTest : StringSpec() {
   init {
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

         Time(1, 2, 3, 4) should haveSeconds(3.toLong())
         Time(1, 2, 3, 4) shouldNot haveSeconds(5.toLong())
         Time(1, 2, 3, 4) shouldHaveSeconds (3.toLong())
         Time(1, 2, 3, 4) shouldNotHaveSeconds (5.toLong())
      }

      "Time should have same minutes ignoring other fields" {
         Time(1, 2, 3, 4) should haveSameMinutes(Time(5, 2, 7, 8))
         Time(1, 2, 3, 4) shouldNot haveSameMinutes(Time(1, 5, 3, 4))
         Time(1, 2, 3, 4) shouldHaveSameMinutesAs Time(5, 2, 7, 8)
         Time(1, 2, 3, 4) shouldNotHaveSameMinutesAs Time(1, 5, 3, 4)

         Time(1, 2, 3, 4) should haveMinutes(2.toLong())
         Time(1, 2, 3, 4) shouldNot haveMinutes(5.toLong())
         Time(1, 2, 3, 4) shouldHaveMinutes (2.toLong())
         Time(1, 2, 3, 4) shouldNotHaveMinutes (5.toLong())
      }

      "Time should have same hours ignoring other fields" {
         Time(12, 1, 2, 7777) should haveSameHours(Time(12, 59, 58, 9999))
         Time(3, 59, 58, 9999) shouldNot haveSameHours(Time(12, 59, 58, 9999))
         Time(12, 1, 2, 7777) shouldHaveSameHoursAs Time(12, 59, 58, 9999)
         Time(3, 59, 58, 9999) shouldNotHaveSameHoursAs Time(12, 59, 58, 9999)

         Time(12, 1, 2, 7777) should haveHours(12.toLong())
         Time(3, 59, 58, 9999) shouldNot haveHours(12.toLong())
         Time(12, 1, 2, 7777) shouldHaveHours (12.toLong())
         Time(3, 59, 58, 9999) shouldNotHaveHours (12.toLong())
      }

      "Time should be after" {
         Time(12, 1, 2, 7777) should after(Time(10, 1, 2, 7777))
         Time(7, 1, 2, 7777) shouldNot after(Time(10, 1, 2, 7777))
         Time(12, 1, 2, 7777) shouldBeAfter (Time(10, 1, 2, 7777))
         Time(7, 1, 2, 7777) shouldNotBeAfter (Time(10, 1, 2, 7777))
      }

      "Time should be before" {
         Time(7, 1, 2, 7777) should before(Time(10, 1, 2, 7777))
         Time(12, 1, 2, 7777) shouldNot before(Time(10, 1, 2, 7777))
         Time(7, 1, 2, 7777) shouldBeBefore (Time(10, 1, 2, 7777))
         Time(12, 1, 2, 7777) shouldNotBeBefore (Time(10, 1, 2, 7777))
      }

      "Time is between" {
         Time(11, 1, 2, 7777) should between(Time(10, 1, 2, 7777), Time(12, 1, 2, 7777))
         Time(7, 1, 2, 7777) shouldNot between(Time(10, 1, 2, 7777), Time(12, 1, 2, 7777))
         Time(11, 1, 2, 7777).shouldBeBetween(Time(10, 1, 2, 7777), Time(12, 1, 2, 7777))
         Time(7, 1, 2, 7777).shouldNotBeBetween(Time(10, 1, 2, 7777), Time(12, 1, 2, 7777))
      }
   }
}
