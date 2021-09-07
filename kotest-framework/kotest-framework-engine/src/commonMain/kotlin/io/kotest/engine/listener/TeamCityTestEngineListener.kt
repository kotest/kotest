package io.kotest.engine.listener

import io.kotest.core.plan.displayName
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.Description
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
class TeamCityTestEngineListener(
   private val prefix: String = TeamCityMessageBuilder.TeamCityPrefix
) : TestEngineListener {

   // these are the specs that have been started and the test started event sent to team city
   private val started = mutableSetOf<KClass<*>>()

   // these are the specs for which we received the specFinished event
   private val finished = mutableSetOf<KClass<*>>()

   // intellij has no method for failed suites, so if a container or spec fails we must insert
   // a dummy "test" in order to tag the error against that
   private fun insertDummyFailure(desc: Description, t: Throwable?) {
      val dummyTestName = desc.name.displayName + " <error>"
      println(TeamCityMessageBuilder.testStarted(prefix, dummyTestName).build())
      // we must print out the stack trace in between the dummy, so it appears when you click on the test name
      t?.printStackTrace()
      val message = t?.message?.let { if (it.lines().size == 1) it else null } ?: "Spec failed"
      println(TeamCityMessageBuilder.testFailed(prefix, dummyTestName).message(message).build())
   }

   override suspend fun engineStarted(classes: List<KClass<*>>) {}

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) {
         println()
         println(TeamCityMessageBuilder.testStarted(prefix, "Test failure").build())
         println()
         val errors = t.joinToString("\n") { t.toString() }
         println(TeamCityMessageBuilder.testFailed(prefix, "Test failure").message(errors).build())
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      start(kclass)
   }

   private fun start(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, kclass.displayName() ?: kclass.bestName())
         .id(kclass.toDescription().id.value)
         .locationHint(Locations.locationHint(kclass))
         .spec()
         .build()
      println()
      println(msg)
      started.add(kclass)
   }

   override suspend fun specFinished(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
     finish(kclass)
   }

   override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {
      // if we have an error we must tag it to a dummy test
      if (t != null) {
         if (!started.contains(kclass))
            start(kclass)
      }
      super.specExit(kclass, t)
   }

   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      start(kclass)
      results.forEach { (testCase, result) ->
         testIgnored(testCase, result.reason)
      }
      finish(kclass)
   }

   private fun finish(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, kclass.displayName() ?: kclass.bestName())
         .id(kclass.toDescription().id.value)
         .locationHint(Locations.locationHint(kclass))
         .resultStatus(TestStatus.Success.name)
         .spec()
         .build()
      println()
      println(msg)
      finished.add(kclass)
   }

   override suspend fun testStarted(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testStarted(prefix, testCase.displayName)
         .id(testCase.description.id.value)
         .parent(testCase.description.parent.id.value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .build()
      println()
      println(msg)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      val msg = TeamCityMessageBuilder
         .testIgnored(prefix, testCase.displayName)
         .id(testCase.description.id.value)
         .parent(testCase.description.parent.id.value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .message(reason)
         .build()
      println()
      println(msg)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      val msg = when (result.status) {
         TestStatus.Ignored ->
            TeamCityMessageBuilder
               .testIgnored(prefix, testCase.displayName)
               .id(testCase.description.id.value)
               .parent(testCase.description.parent.id.value)
               .locationHint(Locations.locationHint(testCase.spec::class))
               .testType(testCase.type.name)
               .message(result.reason ?: "No reason")
               .resultStatus(result.status.name)
               .build()
         TestStatus.Success ->
            TeamCityMessageBuilder
               .testFinished(prefix, testCase.displayName)
               .id(testCase.description.id.value)
               .parent(testCase.description.parent.id.value)
               .duration(result.duration)
               .locationHint(Locations.locationHint(testCase.spec::class))
               .testType(testCase.type.name)
               .build()
         TestStatus.Error, TestStatus.Failure ->
            TeamCityMessageBuilder
               .testFailed(prefix, testCase.displayName)
               .id(testCase.description.id.value)
               .parent(testCase.description.parent.id.value)
               .duration(result.duration)
               .withException(result.error)
               .locationHint(Locations.locationHint(testCase.spec::class))
               .resultStatus(result.status.name)
               .testType(testCase.type.name)
               .build()
      }
      println()
      println(msg)
   }
}
