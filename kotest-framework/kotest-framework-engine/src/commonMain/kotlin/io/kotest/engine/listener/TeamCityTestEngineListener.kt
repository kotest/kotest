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
import io.kotest.engine.teamcity.TeamCityWriter
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

   private var writer = TeamCityWriter(prefix, FallbackDisplayNameFormatter.default(ProjectConfiguration()))

   // once a spec has completed, we want to be able to check whether any given test is
   // a container or a leaf test, and so this map contains all test that have children
   private val children = mutableMapOf<Descriptor, MutableList<TestCase>>()

   private val results = mutableMapOf<Descriptor, TestResult>()

   private val started = mutableSetOf<Descriptor.TestDescriptor>()

   // intellij has no method for failed suites, so if a container or spec fails we must insert
   // a dummy "test" in order to tag the error against that
   private fun insertPlaceholder(t: Throwable, parent: Descriptor) {

      val (name, cause) = ExtensionExceptionExtractor.resolve(t)

      writer.outputTestStarted(name, parent.path().value)
      // we must print out the stack trace in between the dummy, so it appears when you click on the test name
      //t?.printStackTrace()
      writer.outputTestFailed(name, cause, details, parent.path().value)
      writer.outputTestFinished(name, parent.path().value)
   }

   override suspend fun engineStarted() {}

   override suspend fun engineInitialized(context: EngineContext) {
      writer = TeamCityWriter(
         prefix,
         getFallbackDisplayNameFormatter(context.configuration.registry, context.configuration)
      )
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) {
         t.withIndex().forEach { (index, error) ->
            val testName = if (t.size == 1) "Engine exception" else "Engine exception ${index + 1}"
            val message = error.message ?: t::class.bestName()
            writer.outputTestStarted(testName)
            writer.outputTestFailed(testName, message)
            writer.outputTestFinished(testName)
         }
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      writer.outputTestSuiteStarted(kclass)
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

      writer.outputTestSuiteFinished(kclass)
      results.clear()
      children.clear()
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "testStarted $testCase") }
      if (testCase.parent != null) addChild(testCase)
      when (testCase.type) {
         TestType.Container -> {
            val p = testCase.parent
            // we might have a container inside a dynamic parent, in which case we need to start the dynamic parent
            if (p != null && p.type == TestType.Dynamic) {
               if (!started.contains(p.descriptor)) {
                  writer.outputTestSuiteStarted(p)
                  started.add(p.descriptor)
               }
            }
            writer.outputTestSuiteStarted(testCase)
            started.add(testCase.descriptor)
         }

         TestType.Test -> {
            val p = testCase.parent
            // we might have a container inside a dynamic parent, in which case we need to start it
            if (p != null && p.type == TestType.Dynamic) {
               if (!started.contains(p.descriptor)) {
                  writer.outputTestSuiteStarted(p)
                  started.add(p.descriptor)
               }
            }
            writer.outputTestStarted(testCase)
            started.add(testCase.descriptor)
         }

         TestType.Dynamic -> {
            val p = testCase.parent
            // we might have a dynamic inside another dynamic parent, in which case we need to start the dynamic parent
            if (p != null && p.type == TestType.Dynamic) {
               if (!started.contains(p.descriptor)) {
                  writer.outputTestSuiteStarted(p)
                  started.add(p.descriptor)
               }
            }
         }
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      writer.outputTestIgnored(testCase, TestResult.Ignored(reason))
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
            writer.outputTestSuiteFinished(testCase, result)
         }

         TestType.Test -> {
            if (!started.contains(testCase.descriptor)) writer.outputTestStarted(testCase)
            if (result.isErrorOrFailure) writer.outputTestFailed(testCase, result, details)
            writer.outputTestFinished(testCase, result)
         }

         TestType.Dynamic -> {
            if (isParent(testCase)) {
               if (!started.contains(testCase.descriptor)) writer.outputTestSuiteStarted(testCase)
               failTestSuiteIfError(testCase, result)
               writer.outputTestSuiteFinished(testCase, result)
            } else {
               if (!started.contains(testCase.descriptor)) writer.outputTestStarted(testCase)
               if (result.isErrorOrFailure) writer.outputTestFailed(testCase, result, details)
               writer.outputTestFinished(testCase, result)
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


}
