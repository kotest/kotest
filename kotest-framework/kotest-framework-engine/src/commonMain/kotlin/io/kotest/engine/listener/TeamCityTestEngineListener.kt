package io.kotest.engine.listener

import io.kotest.core.plan.displayName
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
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
   private fun insertDummyFailure(name: String, t: Throwable?, testCase: TestCase) {
      require(testCase.type == TestType.Container)
      val dummyTestName = "$name <error>"

      val msg = TeamCityMessageBuilder
         .testStarted(prefix, dummyTestName)
         .id(dummyTestName)
         .parent(testCase.description.id.value)
         .testType(TestType.Test.name)
         .build()

      println()
      println(msg)

      // we must print out the stack trace in between the dummy, so it appears when you click on the test name
      t?.printStackTrace()
      val message = t?.message?.let { if (it.lines().size == 1) it else null }

      val msg2 = TeamCityMessageBuilder
         .testFailed(prefix, dummyTestName)
         .id(dummyTestName)
         .parent(testCase.description.id.value)
         .message(message)
         .testType(TestType.Test.name)
         .build()

      println()
      println(msg2)

      val msg3 = TeamCityMessageBuilder
         .testFinished(prefix, dummyTestName)
         .id(dummyTestName)
         .parent(testCase.description.id.value)
         .testType(TestType.Test.name)
         .build()

      println()
      println(msg3)
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

   override suspend fun specFinished(kclass: KClass<*>, results: Map<TestCase, TestResult>) {}

   override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {
      // we must start the test if it wasn't already started
      if (!started.contains(kclass))
         start(kclass)
      finish(kclass)
   }

   override suspend fun specIgnored(kclass: KClass<out Spec>) {}

   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      start(kclass)
      results.forEach { (testCase, result) ->
         testIgnored(testCase, result.reason)
      }
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
      when (testCase.type) {
         TestType.Test -> startTest(testCase)
         TestType.Container -> startTestSuite(testCase)
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      val msg = TeamCityMessageBuilder
         .testIgnored(prefix, testCase.displayName)
         .id(testCase.description.id.value)
         .parent(testCase.description.parent.id.value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .message(reason)
         .resultStatus(TestStatus.Ignored.name)
         .build()
      println()
      println(msg)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      when (result.status) {
         TestStatus.Ignored -> return
         TestStatus.Success -> when (testCase.type) {
            TestType.Container -> finishTestSuite(testCase, result)
            TestType.Test -> finishTest(testCase, result)
         }
         TestStatus.Error, TestStatus.Failure -> when (testCase.type) {
            TestType.Container -> {
               insertDummyFailure(testCase.displayName, result.error, testCase)
               finishTestSuite(testCase, result)
            }
            TestType.Test -> {
               failTest(testCase, result)
               finishTest(testCase, result)
            }
         }
      }
   }

   private fun startTest(testCase: TestCase) {
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

   private fun startTestSuite(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, testCase.displayName)
         .id(testCase.description.id.value)
         .parent(testCase.description.parent.id.value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .build()
      println()
      println(msg)
   }

   private fun failTest(testCase: TestCase, result: TestResult) {
      val msg1 = TeamCityMessageBuilder
         .testFailed(prefix, testCase.displayName)
         .id(testCase.description.id.value)
         .parent(testCase.description.parent.id.value)
         .duration(result.duration)
         .withException(result.error)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .resultStatus(result.status.name)
         .build()
      println()
      println(msg1)
   }

   private fun finishTest(testCase: TestCase, result: TestResult) {
      val msg2 = TeamCityMessageBuilder
         .testFinished(prefix, testCase.displayName)
         .id(testCase.description.id.value)
         .parent(testCase.description.parent.id.value)
         .duration(result.duration)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .resultStatus(result.status.name)
         .build()
      println()
      println(msg2)
   }

   private fun finishTestSuite(testCase: TestCase, result: TestResult) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, testCase.displayName)
         .id(testCase.description.id.value)
         .parent(testCase.description.parent.id.value)
         .duration(result.duration)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .resultStatus(result.status.name)
         .build()
      println()
      println(msg)
   }
}
