package io.kotest.engine.listener

import io.kotest.core.config.configuration
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.TestType
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.teamcity.Locations
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.engine.test.names.getDisplayNameFormatter
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

   private val formatter = getDisplayNameFormatter(configuration)

   private fun TestCase.isContainer() = this.type == TestType.Container

   private fun TestCase.type() = if (isContainer()) "Container" else "Test"

   // intellij has no method for failed suites, so if a container or spec fails we must insert
   // a dummy "test" in order to tag the error against that
   private fun insertPlaceholderFailure(name: String, t: Throwable?, testCase: TestCase) {
      require(testCase.isContainer())
      val dummyTestName = "$name <error>"

      val msg = TeamCityMessageBuilder
         .testStarted(prefix, dummyTestName)
         .id(dummyTestName)
         .parent(testCase.descriptor.path().value)
         .testType(testCase.type())
         .build()

      println(msg)

      // we must print out the stack trace in between the dummy, so it appears when you click on the test name
      t?.printStackTrace()
      val message = t?.message?.let { if (it.lines().size == 1) it else null }

      val msg2 = TeamCityMessageBuilder
         .testFailed(prefix, dummyTestName)
         .id(dummyTestName)
         .parent(testCase.descriptor.path().value)
         .message(message)
         .testType(testCase.type())
         .build()

      println(msg2)

      val msg3 = TeamCityMessageBuilder
         .testFinished(prefix, dummyTestName)
         .id(dummyTestName)
         .parent(testCase.descriptor.path().value)
         .testType(testCase.type())
         .build()

      println(msg3)
   }

   override suspend fun engineStarted() {}

   override suspend fun engineInitialized(context: EngineContext) {}

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) {
         println(TeamCityMessageBuilder.testStarted(prefix, "Engine failure").build())
         val errors = t.joinToString("\n") { it.message ?: t::class.bestName() }
         println(TeamCityMessageBuilder.testFailed(prefix, "Engine failure").message(errors).build())
         //println(TeamCityMessageBuilder.testFinished(prefix, "Engine failure").build())
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      start(kclass)
   }

   private fun start(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         .locationHint(Locations.locationHint(kclass))
         .spec()
         .build()
      println(msg)
      started.add(kclass)
   }

   override suspend fun specFinished(kclass: KClass<*>, results: Map<TestCase, TestResult>) {}

   override suspend fun specExit(kclass: KClass<*>, t: Throwable?) {
      // we must start the test if it wasn't already started
      if (!started.contains(kclass))
         start(kclass)

      if (t != null) {

         val dummyTestName = "<error>"

         val msg = TeamCityMessageBuilder
            .testStarted(prefix, dummyTestName)
            .id(dummyTestName)
            .parent(kclass.toDescriptor().path().value)
            .testType("Test")
            .build()

         println(msg)

         // we must print out the stack trace in between the dummy, so it appears when you click on the test name
         t.printStackTrace()
         val message = t.message?.let { if (it.lines().size == 1) it else null }

         val msg2 = TeamCityMessageBuilder
            .testFailed(prefix, dummyTestName)
            .id(dummyTestName)
            .parent(kclass.toDescriptor().path().value)
            .message(message)
            .testType("Test")
            .build()

         println(msg2)

         val msg3 = TeamCityMessageBuilder
            .testFinished(prefix, dummyTestName)
            .id(dummyTestName)
            .parent(kclass.toDescriptor().path().value)
            .testType("Test")
            .build()

         println(msg3)

      }
      finish(kclass, t)
   }

   override suspend fun specIgnored(kclass: KClass<*>) {}

   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      if (results.isEmpty()) {
         start(kclass)
         val msg = TeamCityMessageBuilder
            .testIgnored(prefix, "<no tests>")
            .id("<no tests>")
            .parent(kclass.toDescriptor().path().value)
            .testType("Test")
            .resultStatus(TestStatus.Ignored.name)
            .build()
         println(msg)
      } else {
         start(kclass)
         results.forEach { (testCase, result) ->
            testIgnored(testCase, result.reason)
         }
      }
   }

   private fun finish(kclass: KClass<*>, t: Throwable?) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         .locationHint(Locations.locationHint(kclass))
         .resultStatus(if (t == null) TestStatus.Success.name else TestStatus.Error.name)
         .spec()
         .build()
      println(msg)
      finished.add(kclass)
   }

   override suspend fun testStarted(testCase: TestCase) {
      when (testCase.isContainer()) {
         true -> startTestSuite(testCase)
         false -> startTest(testCase)
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      val msg = TeamCityMessageBuilder
         .testIgnored(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .message(reason)
         .resultStatus(TestStatus.Ignored.name)
         .build()
      println(msg)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      when (result.status) {
         TestStatus.Ignored -> return
         TestStatus.Success -> when (testCase.isContainer()) {
            true -> finishTestSuite(testCase, result)
            false -> finishTest(testCase, result)
         }
         TestStatus.Error, TestStatus.Failure -> when (testCase.isContainer()) {
            true -> {
               insertPlaceholderFailure(formatter.format(testCase), result.error, testCase)
               finishTestSuite(testCase, result)
            }
            false -> {
               failTest(testCase, result)
               finishTest(testCase, result)
            }
         }
      }
   }

   private fun startTest(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testStarted(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .build()
      println(msg)
   }

   private fun startTestSuite(testCase: TestCase) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .build()
      println(msg)
   }

   private fun failTest(testCase: TestCase, result: TestResult) {
      val msg = TeamCityMessageBuilder
         .testFailed(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .withException(result.error)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .resultStatus(result.status.name)
         .withException(result.error)
         .build()
      println(msg)
   }

   private fun finishTest(testCase: TestCase, result: TestResult) {
      val msg = TeamCityMessageBuilder
         .testFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .resultStatus(result.status.name)
         .build()
      println(msg)
   }

   private fun finishTestSuite(testCase: TestCase, result: TestResult) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .resultStatus(result.status.name)
         .build()
      println(msg)
   }
}
