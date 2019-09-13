package io.kotest.runner.console

import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.TestStatus
import io.kotest.TestType
import io.kotest.core.fromSpecClass
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.reflect.KClass

class TeamCityConsoleWriter : ConsoleWriter {

  private var errors = false

  private fun locationHint(testCase: TestCase) = "kotest://" + testCase.spec.javaClass.canonicalName + ":" + testCase.source.lineNumber

  // intellij has no support for failed suites, so if a container or spec fails we must insert
  // a dummy "test" in order to show something is red or yellow.
  private fun insertDummyFailure(desc: Description, t: Throwable?) {
    val initName = desc.name + " <init>"
    println(TeamCityMessages.testStarted(initName))
    // we must print out the stack trace in between the dummy so it appears when you click on the test name
    if (t != null) printStackTrace(t)
    val message = t?.message?.let { if (it.lines().size == 1) it else null } ?: "Spec failed"
    println(TeamCityMessages.testFailed(initName).message(message))
  }

  private fun printStackTrace(t: Throwable) {
    val writer = StringWriter()
    t.printStackTrace(PrintWriter(writer))
    System.err.println()
    System.err.println(writer.toString())
    System.err.flush()
  }

  override fun hasErrors(): Boolean = errors

  override fun beforeSpecClass(klass: KClass<out Spec>) {
    println()
    println(TeamCityMessages.testSuiteStarted(Description.fromSpecClass(klass).name))
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {
    println()
    val desc = Description.fromSpecClass(klass)
    if (t == null) {
      println(TeamCityMessages.testSuiteFinished(desc.name))
    } else {
      errors = true
      insertDummyFailure(desc, t)
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
        errors = true
        when (testCase.type) {
          TestType.Container -> {
            insertDummyFailure(desc, result.error)
            println(TeamCityMessages.testSuiteFinished(desc.name))
          }
          TestType.Test -> {
            result.error?.apply { printStackTrace(this) }
            val message = result.error?.message?.let { if (it.lines().size == 1) it else null } ?: "Test failed"
            println(TeamCityMessages.testFailed(desc.name).message(message))
          }
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

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    if(result.status == TestStatus.Error || result.status == TestStatus.Failure) {
      errors = true
      insertDummyFailure(testCase.description, result.error)
    }
  }
}
