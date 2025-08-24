package com.sksamuel.kotest.engine.test

import io.kotest.assertions.withClue
import io.kotest.common.testTimeSource
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.config.DefaultTestConfig
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark

class RetryTests : FunSpec() {
   init {
      coroutineTestScope = true

      fun checkResults(collector: CollectingTestEngineListener, testCount: Int) {
         collector.names.size shouldBe testCount

         collector.names.forEach { name ->
            val result = collector.result(name)
            withClue(name) {
               result.shouldNotBeNull()
               withClue(result) {
                  result.isSuccess shouldBe true
               }
            }
         }
      }

      test("regular") {
         val collector = CollectingTestEngineListener()

         TestEngineLauncher().withListener(collector)
            .withClasses(InnerRetryTest::class)
            .async()

         checkResults(collector, 4)
      }

      test("with spec default") {
         val collector = CollectingTestEngineListener()

         TestEngineLauncher().withListener(collector)
            .withClasses(InnerRetryWithSpecDefaultTest::class)
            .async()

         checkResults(collector, 1)
      }
   }
}

private class InnerRetryTest : FunSpec() {
   private lateinit var mark: TimeMark

   init {

      retries = 3
      retryDelay = 25.milliseconds

      var count = 0

      beforeTest {
         mark = testTimeSource().markNow()
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
            // 2 retries in total, with delays of 20 each
            mark.elapsedNow().shouldBe(40.milliseconds)
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
            // 3 retries in total, with delays of 25 each
            mark.elapsedNow().shouldBe(75.milliseconds)
         }
      }
   }
}

private class InnerRetryWithSpecDefaultTest : DescribeSpec() {
   private lateinit var mark: TimeMark

   init {
      var count = 0

      beforeTest {
         mark = testTimeSource().markNow()
      }

      afterTest {
         count = 0
      }

      defaultTestConfig = DefaultTestConfig(retries = 2, retryDelay = 20.milliseconds)

      describe("tests should use default test config when test/spec fields are not specified") {
         if (count < 2) {
            count++
            error("boom")
         } else {
            // 3 invocations in total, with delays of 20 each
            mark.elapsedNow().shouldBe(40.milliseconds)
         }
      }
   }
}
