package com.sksamuel.kotlintest.matchers.date

import io.kotlintest.matchers.date.haveSameDay
import io.kotlintest.matchers.date.haveSameMonth
import io.kotlintest.matchers.date.haveSameYear
import io.kotlintest.matchers.date.shouldHaveSameDayAs
import io.kotlintest.matchers.date.shouldHaveSameMonthAs
import io.kotlintest.matchers.date.shouldHaveSameYearAs
import io.kotlintest.matchers.date.shouldNotHaveSameDayAs
import io.kotlintest.matchers.date.shouldNotHaveSameMonthAs
import io.kotlintest.matchers.date.shouldNotHaveSameYearAs
import io.kotlintest.should
import io.kotlintest.shouldNot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class DateMatchersSamples {

    fun localDateShouldHaveSameYearAs() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 3, 10)
        
        firstDate shouldHaveSameYearAs secondDate   //Assertion passes
    }
    
    fun localDateShouldHaveSameYearAsFailure() {
        val firstDate = LocalDate.of(2018, 2, 9)
        val secondDate = LocalDate.of(1998, 2, 9)
    
        firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
    }
    
    fun localDateShouldNotHaveSameYearAs() {
        val firstDate = LocalDate.of(2018, 2, 9)
        val secondDate = LocalDate.of(1998, 2, 9)
        
        firstDate shouldNotHaveSameYearAs secondDate    //Assertion passes
    }
    
    fun localDateShouldNotHaveSameYearAsFailure() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 3, 10)
    
        firstDate shouldNotHaveSameYearAs  secondDate   //Assertion fails, 1998 == 1998, and we expected a difference
    }
    
    fun localDateHaveSameYear() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 3, 10)
        
        firstDate should haveSameYear(secondDate)   //Assertion passes
    }
    
    fun localDateHaveSameYearNegation() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(2018, 2, 9)
        
        firstDate shouldNot haveSameYear(secondDate)    //Assertion passes
    }
    
    
    
    
    fun localDateTimeShouldHaveSameYearAs() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 10, 11, 30, 30)
        
        firstDate shouldHaveSameYearAs secondDate   //Assertion passes
    }
    
    fun localDateTimeShouldHaveSameYearAsFailure() {
        val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        
        firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
    }
    
    fun localDateTimeShouldNotHaveSameYearAs() {
        val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 10, 11, 30, 30)
        
        firstDate shouldNotHaveSameYearAs secondDate    //Assertion passes
    }
    
    fun localDateTimeShouldNotHaveSameYearAsFailure() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 10, 1, 30, 30)
        
        firstDate shouldNotHaveSameYearAs  secondDate   //Assertion fails, 1998 == 1998, and we expected a difference
    }
    
    fun localDateTimeHaveSameYear() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 10, 11, 30, 30)
        
        firstDate should haveSameYear(secondDate)   //Assertion passes
    }
    
    fun localDateTimeHaveSameYearNegation() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
        
        firstDate shouldNot haveSameYear(secondDate)    //Assertion passes
    }
    
    
    
    
    fun zonedDateTimeShouldHaveSameYearAs() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 3, 10, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate shouldHaveSameYearAs secondDate   //Assertion passes
    }
    
    fun zonedDateTimeShouldHaveSameYearAsFailure() {
        val firstDate = ZonedDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
    }
    
    fun zonedDateTimeShouldNotHaveSameYearAs() {
        val firstDate = ZonedDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 2, 9, 19, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldNotHaveSameYearAs secondDate    //Assertion passes
    }
    
    fun zonedDateTimeShouldNotHaveSameYearAsFailure() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 3, 10, 1, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate shouldNotHaveSameYearAs  secondDate   //Assertion fails, 1998 == 1998, and we expected a difference
    }
    
    fun zonedDateTimeHaveSameYear() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 3, 10, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate should haveSameYear(secondDate)   //Assertion passes
    }
    
    fun zonedDateTimeHaveSameYearNegation() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldNot haveSameYear(secondDate)    //Assertion passes
    }
    
    
    
    
    fun offsetDateTimeShouldHaveSameYearAs() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 3, 10, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate shouldHaveSameYearAs secondDate   //Assertion passes
    }
    
    fun offsetDateTimeShouldHaveSameYearAsFailure() {
        val firstDate = OffsetDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
    }
    
    fun offsetDateTimeShouldNotHaveSameYearAs() {
        val firstDate = OffsetDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 2, 9, 19, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldNotHaveSameYearAs secondDate    //Assertion passes
    }
    
    fun offsetDateTimeShouldNotHaveSameYearAsFailure() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 3, 10, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate shouldNotHaveSameYearAs  secondDate   //Assertion fails, 1998 == 1998, and we expected a difference
    }
    
    fun offsetDateTimeHaveSameYear() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 3, 10, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate should haveSameYear(secondDate)   //Assertion passes
    }
    
    fun offsetDateTimeHaveSameYearNegation() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(2018, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldNot haveSameYear(secondDate)    //Assertion passes
    }
    
    
    
    
    
    fun localDateShouldHaveSameMonthAs() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(2018, 2, 10)
        
        firstDate shouldHaveSameMonthAs secondDate   //Assertion passes
    }
    
    fun localDateShouldHaveSameMonthAsFailure() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 3, 9)
        
        firstDate shouldHaveSameMonthAs secondDate   //Assertion fails, 2 != 3
    }
    
    fun localDateShouldNotHaveSameMonthAs() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 3, 9)
        
        firstDate shouldNotHaveSameMonthAs secondDate    //Assertion passes
    }
    
    fun localDateShouldNotHaveSameMonthAsFailure() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(2018, 2, 10)
        
        firstDate shouldNotHaveSameMonthAs  secondDate   //Assertion fails, 2 == 2, and we expected a difference
    }
    
    fun localDateHaveSameMonth() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(2018, 2, 10)
        
        firstDate should haveSameMonth(secondDate)   //Assertion passes
    }
    
    fun localDateHaveSameMonthNegation() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 3, 9)
        
        firstDate shouldNot haveSameMonth(secondDate)    //Assertion passes
    }
    
    
    
    
    
    fun localDateTimeShouldHaveSameMonthAs() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(2018, 2, 10, 11, 30, 30)
        
        firstDate shouldHaveSameMonthAs secondDate   //Assertion passes
    }
    
    fun localDateTimeShouldHaveSameMonthAsFailure() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 9, 10, 0, 0)
        
        firstDate shouldHaveSameMonthAs secondDate   //Assertion fails, 2 != 3
    }
    
    fun localDateTimeShouldNotHaveSameMonthAs() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 10, 11, 30, 30)
        
        firstDate shouldNotHaveSameMonthAs secondDate    //Assertion passes
    }
    
    fun localDateTimeShouldNotHaveSameMonthAsFailure() {
        val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 2, 10, 1, 30, 30)
        
        firstDate shouldNotHaveSameMonthAs  secondDate   //Assertion fails, 2 == 2, and we expected a difference
    }
    
    fun localDateTimeHaveSameMonth() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(2018, 2, 10, 11, 30, 30)
        
        firstDate should haveSameMonth(secondDate)   //Assertion passes
    }
    
    fun localDateTimeHaveSameMonthNegation() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 9, 10, 0, 0)
        
        firstDate shouldNot haveSameMonth(secondDate)    //Assertion passes
    }
    
    
    
    
    
    fun zonedDateTimeShouldHaveSameMonthAs() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(2018, 2, 10, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate shouldHaveSameMonthAs secondDate   //Assertion passes
    }
    
    fun zonedDateTimeShouldHaveSameMonthAsFailure() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldHaveSameMonthAs secondDate   //Assertion fails, 2 != 3
    }
    
    fun zonedDateTimeShouldNotHaveSameMonthAs() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 3, 9, 19, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldNotHaveSameMonthAs secondDate    //Assertion passes
    }
    
    fun zonedDateTimeShouldNotHaveSameMonthAsFailure() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(2018, 2, 10, 1, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate shouldNotHaveSameMonthAs  secondDate   //Assertion fails, 2 == 2, and we expected a difference
    }
    
    fun zonedDateTimeHaveSameMonth() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(2018, 2, 10, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate should haveSameMonth(secondDate)   //Assertion passes
    }
    
    fun zonedDateTimeHaveSameMonthNegation() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldNot haveSameMonth(secondDate)    //Assertion passes
    }
    
    
    
    
    
    
    
    fun offsetDateTimeShouldHaveSameMonthAs() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(2018, 2, 10, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate shouldHaveSameMonthAs secondDate   //Assertion passes
    }
    
    fun offsetDateTimeShouldHaveSameMonthAsFailure() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldHaveSameMonthAs secondDate   //Assertion fails, 2 != 3
    }
    
    fun offsetDateTimeShouldNotHaveSameMonthAs() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 3, 9, 19, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldNotHaveSameMonthAs secondDate    //Assertion passes
    }
    
    fun offsetDateTimeShouldNotHaveSameMonthAsFailure() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(2018, 2, 10, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate shouldNotHaveSameMonthAs  secondDate   //Assertion fails, 2 == 2, and we expected a difference
    }
    
    fun offsetDateTimeHaveSameMonth() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(2018, 2, 10, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate should haveSameMonth(secondDate)   //Assertion passes
    }
    
    fun offsetDateTimeHaveSameMonthNegation() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 3, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldNot haveSameMonth(secondDate)    //Assertion passes
    }
    
    
    
    
    
    
    fun localDateShouldHaveSameDayAs() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(2018, 3, 9)
        
        firstDate shouldHaveSameDayAs secondDate   //Assertion passes
    }
    
    fun localDateShouldHaveSameDayAsFailure() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 2, 10)
        
        firstDate shouldHaveSameDayAs secondDate   //Assertion fails, 9 != 10
    }
    
    fun localDateShouldNotHaveSameDayAs() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 2, 10)
        
        firstDate shouldNotHaveSameDayAs secondDate    //Assertion passes
    }
    
    fun localDateShouldNotHaveSameDayAsFailure() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(2018, 3, 9)
        
        firstDate shouldNotHaveSameDayAs  secondDate   //Assertion fails, 9 == 9, and we expected a difference
    }
    
    fun localDateHaveSameDay() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(2018, 3, 9)
        
        firstDate should haveSameDay(secondDate)   //Assertion passes
    }
    
    fun localDateHaveSameDayNegation() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 2, 10)
        
        firstDate shouldNot haveSameDay(secondDate)    //Assertion passes
    }
    
    
    
    
    fun localDateTimeShouldHaveSameDayAs() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 9, 11, 30, 30)
        
        firstDate shouldHaveSameDayAs secondDate   //Assertion passes
    }
    
    fun localDateTimeShouldHaveSameDayAsFailure() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
        
        firstDate shouldHaveSameDayAs secondDate   //Assertion fails, 9 != 10
    }
    
    fun localDateTimeShouldNotHaveSameDayAs() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
        
        firstDate shouldNotHaveSameDayAs secondDate    //Assertion passes
    }
    
    fun localDateTimeShouldNotHaveSameDayAsFailure() {
        val firstDate = LocalDateTime.of(2018, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 3, 9, 11, 30, 30)
        
        firstDate shouldNotHaveSameDayAs  secondDate   //Assertion fails, 9 == 9, and we expected a difference
    }
    
    fun localDateTimeHaveSameDay() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(2018, 3, 9, 11, 30, 30)
        
        firstDate should haveSameDay(secondDate)   //Assertion passes
    }
    
    fun localDateTimeHaveSameDayNegation() {
        val firstDate = LocalDateTime.of(1998, 2, 9, 10, 0, 0)
        val secondDate = LocalDateTime.of(1998, 2, 10, 10, 0, 0)
        
        firstDate shouldNot haveSameDay(secondDate)    //Assertion passes
    }
    
    
    
    
    
    
    fun zonedDateTimeShouldHaveSameDayAs() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate shouldHaveSameDayAs secondDate   //Assertion passes
    }
    
    fun zonedDateTimeShouldHaveSameDayAsFailure() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldHaveSameDayAs secondDate   //Assertion fails, 9 != 10
    }
    
    fun zonedDateTimeShouldNotHaveSameDayAs() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldNotHaveSameDayAs secondDate    //Assertion passes
    }
    
    fun zonedDateTimeShouldNotHaveSameDayAsFailure() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate shouldNotHaveSameDayAs  secondDate   //Assertion fails, 9 == 9, and we expected a difference
    }
    
    fun zonedDateTimeHaveSameDay() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneId.of("America/Chicago"))
        
        firstDate should haveSameDay(secondDate)   //Assertion passes
    }
    
    fun zonedDateTimeHaveSameDayNegation() {
        val firstDate = ZonedDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        val secondDate = ZonedDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneId.of("America/Sao_Paulo"))
        
        firstDate shouldNot haveSameDay(secondDate)    //Assertion passes
    }
    
    
    
    
    fun offsetDateTimeShouldHaveSameDayAs() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate shouldHaveSameDayAs secondDate   //Assertion passes
    }
    
    fun offsetDateTimeShouldHaveSameDayAsFailure() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldHaveSameDayAs secondDate   //Assertion fails, 9 != 12
    }
    
    fun offsetDateTimeShouldNotHaveSameDayAs() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 2, 10, 19, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldNotHaveSameDayAs secondDate    //Assertion passes
    }
    
    fun offsetDateTimeShouldNotHaveSameDayAsFailure() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(2018, 3, 9, 1, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate shouldNotHaveSameDayAs  secondDate   //Assertion fails, 9 == 9, and we expected a difference
    }
    
    fun offsetDateTimeHaveSameDay() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(2018, 3, 9, 11, 30, 30, 30, ZoneOffset.ofHours(-5))
        
        firstDate should haveSameDay(secondDate)   //Assertion passes
    }
    
    fun offsetDateTimeHaveSameDayNegation() {
        val firstDate = OffsetDateTime.of(1998, 2, 9, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        val secondDate = OffsetDateTime.of(1998, 2, 10, 10, 0, 0, 0, ZoneOffset.ofHours(-3))
        
        firstDate shouldNot haveSameDay(secondDate)    //Assertion passes
    }
}