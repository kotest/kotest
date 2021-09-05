package io.kotest.engine.listener

import io.kotest.core.plan.displayName
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.teamcity.Locations
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that logs events to the console using a [TeamCityMessageBuilder].
 */
object TeamCityTestEngineListener : TestEngineListener {

   override suspend fun engineStarted(classes: List<KClass<*>>) {}
   override suspend fun engineFinished(t: List<Throwable>) {}

   override suspend fun specStarted(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(kclass.displayName() ?: kclass.bestName())
         .id(kclass.toDescription().id.value)
         .locationHint(Locations.locationHint(kclass))
         .spec()
      println()
      println(msg)
   }

   override suspend fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(kclass.displayName() ?: kclass.bestName())
         .id(kclass.toDescription().id.value)
         .locationHint(Locations.locationHint(kclass))
         .resultStatus(TestStatus.Success.name)
         .spec()
      println()
      println(msg)
   }

   override suspend fun testStarted(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testStarted(testCase.displayName)
         .id(testCase.description.id.value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
      println()
      println(msg)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      val msg = TeamCityMessageBuilder
         .testIgnored(testCase.displayName)
         .id(testCase.description.id.value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .message(reason)
      println()
      println(msg)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
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
