package io.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.TestType
import io.kotlintest.runner.jvm.TestEngineListener
import kotlin.reflect.KClass

class ConsoleTestEngineListener : TestEngineListener {

  private fun locationHint(testCase: TestCase) = "kotlintest://" + testCase.spec.javaClass.canonicalName + ":" + testCase.line

  override fun beforeSpecClass(klass: KClass<out Spec>) {
    println(ServiceMessageBuilder.testSuiteStarted(Description.spec(klass).name))
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {
    println(ServiceMessageBuilder.testSuiteFinished(Description.spec(klass).name))
  }

  override fun beforeTestCaseExecution(testCase: TestCase) {
    if (testCase.type == TestType.Container) {
      println(ServiceMessageBuilder.testSuiteStarted(testCase.description.name).locationHint(locationHint(testCase)))
    } else {
      println(ServiceMessageBuilder.testStarted(testCase.description.name).locationHint(locationHint(testCase)))
    }
  }

  override fun afterTestCaseExecution(testCase: TestCase, result: TestResult) {
    val msg = when (result.status) {
      TestStatus.Error ->
        ServiceMessageBuilder.testFailed(testCase.description.name).message(result.error?.message ?: "No message")
      TestStatus.Failure ->
        ServiceMessageBuilder.testFailed(testCase.description.name).message(result.error?.message ?: "No message")
      TestStatus.Ignored ->
        ServiceMessageBuilder.testFailed(testCase.description.name).ignoreComment(result.error?.message ?: "No reason")
      TestStatus.Success ->
        when (testCase.type) {
          TestType.Container -> ServiceMessageBuilder.testSuiteFinished(testCase.description.name)
          TestType.Test -> ServiceMessageBuilder.testSuiteFinished(testCase.description.name)
        }
    }
    println(msg)
  }
}