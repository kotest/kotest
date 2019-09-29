package com.sksamuel.kotest.runner.console

import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.TestType
import io.kotest.assertions.shouldFail
import io.kotest.core.TestCaseConfig
import io.kotest.core.fromSpecClass
import io.kotest.core.sourceRef
import io.kotest.data.forall
import io.kotest.extensions.system.captureStandardErr
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldStartWith
import io.kotest.runner.console.TeamCityConsoleWriter
import io.kotest.shouldBe
import io.kotest.specs.FunSpec
import io.kotest.tables.row
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class TeamCityConsoleWriterTest : FunSpec() {

  private val klass: KClass<out Spec> = TeamCityConsoleWriterTest::class

  private val testCaseContainer = TestCase(
      Description.fromSpecClass(klass).append("my context").append("my test container"),
      this@TeamCityConsoleWriterTest,
      { },
      sourceRef(),
      TestType.Container,
      TestCaseConfig()
  )

  private val testCaseTest = testCaseContainer.copy(
      description = testCaseContainer.description.append("my test case"),
      type = TestType.Test
  )

  init {

    test("before spec class should write testSuiteStarted started") {
      captureStandardOut {
        TeamCityConsoleWriter().beforeSpecClass(klass)
      } shouldBe "\n##teamcity[testSuiteStarted name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest']\n"
    }

    test("before test should write testSuiteStarted for TestType.Container") {
      captureStandardOut {
        TeamCityConsoleWriter().beforeTestCaseExecution(testCaseContainer)
      } shouldBe "\n##teamcity[testSuiteStarted name='my test container' locationHint='kotest://com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest:29']\n"
    }

    test("before test should write testStarted for TestType.Test") {
      captureStandardOut {
        TeamCityConsoleWriter().beforeTestCaseExecution(testCaseTest)
      } shouldBe "\n##teamcity[testStarted name='my test case' locationHint='kotest://com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest:29']\n"
    }

    test("after spec class should write testSuiteFinished") {
      captureStandardOut {
        TeamCityConsoleWriter().afterSpecClass(klass, null)
      } shouldBe "\n##teamcity[testSuiteFinished name='com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest']\n"
    }

    test("afterSpecClass should insert dummy test and write testSuiteFinished for spec error") {
      val err = captureStandardErr {
        captureStandardOut {
          TeamCityConsoleWriter().afterSpecClass(klass, AssertionError("boom"))
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
        TeamCityConsoleWriter().afterTestCaseExecution(testCaseContainer, TestResult.success(Duration.ZERO))
      } shouldBe "\n##teamcity[testSuiteFinished name='my test container']\n"
    }

    test("after test should insert dummy test and write testSuiteFinished for container error") {
      captureStandardErr {
        captureStandardOut {
          TeamCityConsoleWriter().afterTestCaseExecution(testCaseContainer,
              TestResult.error(AssertionError("wibble"), Duration.ZERO))
        } shouldBe "\n" +
          "##teamcity[testStarted name='my test container <init>']\n" +
          "##teamcity[testFailed name='my test container <init>' message='wibble']\n" +
            "##teamcity[testSuiteFinished name='my test container']\n"
      } shouldStartWith "\njava.lang.AssertionError: wibble\n" +
          "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriterTest"
    }

    test("after test should write testSuiteFinished for container ignored") {
      captureStandardOut {
        TeamCityConsoleWriter().afterTestCaseExecution(testCaseContainer, TestResult.ignored("ignore me?"))
      } shouldBe "\n##teamcity[testSuiteFinished name='my test container']\n"
    }

    test("after test should write testFinished for test success") {
      captureStandardOut {
        TeamCityConsoleWriter().afterTestCaseExecution(testCaseTest, TestResult.success(Duration.ZERO))
      } shouldBe "\n##teamcity[testFinished name='my test case']\n"
    }

    test("afterTestCaseExecution for errored test should write stack trace for error to std err, and write testFailed to std out") {
      captureStandardOut {
        captureStandardErr {
          TeamCityConsoleWriter().afterTestCaseExecution(testCaseTest,
              TestResult.error(AssertionError("wibble"), Duration.ZERO)
          )
        } shouldStartWith "\njava.lang.AssertionError: wibble\n" +
            "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriter"
      } shouldBe "\n##teamcity[testFailed name='my test case' message='wibble']\n"
    }

    test("afterTestCaseExecution for failed test should write stack trace for error to std err, and write testFailed to std out") {
      captureStandardOut {
        captureStandardErr {
          TeamCityConsoleWriter().afterTestCaseExecution(
              testCaseTest,
              TestResult.failure(AssertionError("wibble"), Duration.ZERO)
          )
        } shouldStartWith "\njava.lang.AssertionError: wibble\n" +
            "\tat com.sksamuel.kotest.runner.console.TeamCityConsoleWriter"
      } shouldBe "\n##teamcity[testFailed name='my test case' message='wibble']\n"
    }

    test("after test should write testIgnored for test with ignored") {
      captureStandardOut {
        TeamCityConsoleWriter().afterTestCaseExecution(testCaseTest, TestResult.ignored("ignore me?"))
      } shouldBe "\n##teamcity[testIgnored name='my test case' ignoreComment='ignore me?']\n"
    }

    test("after test with error should handle multiline messages") {

      val error = shouldFail {
        forall(
          row(2, 3, 1),
          row(0, 2, 0)
        ) { a, b, max ->
          kotlin.math.max(a, b) shouldBe max
        }
      }

      captureStandardOut {
        TeamCityConsoleWriter().afterTestCaseExecution(testCaseTest, TestResult.failure(error, Duration.ZERO))
      } shouldBe "\n##teamcity[testFailed name='my test case' message='Test failed']\n"
    }
  }
}
