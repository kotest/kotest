package io.kotest.engine.listener

import io.kotest.core.config.configuration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.teamcity.Locations
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.engine.test.names.getDisplayNameFormatter
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

   // contains any tests which we know are parents
   private val parents = hashSetOf<Descriptor>()

   // set to true if we had any errors at all
   private var errors = false

   private fun TestCase.isContainer() = parents.contains(descriptor)

   private fun TestCase.type() = if (isContainer()) "Container" else "Test"

   // intellij has no method for failed suites, so if a container or spec fails we must insert
   // a dummy "test" in order to tag the error against that
   private fun insertDummyFailure(name: String, t: Throwable?, testCase: TestCase) {
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

   override suspend fun engineStarted(classes: List<KClass<*>>) {}

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) {
         println(TeamCityMessageBuilder.testStarted(prefix, "Test failure").build())
         val errors = t.joinToString("\n") { t.toString() }
         println(TeamCityMessageBuilder.testFailed(prefix, "Test failure").message(errors).build())
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
         .testSuiteFinished(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         .locationHint(Locations.locationHint(kclass))
         .resultStatus(TestStatus.Success.name)
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
               insertDummyFailure(formatter.format(testCase), result.error, testCase)
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
      val msg1 = TeamCityMessageBuilder
         .testFailed(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .withException(result.error)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .resultStatus(result.status.name)
         .build()
      println(msg1)
   }

   private fun finishTest(testCase: TestCase, result: TestResult) {
      val msg2 = TeamCityMessageBuilder
         .testFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.locationHint(testCase.spec::class))
         .testType(testCase.type.name)
         .resultStatus(result.status.name)
         .build()
      println(msg2)
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
