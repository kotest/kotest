package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.teamcity.Locations
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that logs events to the console using a [TeamCityMessageBuilder].
 */
@KotestInternal
class TeamCityTestEngineListener(
   private val prefix: String = TeamCityMessageBuilder.TeamCityPrefix,
   private val details: Boolean = true,
) : TestEngineListener {

   private val logger = Logger(TeamCityTestEngineListener::class)

   private var formatter = FallbackDisplayNameFormatter.default(ProjectConfiguration())

   // once a spec has completed, we want to be able to check whether any given test is
   // a container or a leaf test, and so this map contains all test that have children
   private val children = mutableMapOf<Descriptor, MutableList<TestCase>>()

   private val results = mutableMapOf<Descriptor, TestResult>()

   private val started = mutableSetOf<Descriptor.TestDescriptor>()

   // intellij has no method for failed suites, so if a container or spec fails we must insert
   // a dummy "test" in order to tag the error against that
   private fun insertPlaceholder(t: Throwable, parent: Descriptor) {

      val (name, cause) = ExtensionExceptionExtractor.resolve(t)

      val msg1 = TeamCityMessageBuilder
         .testStarted(prefix, name)
         .id(name)
         .parent(parent.path().value)
         .build()
      println(msg1)

      // we must print out the stack trace in between the dummy, so it appears when you click on the test name
      //t?.printStackTrace()

      val msg2 = TeamCityMessageBuilder
         .testFailed(prefix, name)
         .id(name)
         .parent(parent.path().value)
         .withException(cause, details)
         .build()
      println(msg2)

      val msg3 = TeamCityMessageBuilder
         .testFinished(prefix, name)
         .id(name)
         .parent(parent.path().value)
         .build()
      println(msg3)
   }

   override suspend fun engineStarted() {}

   override suspend fun engineInitialized(context: EngineContext) {
      formatter = getFallbackDisplayNameFormatter(context.configuration.registry, context.configuration)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) {
         t.withIndex().forEach { (index, error) ->
            val testName = if (t.size == 1) "Engine exception" else "Engine exception ${index + 1}"
            println(TeamCityMessageBuilder.testStarted(prefix, testName).build())
            val message = error.message ?: t::class.bestName()
            println(TeamCityMessageBuilder.testFailed(prefix, testName).message(message).build())
            println(TeamCityMessageBuilder.testFinished(prefix, testName).build())
         }
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         .locationHint(Locations.location(kclass))
         .build()
      println(msg)
   }

   // ignored specs are completely hidden from output in team city
   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {}

   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {

      // if the spec itself has an error, we must insert a placeholder test
      when (val t = result.errorOrNull) {
         null -> Unit
         is MultipleExceptions -> t.causes.forEach { insertPlaceholder(it, kclass.toDescriptor()) }
         else -> insertPlaceholder(t, kclass.toDescriptor())
      }

      finishSpec(kclass)
      results.clear()
      children.clear()
   }

   private fun finishSpec(kclass: KClass<*>) {
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, formatter.format(kclass))
         .id(kclass.toDescriptor().path().value)
         .locationHint(Locations.location(kclass))
         .build()
      println(msg)
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "testStarted $testCase") }
      if (testCase.parent != null) addChild(testCase)
      when (testCase.type) {
         TestType.Container -> {
            val p = testCase.parent
            // we might have a container inside a dynamic parent, in which case we need to start the dynamic parent
            if (p != null && p.type == TestType.Dynamic) {
               if (!started.contains(p.descriptor)) startTestSuite(p)
            }
            startTestSuite(testCase)
         }
         TestType.Test -> {
            val p = testCase.parent
            // we might have a container inside a dynamic parent, in which case we need to start it
            if (p != null && p.type == TestType.Dynamic) {
               if (!started.contains(p.descriptor)) startTestSuite(p)
            }
            startTest(testCase)
         }
         TestType.Dynamic -> {
            val p = testCase.parent
            // we might have a dynamic inside another dynamic parent, in which case we need to start the dynamic parent
            if (p != null && p.type == TestType.Dynamic) {
               if (!started.contains(p.descriptor)) startTestSuite(p)
            }
         }
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      ignoreTest(testCase, TestResult.Ignored(reason))
   }

   private fun addChild(testCase: TestCase) {
      children.getOrPut(testCase.descriptor.parent) { mutableListOf() }.add(testCase)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "testFinished $testCase") }
      results[testCase.descriptor] = result
      when (testCase.type) {
         TestType.Container -> {
            failTestSuiteIfError(testCase, result)
            finishTestSuite(testCase, result)
         }
         TestType.Test -> {
            if (!started.contains(testCase.descriptor)) startTest(testCase)
            if (result.isErrorOrFailure) failTest(testCase, result)
            finishTest(testCase, result)
         }
         TestType.Dynamic -> {
            if (isParent(testCase)) {
               if (!started.contains(testCase.descriptor)) startTestSuite(testCase)
               failTestSuiteIfError(testCase, result)
               finishTestSuite(testCase, result)
            } else {
               if (!started.contains(testCase.descriptor)) startTest(testCase)
               if (result.isErrorOrFailure) failTest(testCase, result)
               finishTest(testCase, result)
            }
         }
      }
   }

   private fun failTestSuiteIfError(testCase: TestCase, result: TestResult) {
      // test suites cannot be in a failed state, so we must insert a placeholder to hold any error
      when (val t = result.errorOrNull) {
         null -> Unit
         is MultipleExceptions -> t.causes.forEach { insertPlaceholder(it, testCase.descriptor) }
         else -> insertPlaceholder(t, testCase.descriptor)
      }
   }

   // returns true if this test case contains child tests
   private fun isParent(testCase: TestCase) = children.getOrElse(testCase.descriptor) { mutableListOf() }.isNotEmpty()

   /**
    * For a given [TestCase] will output the "test ignored" message.
    */
   private fun ignoreTest(testCase: TestCase, result: TestResult.Ignored) {
      val msg = TeamCityMessageBuilder
         .testIgnored(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.location(testCase.source))
         .message(result.reason)
         .result(result)
         .build()
      println(msg)
   }

   /**
    * For a [TestCase] will output the "test started" message.
    */
   private fun startTest(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "startTest ${testCase.descriptor.path().value}") }
      val msg = TeamCityMessageBuilder
         .testStarted(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.location(testCase.source))
         .build()
      println(msg)
      started.add(testCase.descriptor)
   }

   /**
    * For a given [TestCase] will output the "test failed" message.
    */
   private fun failTest(testCase: TestCase, result: TestResult) {
      val msg = TeamCityMessageBuilder
         .testFailed(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.location(testCase.source))
         .withException(result.errorOrNull, details)
         .result(result)
         .build()
      println(msg)
   }

   /**
    * For a given [TestCase] will output the "test finished" message.
    */
   private fun finishTest(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "finishTest ${testCase.descriptor.path().value}") }
      val msg = TeamCityMessageBuilder
         .testFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.location(testCase.source))
         .result(result)
         .build()
      println(msg)
   }

   /**
    * For a given [TestCase] will output the "test suite started" message.
    */
   private fun startTestSuite(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "startTestSuite ${testCase.descriptor.path().value}") }
      val msg = TeamCityMessageBuilder
         .testSuiteStarted(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .locationHint(Locations.location(testCase.source))
         .build()
      println(msg)
      started.add(testCase.descriptor)
   }

   /**
    * For a given [TestCase] will output the "test suite finished" message.
    */
   private fun finishTestSuite(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "finishTestSuite ${testCase.descriptor.path().value}") }
      val msg = TeamCityMessageBuilder
         .testSuiteFinished(prefix, formatter.format(testCase))
         .id(testCase.descriptor.path().value)
         .parent(testCase.descriptor.parent.path().value)
         .duration(result.duration)
         .locationHint(Locations.location(testCase.source))
         .result(result)
         .build()
      println(msg)
   }
}
