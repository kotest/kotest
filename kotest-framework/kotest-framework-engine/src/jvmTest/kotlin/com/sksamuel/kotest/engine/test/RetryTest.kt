package com.sksamuel.kotest.engine.test

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.config.DefaultTestConfig
import io.kotest.matchers.comparables.shouldBeGreaterThan
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

class RetryTest : FunSpec() {
   init {

      retries = 3
      retryDelay = 25.milliseconds

      val timesource = TimeSource.Monotonic
      var count = 0
      var mark = timesource.markNow()

      beforeTest {
         mark = timesource.markNow()
      }

      afterTest {
         count = 0
      }

      test("basic retry").config(retries = 2) {
         if (count < 2) {
            count++
            error("boom")
         }
      }

      test("with delay").config(retries = 2, retryDelay = 20.milliseconds) {
         if (count < 2) {
            count++
            error("boom")
         } else {
            // 3 invocations in total, with delays of 25 each, so should be at least 50ms
            mark.elapsedNow().shouldBeGreaterThan(40.milliseconds)
         }
      }

      test("defaulting to spec field level retries") {
         if (count < 3) {
            count++
            error("boom")
         }
      }

      test("defaulting to spec field level retry delay") {
         if (count < 3) {
            count++
            error("boom")
         } else {
            // 4 invocations in total, with delays of 25 each, so should be at least 50ms
            mark.elapsedNow().shouldBeGreaterThan(75.milliseconds)
         }
      }
   }
}

class RetryWithSpecDefaultTest : DescribeSpec() {
   init {

      val timesource = TimeSource.Monotonic
      var count = 0
      var mark = timesource.markNow()

      beforeTest {
         mark = timesource.markNow()
      }

      afterTest {
         count = 0
      }

      defaultTestConfig = DefaultTestConfig(retryFn = { 2 }, retryDelayFn = { _, _ -> 20.milliseconds })

      describe("tests should use default test config when test/spec fields are not specified") {
         if (count < 2) {
            count++
            error("boom")
         } else {
            // 3 invocations in total, with delays of 20 each, so should be at least 40
            mark.elapsedNow().shouldBeGreaterThan(40.milliseconds)
         }
      }
   }
}
