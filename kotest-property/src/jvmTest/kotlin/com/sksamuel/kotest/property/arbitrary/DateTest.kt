package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.date.shouldNotBeAfter
import io.kotest.matchers.date.shouldNotBeBefore
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.ranges.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.javaDate
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.localTime
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.offsetDateTime
import io.kotest.property.arbitrary.period
import io.kotest.property.arbitrary.removeEdgecases
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.year
import io.kotest.property.arbitrary.yearMonth
import io.kotest.property.arbitrary.zonedDateTime
import io.kotest.property.checkAll
import io.kotest.property.forAll
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.MAX
import java.time.LocalDate.MIN
import java.time.LocalDate.of
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.hours

class DateTest : WordSpec({

   "Arb.localDate(minYear, maxYear)" should {
      "generate valid LocalDates (no exceptions)" {
         shouldNotThrowAny {
            Arb.localDate().take(10_000).toList()
         }
      }

      "generate LocalDates between minYear and maxYear" {
         val years = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val days = mutableSetOf<Int>()

         checkAll(10_000, Arb.localDate(of(1998, 1, 1), of(1999, 12, 31))) {
            years += it.year
            months += it.monthValue
            days += it.dayOfMonth
         }

         years shouldBe setOf(1998, 1999)
         months shouldBe (1..12).toSet()
         days shouldBe (1..31).toSet()
      }

      "Generate LocalDates always in the range" {
         repeat(1_000) {
            val min = of(1970, 1, 1).plusDays(it.toLong())
            val max = min.plusYears(20)
            Arb.localDate(min, max).forAll { it >= min && it <= max }
         }
      }

      "Edge cases are within specified date range when minDate is after typical edge case dates" {
         val minDate = of(2024, 3, 1)
         val maxDate = of(2024, 12, 31)
         val arb = Arb.localDate(minDate, maxDate)
         val dates = arb.edgecases()
         dates.forAll { date -> date shouldBeIn minDate..maxDate }
      }

      "Century year edge case is included when within the date range" {
         val minDate = of(1999, 12, 31)
         val maxDate = of(2001, 1, 1)
         val arb = Arb.localDate(minDate, maxDate)
         val dates = arb.edgecases()
         dates.forAll { date -> date shouldBeIn minDate..maxDate }
      }

      "Contain Feb 29th if leap year" {
         val leapYear = 2016
         Arb.localDate(of(leapYear, 1, 1), of(leapYear, 12, 31)).edgecases() shouldContain of(leapYear, 2, 29)
      }

      "Contain Jan 1st if century year" {
         val centuryYear = 2100
         Arb.localDate(of(centuryYear, 1, 1), of(centuryYear, 12, 31)).edgecases() shouldContain of(centuryYear, 1, 1)
      }


      "Be the default generator for LocalDate" {
         checkAll(10) { _: LocalDate -> /* No use. Won't reach here if unsupported */ }
      }
   }

   "Arb.localDate(minDate, maxDate)" should {
      "be able to handle a very large delta between boundaries" {
         shouldNotThrowAny {
            Arb.localDate(MIN, MAX).take(10_000).toList()
         }
      }

      "Work when min date == max date" {
         val date = of(2021, 1, 1)
         Arb.localDate(date, date).take(10).toList() shouldBe List(10) { date }
      }

      "Throw when min date > max date" {
         shouldThrow<IllegalArgumentException> {
            val minDate = of(2021, 1, 1)
            Arb.localDate(minDate, minDate.minusDays(1))
         }.message shouldBe "minDate must be before or equal to maxDate"
      }

      "generate LocalDates from minDate to (including) maxDate" {
         val dates = mutableSetOf<LocalDate>()
         val minDate = of(1998, 12, 30)
         val maxDate = of(1999, 1, 3)

         checkAll(10_000, Arb.localDate(minDate, maxDate).removeEdgecases()) {
            dates += it
         }

         dates shouldBe generateSequence(minDate) { it.plusDays(1) }.takeWhile { it <= maxDate }.toSet()
      }
   }

   "Arb.localTime()" should {
      "generate N valid LocalTimes(no exceptions)" {
         Arb.localTime().generate(RandomSource.default()).take(10_000).toList()
            .size shouldBe 10_000
      }

      "Be the default generator for LocalTime" {
         checkAll(10) { _: LocalTime -> /* No use. Won't reach here if unsupported */ }
      }
   }

   "Arb.localDateTime(minLocalDateTime, maxLocalDateTime)" should {
      "generate N valid LocalDateTimes(no exceptions)" {
         Arb.localDateTime().generate(RandomSource.default()).take(10_000).toList()
            .size shouldBe 10_000
      }

      "generate LocalDateTimes between minLocalDateTime and maxLocalDateTime (same year test)" {
         val years = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val days = mutableSetOf<Int>()
         val hours = mutableSetOf<Int>()
         val minutes = mutableSetOf<Int>()
         val seconds = mutableSetOf<Int>()
         val minLocalDateTime = LocalDateTime.of(1998, 7, 1, 0, 0)
         val maxLocalDateTime = LocalDateTime.of(1998, 12, 31, 23, 59)

         checkAll(5000, Arb.localDateTime(minLocalDateTime, maxLocalDateTime)) {
            years += it.year
            months += it.monthValue
            days += it.dayOfMonth
            hours += it.hour
            minutes += it.minute
            seconds += it.second
         }

         years shouldBe setOf(1998)
         months shouldBe (7..12).toSet()
         days shouldBe (1..31).toSet()
         hours shouldBe (0..23).toSet()
         minutes shouldBe (0..59).toSet()
      }

      "generate LocalDateTimes between minLocalDateTime and maxLocalDateTime (different years)" {
         val years = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val days = mutableSetOf<Int>()
         val hours = mutableSetOf<Int>()
         val minutes = mutableSetOf<Int>()
         val seconds = mutableSetOf<Int>()
         val minLocalDateTime = LocalDateTime.of(1998, 1, 1, 0, 0)
         val maxLocalDateTime = LocalDateTime.of(1999, 12, 31, 23, 59)

         checkAll(5000, Arb.localDateTime(minLocalDateTime, maxLocalDateTime)) {
            years += it.year
            months += it.monthValue
            days += it.dayOfMonth
            hours += it.hour
            minutes += it.minute
            seconds += it.second
         }

         years shouldBe setOf(1998, 1999)
         months shouldBe (1..12).toSet()
         days shouldBe (1..31).toSet()
         hours shouldBe (0..23).toSet()
         minutes shouldBe (0..59).toSet()
      }

      "generate LocalDateTimes between minLocalDateTime and maxLocalDateTime (startTime and endTIme during the day)" {
         val minLocalDateTime = LocalDateTime.of(1998, 1, 1, 12, 0)
         val maxLocalDateTime = LocalDateTime.of(1998, 12, 31, 12, 0)
         val localDateTimes = mutableSetOf<LocalDateTime>()

         checkAll(5000, Arb.localDateTime(minLocalDateTime, maxLocalDateTime)) {
            localDateTimes += it
         }

         localDateTimes.forAll {
            it shouldNotBeBefore minLocalDateTime
            it shouldNotBeAfter maxLocalDateTime
         }
      }

      "Be the default generator for LocalDateTime" {
         checkAll(10) { _: LocalDateTime -> /* No use. Won't reach here if unsupported */ }
      }
   }

   "Arb.localDateTime(minYear, maxYear)" should {
      "generate LocalDateTimes between minYear and maxYear" {
         val years = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val days = mutableSetOf<Int>()
         val hours = mutableSetOf<Int>()
         val minutes = mutableSetOf<Int>()
         val seconds = mutableSetOf<Int>()

         checkAll(5000, Arb.localDateTime(1998, 1999)) {
            years += it.year
            months += it.monthValue
            days += it.dayOfMonth
            hours += it.hour
            minutes += it.minute
            seconds += it.second
         }

         years shouldBe setOf(1998, 1999)
         months shouldBe (1..12).toSet()
         days shouldBe (1..31).toSet()
         hours shouldBe (0..23).toSet()
         minutes shouldBe (0..59).toSet()
      }
   }

   "Gen.period(maxYears)" should {
      "Generate only periods with years <= maxYears" {
         checkAll(10_000, Arb.period(2)) {
            it.years <= 2
         }
      }

      "Generate all possible years in the interval [0, maxYears]" {
         val generated = mutableSetOf<Int>()
         checkAll(10_000, Arb.period(10)) {
            generated += it.years
         }

         generated shouldBe (0..10).toSet()
      }

      "Generate all possible intervals for Months and Days" {
         val generatedDays = mutableSetOf<Int>()
         val generatedMonths = mutableSetOf<Int>()

         checkAll(10_000, Arb.period(10)) {
            generatedDays += it.days
            generatedMonths += it.months
         }

         generatedDays shouldBe (0..31).toSet()
         generatedMonths shouldBe (0..11).toSet()
      }

      "Be the default generator for Duration" {
         checkAll(10) { _: Period -> /* No use. Won't reach here if unsupported */ }
      }
   }

   "Arb.year(minYear, maxYear)" should {
      "generate valid years (no exceptions)" {
         shouldNotThrowAny {
            Arb.year().take(10_000).toList()
         }
      }

      "generate years between minYear and maxYear" {
         val years = mutableSetOf<Year>()

         checkAll(10_000, Arb.year(Year.of(1998), Year.of(1999))) {
            years += it
         }

         years shouldBe setOf(Year.of(1998), Year.of(1999))
      }
   }

   "Arb.yearMonth(minYearMonth, maxYearMonth)" should {
      "generate valid YearMonths (no exceptions)" {
         shouldNotThrowAny {
            Arb.yearMonth().take(10_000).toList()
         }
      }

      "generate YearMonths between minYearMonth and maxYearMonth" {
         val years = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()

         checkAll(10_000, Arb.yearMonth(YearMonth.of(1998, 2), YearMonth.of(1998, 8))) {
            years += it.year
            months += it.monthValue
         }

         years shouldBe setOf(1998)
         months shouldBe (2..8).toSet()
      }

      "Contain Feb if leap year" {
         val leapYear = 2016
         Arb.yearMonth(YearMonth.of(leapYear, 1), YearMonth.of(leapYear, 12)).edgecases() shouldContain YearMonth.of(
            2016,
            2
         )
      }
   }

   "Arb.offsetDateTime(minLocalDateTime, maxLocalDateTime, zoneOffset)" should {
      "generate OffsetDateTimes between with default values" {
         val days = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val offsets = mutableSetOf<Int>()

         checkAll(5000, Arb.offsetDateTime()) {
            days.add(it.dayOfMonth)
            months.add(it.monthValue)
            offsets.add(it.offset.totalSeconds)
         }

         days.sorted() shouldBe (1..31).toSet()
         months.sorted() shouldBe (1..12).toSet()
         offsets.min() shouldBeGreaterThanOrEqual -18.hours.inWholeSeconds.toInt()
         offsets.max() shouldBeLessThanOrEqual 18.hours.inWholeSeconds.toInt()
      }
   }

   "Arb.offsetDateTime(minInstant, maxInstant, zoneOffset)" should {
      "generate OffsetDateTimes between 1970 and 2030 year" {
         val days = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val offsets = mutableSetOf<Int>()

         checkAll(
            5000, Arb.offsetDateTime(
               LocalDateTime.of(1970, 1, 1, 0, 0).toInstant(ZoneOffset.UTC),
               LocalDateTime.of(2030, 12, 31, 23, 59).toInstant(ZoneOffset.UTC)
            )
         ) {
            days.add(it.dayOfMonth)
            months.add(it.monthValue)
            offsets.add(it.offset.totalSeconds)
         }

         days.sorted() shouldBe (1..31).toSet()
         months.sorted() shouldBe (1..12).toSet()
         offsets.min() shouldBeGreaterThanOrEqual -18.hours.inWholeSeconds.toInt()
         offsets.max() shouldBeLessThanOrEqual 18.hours.inWholeSeconds.toInt()
      }
   }

   "Arb.zonedDateTime(minLocalDateTime, maxLocalDateTime, zoneId)" should {
      "generate ZonedDateTime between with default values" {
         val days = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()

         checkAll(5000, Arb.zonedDateTime()) {
            days.add(it.dayOfMonth)
            months.add(it.monthValue)
         }

         days.sorted() shouldBe (1..31).toSet()
         months.sorted() shouldBe (1..12).toSet()
      }
   }

   "Arb.javaDate(minDate: String, maxDate: String, zoneId)" should {
      "generate Dates between with default values" {
         val days = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val years = mutableSetOf<Int>()

         val zoneId = ZoneId.systemDefault()
         val testedArb = Arb.javaDate(zoneId = Arb.of(zoneId))

         checkAll(5000, testedArb) { date ->
            val localDate = ZonedDateTime.ofInstant(date.toInstant(), zoneId)
            days.add(localDate.dayOfMonth)
            months.add(localDate.monthValue)
            years.add(localDate.year)
         }

         days.sorted() shouldBe (1..31).toSet()
         months.sorted() shouldBe (1..12).toSet()
         years.sorted() shouldBe (1970..2050).toSet()
      }
   }

   "Arb.javaDate(minDate: Date, maxDate: Date, zoneId)" should {
      "generate Dates between with default values" {
         val days = mutableSetOf<Int>()
         val months = mutableSetOf<Int>()
         val years = mutableSetOf<Int>()

         val zoneId = ZoneId.systemDefault()
         val testedArb = Arb.javaDate(
            minDate = SimpleDateFormat("yyyy-mm-dd").parse("1970-01-01"),
            maxDate = SimpleDateFormat("yyyy-mm-dd").parse("2050-12-31"),
            zoneId = Arb.of(zoneId)
         )

         checkAll(5000, testedArb) { date ->
            val localDate = ZonedDateTime.ofInstant(date.toInstant(), zoneId)
            days.add(localDate.dayOfMonth)
            months.add(localDate.monthValue)
            years.add(localDate.year)
         }

         days.sorted() shouldBe (1..31).toSet()
         months.sorted() shouldBe (1..12).toSet()
         years.sorted() shouldBe (1970..2050).toSet()
      }
   }
})
