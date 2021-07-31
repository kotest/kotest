package io.kotest.engine.test

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.teamcity.Locations
import io.kotest.engine.teamcity.TeamCityMessageBuilder

/**
 * A [TestCaseExecutionListener] that logs test statuses to the console using a [TeamCityMessageBuilder].
 */
object TeamCityTestCaseExecutionListener : TestCaseExecutionListener {

   override fun testStarted(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testStarted(testCase.displayName)
         .id(testCase.description.id.value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
      println()
      println(msg)
   }

   override fun testIgnored(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testIgnored(testCase.displayName)
         .id(testCase.description.id.value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
      println()
      println(msg)
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      val msg = when (result.status) {
         TestStatus.Ignored ->
            TeamCityMessageBuilder
               .testIgnored(testCase.displayName)
               .id(testCase.description.id.value)
               .locationHint(Locations.locationHint(testCase.spec::class))
               .testType(testCase.type.name)
               .message(result.reason ?: "No reason")
               .resultStatus(result.status.name)
         TestStatus.Success ->
            TeamCityMessageBuilder.testFinished(testCase.displayName)
               .duration(result.duration)
               .id(testCase.description.id.value)
               .locationHint(Locations.locationHint(testCase.spec::class))
               .testType(testCase.type.name)
         TestStatus.Error, TestStatus.Failure ->
            TeamCityMessageBuilder
               .testFailed(testCase.displayName)
               .duration(result.duration)
               .withException(result.error)
               .id(testCase.description.id.value)
               .locationHint(Locations.locationHint(testCase.spec::class))
               .testType(testCase.type.name)
      }
      println()
      println(msg)
   }
}
