package com.sksamuel.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.TestType
import io.kotlintest.extensions.system.captureStandardErr
import io.kotlintest.extensions.system.captureStandardOut
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.runner.console.ConsoleTestEngineListener
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import kotlin.reflect.KClass

class ConsoleTestEngineListenerTest : FunSpec() {

  @Suppress("UNCHECKED_CAST")
  private val klass: KClass<out Spec> = ConsoleTestEngineListener::class as KClass<out Spec>

  private val testCaseContainer = TestCase(
      Description.spec(klass).append("my context").append("my test container"),
      this@ConsoleTestEngineListenerTest,
      { },
      123,
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
        ConsoleTestEngineListener().beforeSpecClass(klass)
      } shouldBe "\n##teamcity[testSuiteStarted name='io.kotlintest.runner.console.ConsoleTestEngineListener']\n"
    }

    test("before test should write testSuiteStarted for TestType.Container") {
      captureStandardOut {
        ConsoleTestEngineListener().beforeTestCaseExecution(testCaseContainer)
      } shouldBe "\n##teamcity[testSuiteStarted name='my test container' locationHint='kotlintest://com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest:123']\n"
    }

    test("before test should write testStarted for TestType.Test") {
      captureStandardOut {
        ConsoleTestEngineListener().beforeTestCaseExecution(testCaseTest)
      } shouldBe "\n##teamcity[testStarted name='my test case' locationHint='kotlintest://com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest:123']\n"
    }

    test("after spec class should write testSuiteFinished") {
      captureStandardOut {
        ConsoleTestEngineListener().afterSpecClass(klass, null)
      } shouldBe "\n##teamcity[testSuiteFinished name='io.kotlintest.runner.console.ConsoleTestEngineListener']\n"
    }

    test("afterSpecClass should insert dummy test and write testSuiteFinished for spec error") {
      captureStandardErr {
        captureStandardOut {
          ConsoleTestEngineListener().afterSpecClass(klass, AssertionError("boom"))
        } shouldBe "\n" +
          "##teamcity[testStarted name='io.kotlintest.runner.console.ConsoleTestEngineListener <init>']\n" +
            "##teamcity[testFailed name='io.kotlintest.runner.console.ConsoleTestEngineListener <init>' message='boom']\n" +
          "##teamcity[testSuiteFinished name='io.kotlintest.runner.console.ConsoleTestEngineListener']\n"
      } shouldStartWith "java.lang.AssertionError: boom\n" +
          "\tat com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest"
    }

    test("after test should write testSuiteFinished for container success") {
      captureStandardOut {
        ConsoleTestEngineListener().afterTestCaseExecution(testCaseContainer, TestResult.Success)
      } shouldBe "\n##teamcity[testSuiteFinished name='my test container']\n"
    }

    test("after test should insert dummy test and write testSuiteFinished for container error") {
      captureStandardErr {
        captureStandardOut {
          ConsoleTestEngineListener().afterTestCaseExecution(testCaseContainer,
              TestResult.error(AssertionError("wibble")))
        } shouldBe "\n" +
          "##teamcity[testStarted name='my test container <init>']\n" +
          "##teamcity[testFailed name='my test container <init>' message='wibble']\n" +
            "##teamcity[testSuiteFinished name='my test container']\n"
      } shouldStartWith "java.lang.AssertionError: wibble\n" +
          "\tat com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest"
    }

    test("after test should write testSuiteFinished for container ignored") {
      captureStandardOut {
        ConsoleTestEngineListener().afterTestCaseExecution(testCaseContainer, TestResult.ignored("ignore me?"))
      } shouldBe "\n##teamcity[testSuiteFinished name='my test container']\n"
    }

    test("after test should write testFinished for test success") {
      captureStandardOut {
        ConsoleTestEngineListener().afterTestCaseExecution(testCaseTest, TestResult.Success)
      } shouldBe "\n##teamcity[testFinished name='my test case']\n"
    }

    test("afterTestCaseExecution for errored test should write stack trace for error to std err, and write testFailed to std out") {
      captureStandardOut {
        captureStandardErr {
          ConsoleTestEngineListener().afterTestCaseExecution(testCaseTest, TestResult.error(AssertionError("wibble")))
        } shouldStartWith "java.lang.AssertionError: wibble\n" +
          "\tat com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest"
      } shouldBe "\n##teamcity[testFailed name='my test case' message='wibble']\n"
    }

    test("afterTestCaseExecution for failed test should write stack trace for error to std err, and write testFailed to std out") {
      captureStandardOut {
        captureStandardErr {
          ConsoleTestEngineListener().afterTestCaseExecution(testCaseTest, TestResult.failure(AssertionError("wibble")))
        } shouldStartWith "java.lang.AssertionError: wibble\n" +
            "\tat com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest"
      } shouldBe "\n##teamcity[testFailed name='my test case' message='wibble']\n"
    }

    test("after test should write testIgnored for test with ignored") {
      captureStandardOut {
        ConsoleTestEngineListener().afterTestCaseExecution(testCaseTest, TestResult.ignored("ignore me?"))
      } shouldBe "\n##teamcity[testIgnored name='my test case' ignoreComment='ignore me?']\n"
    }
  }
}