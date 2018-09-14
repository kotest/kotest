package com.sksamuel.kotlintest.matchers.date

import io.kotlintest.matchers.date.haveSameMonth
import io.kotlintest.matchers.date.haveSameYear
import io.kotlintest.matchers.date.shouldHaveSameMonthAs
import io.kotlintest.matchers.date.shouldHaveSameYearAs
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
    
    
    
}