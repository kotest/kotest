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
    println()
    println(TeamCityMessageBuilder.testSuiteStarted(Description.spec(klass).name))
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {
    println()
    println(TeamCityMessageBuilder.testSuiteFinished(Description.spec(klass).name))
  }

  override fun beforeTestCaseExecution(testCase: TestCase) {
    if (testCase.type == TestType.Container) {
      println()
      println(TeamCityMessageBuilder.testSuiteStarted(testCase.description.name).locationHint(locationHint(testCase)))
    } else {
      println()
      println(TeamCityMessageBuilder.testStarted(testCase.description.name).locationHint(locationHint(testCase)))
    }
  }

  override fun afterTestCaseExecution(testCase: TestCase, result: TestResult) {
    val msg = when (result.status) {
      TestStatus.Failure, TestStatus.Error -> {
        result.error?.printStackTrace(System.err)
        TeamCityMessageBuilder.testFailed(testCase.description.name).message(result.error?.message ?: "No message")
      }
      TestStatus.Ignored ->
        TeamCityMessageBuilder.testIgnored(testCase.description.name).ignoreComment(result.reason ?: "No reason")
      TestStatus.Success ->
        when (testCase.type) {
          TestType.Container -> TeamCityMessageBuilder.testSuiteFinished(testCase.description.name)
          TestType.Test -> TeamCityMessageBuilder.testFinished(testCase.description.name)
        }
    }
    println()
    println(msg)
  }
}