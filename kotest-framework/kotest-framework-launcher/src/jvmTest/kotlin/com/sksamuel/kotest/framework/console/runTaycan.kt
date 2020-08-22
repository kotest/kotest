package com.sksamuel.kotest.framework.console

import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.core.test.TestResult
import io.kotest.framework.console.TaycanReporter
import kotlin.random.Random

fun main() {
   val specs = listOf(
      BasicConsoleWriterTest::class,
      TeamCityConsoleWriterTest::class,
      TeamCityMessagesTest::class
   )
   val reporter = TaycanReporter(term)
   reporter.engineStarted(specs)
   specs.forEach {
      reporter.specStarted(it)
      val tests = BasicConsoleWriterTest().materializeAndOrderRootTests()
      tests.forEach {
         reporter.testStarted(it.testCase)
         when (Random.nextBoolean()) {
            true -> reporter.testFinished(it.testCase, TestResult.success(43))
            false -> reporter.testFinished(it.testCase, TestResult.failure(AssertionError("a should be b"), 21))
         }
      }
      reporter.specFinished(it, null, emptyMap())
   }
   reporter.engineFinished(emptyList())
}
