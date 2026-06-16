package com.sksamuel.kotest.matchers.time

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.time.shouldHaveHours
import io.kotest.matchers.time.shouldHaveMillis
import io.kotest.matchers.time.shouldHaveMinutes
import io.kotest.matchers.time.shouldHaveSeconds
import io.kotest.matchers.time.shouldNotHaveHours
import io.kotest.matchers.time.shouldNotHaveMillis
import io.kotest.matchers.time.shouldNotHaveMinutes
import io.kotest.matchers.time.shouldNotHaveSeconds
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DurationTest : FunSpec({

   test("should have minutes") {
      2.hours.shouldHaveMinutes(120)
      2.hours.shouldNotHaveMinutes(119)
      shouldThrow<AssertionError> {
         2.hours.shouldNotHaveMinutes(120)
      }.shouldHaveMessage("2h should not have 120 minutes")
   }

   test("should have hours") {
       180.minutes.shouldHaveHours(3)
       180.minutes.shouldNotHaveHours(2)
       shouldThrow<AssertionError> {
           180.minutes.shouldNotHaveHours(3)
       }.shouldHaveMessage("3h should not have 3 hours")
   }

   test("should have seconds") {
       1.minutes.shouldHaveSeconds(60)
       1.minutes.shouldNotHaveSeconds(59)
       shouldThrow<AssertionError> {
           1.minutes.shouldNotHaveSeconds(60)
       }.shouldHaveMessage("1m should not have 60 seconds")
   }

   test("should have millis") {
      3.seconds.shouldHaveMillis(3000)
      3.seconds.shouldNotHaveMillis(2999)
      shouldThrow<AssertionError> {
         3.seconds.shouldNotHaveMillis(3000)
      }.shouldHaveMessage("3s should not have 3000 millis")
   }
})
