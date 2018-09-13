package com.sksamuel.kotlintest.matchers.date

import io.kotlintest.matchers.date.shouldHaveSameYearAs
import java.time.LocalDate

class DateMatchersSamples {

    fun localDateShouldHaveSameYearAs() {
        val firstDate = LocalDate.of(1998, 2, 9)
        val secondDate = LocalDate.of(1998, 3, 10)
        
        firstDate shouldHaveSameYearAs secondDate   //Assertion passes
    }
    
    fun localDateShouldHaveSameYearAsFailure() {
        val firstDate = LocalDate.of(2018, 2, 9)
        val secondDate = LocalDate.of(1998, 3, 10)
    
        firstDate shouldHaveSameYearAs secondDate   //Assertion fails, 2018 != 1998
    }
    
}