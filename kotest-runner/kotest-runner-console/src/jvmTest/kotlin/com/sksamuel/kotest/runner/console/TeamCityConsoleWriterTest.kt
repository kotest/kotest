package com.sksamuel.kotest.runner.console

import io.kotest.assertions.shouldFail
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
import io.kotest.matchers.string.shouldStartWith
import io.kotest.runner.console.TeamCityConsoleWriter
import kotlin.reflect.KClass
import kotlin.time.Duration
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
            TeamCityConsoleWriter().specStarted(kclass)
         } shouldBe "\n##teamcity[testSuiteStarted name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest']\n"
      }

      test("before test should write testSuiteStarted for TestType.Container") {
         captureStandardOut {
            TeamCityConsoleWriter().testStarted(testCaseContainer)
         } shouldBe "\n##teamcity[testSuiteStarted name='my test container' locationHint='kotest://com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest:33']\n"
      }

      test("before test should write testStarted for TestType.Test") {
         captureStandardOut {
            TeamCityConsoleWriter().testStarted(testCaseTest)
         } shouldBe "\n##teamcity[testStarted name='my test case' locationHint='kotest://com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest:33']\n"
      }

      test("after spec class should write testSuiteFinished") {
         captureStandardOut {
            TeamCityConsoleWriter().specFinished(kclass, null, emptyMap())
         } shouldBe "\n##teamcity[testSuiteFinished name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest']\n"
      }

      test("afterSpecClass should insert dummy test and write testSuiteFinished for spec error") {
         val err = captureStandardErr {
            captureStandardOut {
               TeamCityConsoleWriter().specFinished(kclass, AssertionError("boom"), emptyMap())
            } shouldBe "\n" +
               "##teamcity[testStarted name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest <init>']\n" +
               "##teamcity[testFailed name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest <init>' message='boom']\n" +
               "##teamcity[testSuiteFinished name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest']\n"
         }
         err shouldStartWith "\njava.lang.AssertionError: boom\n" +
            "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest"
      }

      test("after test should write testSuiteFinished for container success") {
         captureStandardOut {
            TeamCityConsoleWriter().testFinished(testCaseContainer, TestResult.success(15.milliseconds))
         } shouldBe "\n##teamcity[testSuiteFinished name='my test container' duration='15']\n"
      }

      test("after test should insert dummy test and write testSuiteFinished for container error") {
         captureStandardErr {
            captureStandardOut {
               TeamCityConsoleWriter().testFinished(
                  testCaseContainer,
                  TestResult.throwable(AssertionError("wibble"), 51.milliseconds)
               )
            } shouldBe "\n" +
               "##teamcity[testStarted name='my test container <init>']\n" +
               "##teamcity[testFailed name='my test container <init>' message='wibble']\n" +
               "##teamcity[testSuiteFinished name='my test container' duration='51']\n"
         } shouldStartWith "\njava.lang.AssertionError: wibble\n" +
            "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest"
      }

      test("after test should write testSuiteFinished for container ignored") {
         captureStandardOut {
            TeamCityConsoleWriter().testFinished(testCaseContainer, TestResult.ignored("ignore me?"))
         } shouldBe "\n##teamcity[testSuiteFinished name='my test container']\n"
      }

      test("after test should write testFinished for test success") {
         captureStandardOut {
            TeamCityConsoleWriter().testFinished(testCaseTest, TestResult.success(234.milliseconds))
         } shouldBe "\n##teamcity[testFinished name='my test case' duration='234']\n"
      }

      test("afterTestCaseExecution for errored test should write stack trace for error to std err, and write testFailed to std out") {
         captureStandardOut {
            captureStandardErr {
               TeamCityConsoleWriter().testFinished(
                  testCaseTest,
                  TestResult.throwable(AssertionError("wibble"), 925.milliseconds)
               )
            } shouldStartWith "\njava.lang.AssertionError: wibble\n" +
               "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriter"
         } shouldBe "\n##teamcity[testFailed name='my test case' message='wibble' duration='925']\n"
      }

      test("afterTestCaseExecution for failed test should write stack trace for error to std err, and write testFailed to std out") {
         captureStandardOut {
            captureStandardErr {
               TeamCityConsoleWriter().testFinished(
                  testCaseTest,
                  TestResult.throwable(AssertionError("wibble"), 33.milliseconds)
               )
            } shouldStartWith "\njava.lang.AssertionError: wibble\n" +
               "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriter"
         } shouldBe "\n##teamcity[testFailed name='my test case' message='wibble' duration='33']\n"
      }

      test("after test should write testIgnored for test with ignored") {
         captureStandardOut {
            TeamCityConsoleWriter().testFinished(testCaseTest, TestResult.ignored("ignore me?"))
         } shouldBe "\n##teamcity[testIgnored name='my test case' ignoreComment='ignore me?']\n"
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

         captureStandardOut {
            TeamCityConsoleWriter().testFinished(testCaseTest, TestResult.throwable(error, 8123.milliseconds))
         } shouldBe "\n##teamcity[testFailed name='my test case' message='Test failed' duration='8123']\n"
      }
   }
}
