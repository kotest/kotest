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
    println(TeamCityMessages.testSuiteStarted(Description.spec(klass).name))
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {
    println()
    val desc = Description.spec(klass)
    if (t == null) {
      println(TeamCityMessages.testSuiteFinished(desc.name))
    } else {
      // intellij has no support for failed container tests, so if a container failed we must insert
      // a dummy "test" in order to show something is red or yellow.
      val initName = desc.name + " <init>"
      println(TeamCityMessages.testStarted(initName))
      println(TeamCityMessages.testFailed(initName).message(t.message ?: "Test Container Failed"))
      // We must mark the test suite as completed regardless of the state
      println(TeamCityMessages.testSuiteFinished(desc.name))
    }
  }

  override fun beforeTestCaseExecution(testCase: TestCase) {
    if (testCase.type == TestType.Container) {
      println()
      println(TeamCityMessages.testSuiteStarted(testCase.description.name).locationHint(locationHint(testCase)))
    } else {
      println()
      println(TeamCityMessages.testStarted(testCase.description.name).locationHint(locationHint(testCase)))
    }
  }

  override fun afterTestCaseExecution(testCase: TestCase, result: TestResult) {
    val desc = testCase.description
    println()
    when (result.status) {
      TestStatus.Failure, TestStatus.Error -> {
        result.error?.printStackTrace(System.err)
        when (testCase.type) {
          TestType.Container -> {
            // intellij has no support for failed container tests, so if a container failed we must insert
            // a dummy "test" in order to show something is red or yellow.
            val initName = desc.name + " <init>"
            println(TeamCityMessages.testStarted(initName))
            println(TeamCityMessages.testFailed(initName).message(result.error?.message ?: "Test Container Failed"))
            // We must mark the test suite as completed regardless of the state
            println(TeamCityMessages.testSuiteFinished(desc.name))
          }
          TestType.Test ->
            println(TeamCityMessages.testFailed(desc.name).message(result.error?.message ?: "No message"))
        }
      }
      TestStatus.Ignored -> {
        val msg = when (testCase.type) {
          // this will show up as green rather than ignored in intellij but that's ok, we can't rewrite the IDE!
          TestType.Container -> TeamCityMessages.testSuiteFinished(desc.name)
          TestType.Test -> TeamCityMessages.testIgnored(desc.name).ignoreComment(result.reason ?: "No reason")
        }
        println(msg)
      }
      TestStatus.Success -> {
        val msg = when (testCase.type) {
          TestType.Container -> TeamCityMessages.testSuiteFinished(desc.name)
          TestType.Test -> TeamCityMessages.testFinished(desc.name)
        }
        println(msg)
      }
    }
  }
}