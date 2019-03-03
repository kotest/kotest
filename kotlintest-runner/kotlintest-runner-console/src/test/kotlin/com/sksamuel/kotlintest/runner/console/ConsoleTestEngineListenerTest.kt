package com.sksamuel.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestResult
import io.kotlintest.TestType
import io.kotlintest.extensions.TestListener
import io.kotlintest.extensions.system.SystemErrWireListener
import io.kotlintest.extensions.system.SystemOutWireListener
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

  private val stdout = SystemOutWireListener(true)
  private val stderr = SystemErrWireListener(false)

  override fun listeners(): List<TestListener> = listOf(stdout, stderr)

  init {

    test("before spec class should write testSuiteStarted started") {
      ConsoleTestEngineListener().beforeSpecClass(klass)
      stdout.output() shouldBe "\n##teamcity[testSuiteStarted name='io.kotlintest.runner.console.ConsoleTestEngineListener']\n"
    }

    test("before test should write testSuiteStarted for TestType.Container") {
      ConsoleTestEngineListener().beforeTestCaseExecution(testCaseContainer)
      stdout.output() shouldBe "\n##teamcity[testSuiteStarted name='my test container' locationHint='kotlintest://com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest:123']\n"
    }

    test("before test should write testStarted for TestType.Test") {
      ConsoleTestEngineListener().beforeTestCaseExecution(testCaseTest)
      stdout.output() shouldBe "\n##teamcity[testStarted name='my test case' locationHint='kotlintest://com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest:123']\n"
    }

    test("after spec class should write testSuiteFinished") {
      ConsoleTestEngineListener().afterSpecClass(klass, null)
      stdout.output() shouldBe "\n##teamcity[testSuiteFinished name='io.kotlintest.runner.console.ConsoleTestEngineListener']\n"
    }

    test("after test should write testSuiteFinished for container success") {
      ConsoleTestEngineListener().afterTestCaseExecution(testCaseContainer, TestResult.Success)
      stdout.output() shouldBe "\n##teamcity[testSuiteFinished name='my test container']\n"
    }

    test("after test should write testSuiteFinished for container error") {
      ConsoleTestEngineListener().afterTestCaseExecution(testCaseContainer, TestResult.error(AssertionError("wibble")))
      stdout.output() shouldBe "\n##teamcity[testFailed name='my test container' message='wibble']\n"
    }

    test("after test should write stack trace for error to std err") {
      ConsoleTestEngineListener().afterTestCaseExecution(testCaseContainer, TestResult.error(AssertionError("wibble")))
      stderr.output() shouldStartWith  "java.lang.AssertionError: wibble\n" +
          "\tat com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest"
    }

    test("after test should write testSuiteFinished for container ignored") {
      ConsoleTestEngineListener().afterTestCaseExecution(testCaseContainer, TestResult.ignored("ignore me?"))
      stdout.output() shouldBe "\n##teamcity[testIgnored name='my test container' ignoreComment='ignore me?']\n"
    }

    test("after test should write testFinished for test success") {
      ConsoleTestEngineListener().afterTestCaseExecution(testCaseTest, TestResult.Success)
      stdout.output() shouldBe "\n##teamcity[testFinished name='my test case']\n"
    }

    test("after test should write testFailed for test with error") {
      ConsoleTestEngineListener().afterTestCaseExecution(testCaseTest, TestResult.error(AssertionError("wibble")))
      stdout.output() shouldBe "\n##teamcity[testFailed name='my test case' message='wibble']\n"
    }

    test("after test should write stack trace for error to std out") {
      ConsoleTestEngineListener().afterTestCaseExecution(testCaseTest, TestResult.error(AssertionError("wibble")))
      stderr.output() shouldStartWith "java.lang.AssertionError: wibble\n" +
          "\tat com.sksamuel.kotlintest.runner.console.ConsoleTestEngineListenerTest"
    }

    test("after test should write testIgnored for test with ignored") {
      ConsoleTestEngineListener().afterTestCaseExecution(testCaseTest, TestResult.ignored("ignore me?"))
      stdout.output() shouldBe "\n##teamcity[testIgnored name='my test case' ignoreComment='ignore me?']\n"
    }
  }
}