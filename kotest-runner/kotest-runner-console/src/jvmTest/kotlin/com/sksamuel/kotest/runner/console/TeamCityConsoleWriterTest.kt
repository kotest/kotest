package com.sksamuel.kotest.runner.console

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.shouldFail
import io.kotest.assertions.show.Printed
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.description
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.extensions.system.captureStandardErr
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLineCount
import io.kotest.matchers.string.shouldStartWith
import io.kotest.runner.console.TeamCityConsoleWriter
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
class TeamCityConsoleWriterTest : FunSpec() {

   private val kclass: KClass<out Spec> = TeamCityConsoleWriterTest::class

   private val testCaseContainer = TestCase(
      kclass.description().append("my context").append("my test container"),
      this@TeamCityConsoleWriterTest,
      { },
      sourceRef(),
      TestType.Container,
      TestCaseConfig(),
      null,
      null
   )

   private val testCaseTest = testCaseContainer.copy(
      description = testCaseContainer.description.append("my test case"),
      type = TestType.Test
   )

   init {

      test("before spec class should write testSuiteStarted started") {
         captureStandardOut {
            TeamCityConsoleWriter("testcity").specStarted(kclass)
         } shouldBe "\ntestcity[testSuiteStarted name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest' locationHint='kotest://com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest:1']\n"
      }

      test("before test should write testSuiteStarted for TestType.Container") {
         captureStandardOut {
            TeamCityConsoleWriter("testcity").testStarted(testCaseContainer)
         } shouldBe "\ntestcity[testSuiteStarted name='my test container' locationHint='kotest://com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest:38']\n"
      }

      test("before test should write testStarted for TestType.Test") {
         captureStandardOut {
            TeamCityConsoleWriter("testcity").testStarted(testCaseTest)
         } shouldBe "\ntestcity[testStarted name='my test case' locationHint='kotest://com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest:38']\n"
      }

      test("after spec class should write testSuiteFinished") {
         captureStandardOut {
            TeamCityConsoleWriter("testcity").specFinished(kclass, null, emptyMap())
         } shouldBe "\ntestcity[testSuiteFinished name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest']\n"
      }

      test("afterSpecClass should insert dummy test and write testSuiteFinished for spec error") {
         val err = captureStandardErr {
            captureStandardOut {
               TeamCityConsoleWriter("testcity").specFinished(kclass, AssertionError("boom"), emptyMap())
            } shouldBe "\n" +
               "testcity[testStarted name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest <init>']\n" +
               "testcity[testFailed name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest <init>' message='boom']\n" +
               "testcity[testSuiteFinished name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest']\n"
         }
         err shouldStartWith "\njava.lang.AssertionError: boom\n" +
            "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest"
      }

      test("after test should write testSuiteFinished for container success") {
         captureStandardOut {
            TeamCityConsoleWriter("testcity").testFinished(testCaseContainer, TestResult.success(15.milliseconds))
         } shouldBe "\ntestcity[testSuiteFinished name='my test container' duration='15']\n"
      }

      test("after test should insert dummy test and write testSuiteFinished for container error") {
         captureStandardErr {
            captureStandardOut {
               TeamCityConsoleWriter("testcity").testFinished(
                  testCaseContainer,
                  TestResult.throwable(AssertionError("wibble"), 51.milliseconds)
               )
            } shouldBe "\n" +
               "testcity[testStarted name='my test container <init>']\n" +
               "testcity[testFailed name='my test container <init>' message='wibble']\n" +
               "testcity[testSuiteFinished name='my test container' duration='51']\n"
         } shouldStartWith "\njava.lang.AssertionError: wibble\n" +
            "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest"
      }

      test("after test should write testSuiteFinished for container ignored") {
         captureStandardOut {
            TeamCityConsoleWriter("testcity").testFinished(testCaseContainer, TestResult.ignored("ignore me?"))
         } shouldBe "\ntestcity[testSuiteFinished name='my test container']\n"
      }

      test("after test should write testFinished for test success") {
         captureStandardOut {
            TeamCityConsoleWriter("testcity").testFinished(testCaseTest, TestResult.success(234.milliseconds))
         } shouldBe "\ntestcity[testFinished name='my test case' duration='234']\n"
      }

      test("after test should write testIgnored for test with ignored") {
         captureStandardOut {
            TeamCityConsoleWriter("testcity").testFinished(testCaseTest, TestResult.ignored("ignore me?"))
         } shouldBe "\ntestcity[testIgnored name='my test case' ignoreComment='ignore me?']\n"
      }

      test("after test with error should handle multiline messages") {

         val error = shouldFail {
            forAll(
               row(2, 3, 1),
               row(0, 2, 0)
            ) { a, b, max ->
               kotlin.math.max(a, b) shouldBe max
            }
         }

         val out = captureStandardOut {
            TeamCityConsoleWriter("testcity").testFinished(testCaseTest, TestResult.throwable(error, 8123.milliseconds))
         }
         out.trim().shouldHaveLineCount(1)
         out.shouldStartWith("\ntestcity[testFailed name='my test case' message='Test failed' details='")
         out.shouldEndWith("duration='8123']\n")
      }

      test("should use comparison values when a supported exception type") {
         val out = captureStandardOut {
            TeamCityConsoleWriter("testcity").testFinished(
               testCaseTest,
               TestResult.throwable(failure(Expected(Printed("expected")), Actual(Printed("actual"))), 14.milliseconds)
            )
         }
         out.shouldStartWith("\ntestcity[testFailed name='my test case' message='expected:<expected> but was:<actual>' details='")
         out.shouldEndWith("type='comparisonFailure' actual='actual' expected='expected' duration='14']\n")
         out.trim().shouldHaveLineCount(1)
      }
   }
}
