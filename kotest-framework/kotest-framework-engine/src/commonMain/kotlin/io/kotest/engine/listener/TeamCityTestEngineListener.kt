package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.teamcity.TeamCityMessageBuilder
import io.kotest.engine.teamcity.TeamCityWriter
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import io.kotest.core.Logger
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
   // a suite or not, and so this map marks all tests that have children
   private val parents = mutableSetOf<TestCase>()

   private val started = mutableSetOf<Descriptor.TestDescriptor>()

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
      parents.clear()
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "testStarted $testCase") }

      val p = testCase.parent

      // we can start the parent right now if it has not been started, since we know for sure its a suite now
      // we could start it later, but this means it'll appear in the test window sooner, which is a bit nicer
      if (p != null) {

         // we know any parent of this test obviously has a child (this is the child)
         // we use that information later to know the parent should be marked as a suite
         parents.add(p)

         if (!started.contains(p.descriptor)) {
            writer.outputTestSuiteStarted(p)
            started.add(p.descriptor)
         }
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      writer.outputTestIgnored(testCase, TestResult.Ignored(reason))
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "testFinished $testCase") }

      // check if this test ended up being a suite or test
      if (parents.contains(testCase)) {
         // if a suite, it would have been marked started by the first child so we just need to finish it
         failTestSuiteIfError(testCase, result)
         writer.outputTestSuiteFinished(testCase, result)
      } else {
         // test would not have been started yet if it had no children
         if (!started.contains(testCase.descriptor)) {
            writer.outputTestStarted(testCase)
            started.add(testCase.descriptor)
         }
         if (result.isErrorOrFailure) writer.outputTestFailed(testCase, result, details)
         writer.outputTestFinished(testCase, result)
      }
   }

   private fun failTestSuiteIfError(testCase: TestCase, result: TestResult) {
      // test suites don't support a failed state, so we must insert a placeholder test to hold any error
      when (val t = result.errorOrNull) {
         null -> Unit
         is MultipleExceptions -> t.causes.forEach { insertPlaceholder(it, testCase.descriptor) }
         else -> insertPlaceholder(t, testCase.descriptor)
      }
   }

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
}
