package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.teamcity.TeamCityMessage
import io.kotest.engine.teamcity.TeamCityPathRenderer
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.names.DisplayNameFormatting
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that logs events to the console using a [TeamCityMessage].
 *
 * Notes (From Gemini):
 *
 * To nest test suites in the TeamCity UI, you primarily use the testSuiteStarted and testSuiteFinished messages.
 * The hierarchy is determined by the order of messages rather than a parent_id attribute.
 *
 * For sequential tests, TeamCity nests anything between a "Start" and "Finish" message.
 * You create a hierarchy simply by wrapping one suite inside another.
 *
 * Observations:
 *
 * Native and JS ignore containers without direct tests
 * IntelliJ will parse out a test path on periods assuming they are FQNs with classes.
 *
 * Decisions:
 *
 * Intermediate containers will only be output if configured
 * All tests will be output as root tests under the spec as a suite, with the path flattened.
 *
 */
@KotestInternal
class TeamCityTestEngineListener(
   private val prefix: String = TeamCityMessage.TEAM_CITY_PREFIX,
   private val details: Boolean = true,
) : TestEngineListener {

   private val logger = Logger(TeamCityTestEngineListener::class)
   private var renderer = TeamCityPathRenderer(DisplayNameFormatting(null))
   private val results = mutableMapOf<Descriptor, TestResult>()


   override suspend fun engineStarted() {
      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_REPORTER_ATTACHED) {}.output()
   }

   override suspend fun engineInitialized(context: TestEngineInitializedContext) {
      renderer = TeamCityPathRenderer(DisplayNameFormatting(context.projectConfig))
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) {
         t.forEach { insertPlaceholderTest(it::class.simpleName ?: "Error", it) }
      }
   }

   override suspend fun specStarted(ref: SpecRef) {
      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_SUITE_STARTED) {
         name(renderer.testPath(ref))
      }.output()
   }

   // ignored specs are completely hidden from output in team city
   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {}

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {

      // if the spec itself has an error, we must insert a placeholder test
      when (val t = result.errorOrNull) {
         null -> Unit
         is MultipleExceptions -> t.causes.forEach { insertPlaceholderTest(renderer.testPath(ref, it), it) }
         else -> insertPlaceholderTest(renderer.testPath(ref, t), t)
      }

      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_SUITE_FINISHED) {
         name(renderer.testPath(ref))
      }.output()

      results.clear()
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.name, "testStarted $testCase") }
      if (testCase.type == TestType.Test)
         TeamCityMessage(prefix, TeamCityMessage.Types.TEST_STARTED) {
            name(renderer.testPath(testCase))
         }.output()
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_IGNORED) {
         name(testCase.descriptor.path().value)
         message(reason)
         result(TestResult.Ignored(reason))
      }.output()
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.name, "testFinished $testCase") }
      results[testCase.descriptor] = result

      if (testCase.type == TestType.Container)
         failTestSuiteIfError(testCase, result)
      else {
         if (result.isErrorOrFailure) {
            TeamCityMessage(prefix, TeamCityMessage.Types.TEST_FAILED) {
               name(renderer.testPath(testCase))
               exception(result.errorOrNull, details)
               result(result)
            }.output()
         }

         TeamCityMessage(prefix, TeamCityMessage.Types.TEST_FINISHED) {
            name(renderer.testPath(testCase))
            duration(result.duration)
            result(result)
         }.output()
      }
   }


   private fun failTestSuiteIfError(testCase: TestCase, result: TestResult) {
      // test suites cannot be in a failed state, so we must insert a placeholder to hold any error
      when (val t = result.errorOrNull) {
         null -> Unit
         is MultipleExceptions -> t.causes.forEach { insertPlaceholderTest(renderer.testPath(testCase, it), it) }
         else -> insertPlaceholderTest(renderer.testPath(testCase, t), t)
      }
   }

   // intellij has no method for failed suites, so if a container or spec fails, we must insert
   // a dummy "test" to tag the error against that
   private fun insertPlaceholderTest(testPath: String, t: Throwable) {

      val (name, cause) = ExtensionExceptionExtractor.resolve(t)

      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_STARTED) {
         name(testPath)
      }.output()

      // we must print out the stack trace in between the dummy, so it appears when you click on the test name
      cause.printStackTrace()

      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_FAILED) {
         name(testPath)
      }.output()

      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_FINISHED) {
         name(testPath)
      }.output()
   }
}
