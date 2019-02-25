package io.kotlintest.runner.console

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestStatus
import io.kotlintest.TestType
import io.kotlintest.runner.jvm.TestEngineListener

class ConsoleTestEngineListener : TestEngineListener {

  private fun locationHint(testCase: TestCase): Pair<String, String> {
    return "locationHint" to "kotlintest://" + testCase.spec.javaClass.canonicalName + ":" + testCase.line
  }

  /**
   * See https://github.com/JetBrains/intellij-plugins/blob/4481281dcdeee5db6cf6909ce302021716b0f0a7/flex/tools/flexunit-support/PureAs/src/com/intellij/flexunit/runner/TestRunnerBase.as
   * https://github.com/JetBrains/intellij-plugins/blob/4481281dcdeee5db6cf6909ce302021716b0f0a7/JsTestDriver/rt/src/com/google/jstestdriver/idea/rt/execution/tc/TCCommand.java
   * https://github.com/JetBrains/intellij-plugins/blob/71227775480e79d2fa36e4b767c14cc12c271f83/cucumber-java/test/org/jetbrains/plugins/cucumber/java/run/CucumberJvmFormatterTest.java
   * https://github.com/JetBrains/intellij-plugins/blob/4481281dcdeee5db6cf6909ce302021716b0f0a7/ruby-testing/src/rb/testing/patch/common/teamcity/utils/service_message_factory.rb
   *
   * Or can look at the implementation in [ServiceMessageBuilder] which isn't quite fully featured.
   */
  private fun log(msg: String, params: Map<String, String>) {
    println()
    if (params.isEmpty()) {
      println("##teamcity[$msg]")
    } else {
      println("##teamcity[$msg ${params.map { "${it.key}='${it.value}'" }.joinToString(" ")}]")
    }
  }

  override fun beforeTestCaseExecution(testCase: TestCase) {
    if (testCase.type == TestType.Container) {
      log(
          "testSuiteStarted",
          mapOf(
              "name" to testCase.description.name,
              locationHint(testCase)
          )
      )
    } else {
      log(
          "testStarted",
          mapOf(
              "name" to testCase.description.name,
              locationHint(testCase)
          )
      )
    }
  }

  override fun afterTestCaseExecution(testCase: TestCase, result: TestResult) {
    when (result.status) {
      TestStatus.Error -> log(
          "testFailed",
          mapOf("name" to testCase.description.name, "message" to (result.error?.message ?: "no error message"))
      )
      TestStatus.Failure -> log(
          "testFailed",
          mapOf("name" to testCase.description.name, "message" to (result.error?.message ?: "no error message"))
      )
      TestStatus.Ignored -> log(
          "testIgnored",
          mapOf("name" to testCase.description.name, "ignoreComment" to (result.error?.message ?: "no reason"))
      )
      TestStatus.Success -> when (testCase.type) {
        TestType.Container -> log(
            "testSuiteFinished",
            mapOf("name" to testCase.description.name)
        )
        TestType.Test -> log(
            "testFinished",
            mapOf("name" to testCase.description.name)
        )
      }

    }
//    if (testCase.type == TestType.Container) {
//      log("testSuiteFinished", mapOf("name" to testCase.description.name))
//    }
  }
}