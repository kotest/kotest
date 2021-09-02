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
import kotlin.time.Duration

class DurationTest : FunSpec({
   test("should have minutes") {
      Duration.hours(2).shouldHaveMinutes(120)
      Duration.hours(2).shouldNotHaveMinutes(119)
      shouldThrow<AssertionError> {
         Duration.hours(2).shouldNotHaveMinutes(120)
      }.shouldHaveMessage("2h should not have 120 minutes")
   }
   test("should have hours") {
       Duration.minutes(180).shouldHaveHours(3)
       Duration.minutes(180).shouldNotHaveHours(2)
       shouldThrow<AssertionError> {
           Duration.minutes(180).shouldNotHaveHours(3)
       }.shouldHaveMessage("3h should not have 3 hours")
   }
   test("should have seconds") {
       Duration.minutes(1).shouldHaveSeconds(60)
       Duration.minutes(1).shouldNotHaveSeconds(59)
       shouldThrow<AssertionError> {
           Duration.minutes(1).shouldNotHaveSeconds(60)
       }.shouldHaveMessage("1m should not have 60 seconds")
   }
   test("should have millis") {
      Duration.seconds(3).shouldHaveMillis(3000)
      Duration.seconds(3).shouldNotHaveMillis(2999)
      shouldThrow<AssertionError> {
         Duration.seconds(3).shouldNotHaveMillis(3000)
      }.shouldHaveMessage("3s should not have 3000 millis")
   }
})
