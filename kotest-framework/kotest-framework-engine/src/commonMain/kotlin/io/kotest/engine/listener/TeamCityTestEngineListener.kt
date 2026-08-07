package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.errors.ExtensionExceptionExtractor
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.teamcity.Locations
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
 * By default, intermediate containers are not output as suites: all tests are output as root
 * tests under the spec as a suite, with the path flattened. This is because some TeamCity
 * consumers (e.g. Native and JS) ignore containers without direct tests, so flattening guarantees
 * every test remains visible.
 *
 * When [nestContainers] is enabled, containers are instead reported as nested
 * testSuiteStarted/testSuiteFinished messages, and test names are not flattened. This gives a
 * consumer capable of rendering nested suites (e.g. IntelliJ parsing these messages directly from
 * a forked JVM, as our IJ plugin does for non-Gradle run configurations) a real tree instead of one flattened
 * leaf per test. This is opt-in so that existing consumers relying on the flattened format are
 * unaffected.
 *
 */
@KotestInternal
class TeamCityTestEngineListener(
   private val prefix: String = TeamCityMessage.TEAM_CITY_PREFIX,
   private val nestContainers: Boolean = false,
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
         locationHint(Locations.location(ref))
      }.output()
   }

   // ignored specs are completely hidden from output in team city
   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {}

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {

      // if the spec itself has an error, we must insert a placeholder test
      when (val t = result.errorOrNull) {
         null -> Unit
         is MultipleExceptions -> t.causes.forEach {
            insertPlaceholderTest(if (nestContainers) it::class.simpleName ?: "Error" else renderer.testPath(ref, it), it)
         }
         else -> insertPlaceholderTest(if (nestContainers) t::class.simpleName ?: "Error" else renderer.testPath(ref, t), t)
      }

      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_SUITE_FINISHED) {
         name(renderer.testPath(ref))
      }.output()

      results.clear()
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.name, "testStarted $testCase") }
      when {
         nestContainers && testCase.type == TestType.Container ->
            TeamCityMessage(prefix, TeamCityMessage.Types.TEST_SUITE_STARTED) {
               name(renderer.localName(testCase))
               locationHint(Locations.location(testCase.source))
            }.output()
         testCase.type == TestType.Test ->
            TeamCityMessage(prefix, TeamCityMessage.Types.TEST_STARTED) {
               name(if (nestContainers) renderer.localName(testCase) else renderer.testPath(testCase))
               locationHint(Locations.location(testCase.source))
            }.output()
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_IGNORED) {
         name(if (nestContainers) renderer.localName(testCase) else renderer.testPath(testCase))
         locationHint(Locations.location(testCase.source))
         message(reason)
         result(TestResult.Ignored(reason))
      }.output()
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.name, "testFinished $testCase") }
      results[testCase.descriptor] = result

      if (testCase.type == TestType.Container) {
         failTestSuiteIfError(testCase, result)
         if (nestContainers)
            TeamCityMessage(prefix, TeamCityMessage.Types.TEST_SUITE_FINISHED) {
               name(renderer.localName(testCase))
            }.output()
      } else {
         val testName = if (nestContainers) renderer.localName(testCase) else renderer.testPath(testCase)

         if (result.isErrorOrFailure) {
            TeamCityMessage(prefix, TeamCityMessage.Types.TEST_FAILED) {
               name(testName)
               exception(result.errorOrNull)
               result(result)
            }.output()
         }

         TeamCityMessage(prefix, TeamCityMessage.Types.TEST_FINISHED) {
            name(testName)
            duration(result.duration)
            result(result)
         }.output()
      }
   }


   private fun failTestSuiteIfError(testCase: TestCase, result: TestResult) {
      // test suites cannot be in a failed state, so we must insert a placeholder to hold any error
      when (val t = result.errorOrNull) {
         null -> Unit
         is MultipleExceptions -> t.causes.forEach {
            insertPlaceholderTest(if (nestContainers) it::class.simpleName ?: "Error" else renderer.testPath(testCase, it), it)
         }
         else -> insertPlaceholderTest(if (nestContainers) t::class.simpleName ?: "Error" else renderer.testPath(testCase, t), t)
      }
   }

   // intellij has no method for failed suites, so if a container or spec fails, we must insert
   // a dummy "test" to tag the error against that
   private fun insertPlaceholderTest(testPath: String, t: Throwable) {

      val (_, cause) = ExtensionExceptionExtractor.resolve(t)

      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_STARTED) {
         name(testPath)
      }.output()

      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_FAILED) {
         name(testPath)
         exception(cause)
      }.output()

      TeamCityMessage(prefix, TeamCityMessage.Types.TEST_FINISHED) {
         name(testPath)
      }.output()
   }
}
