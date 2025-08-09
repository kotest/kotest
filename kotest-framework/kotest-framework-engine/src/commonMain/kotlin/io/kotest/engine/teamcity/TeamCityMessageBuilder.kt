package io.kotest.engine.teamcity

import io.kotest.assertions.KotestAssertionFailedError
import io.kotest.engine.test.TestResult
import kotlin.time.Duration

/**
 * [TeamCityMessageBuilder] generates team city style strings that intellij parses for test results.
 *
 * Message format:
 *
 * Reporting tests:
 * https://www.jetbrains.com/help/teamcity/service-messages.html#Reporting+Tests
 *
 * https://www.jetbrains.com/help/teamcity/service-messages.html
 * https://confluence.jetbrains.com/display/TCD10/Build+Script+Interaction+with+TeamCity
 * https://www.jetbrains.com/help/teamcity/build-script-interaction-with-teamcity.html
 * https://confluence.jetbrains.com/display/TCD65/Build+Script+Interaction+with+TeamCity#BuildScriptInteractionwithTeamCity-servMsgsServiceMessages
 *
 * Some message implementations:
 *
 * https://github.com/JetBrains/intellij-community/blob/master/plugins/testng/testSources/com/theoryinpractice/testng/configuration/TestNGTreeHierarchyTest.java
 * https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/junit4/JUnit4TestListener.java
 *
 * @param prefix Is the opening string used to signal that the line is a team city line.
 *               If unspecified this defaults to the team city format '##teamcity'.
 *               Can be overriden to help with testing.
 *
 * @param escapeColons team city uses colons in its format, and does not support colons inside messages properly,
 *                      and there is no escape, so we have to mangle colons if the output is going to the console.
 *                      See https://teamcity-support.jetbrains.com/hc/en-us/community/posts/206882875-Colons-in-test-service-messages-are-confusing-Team-City-
 */
class TeamCityMessageBuilder(
   prefix: String,
   messageName: String,
   private val escapeColons: Boolean = false
) {

   companion object {
      const val TEAM_CITY_PREFIX = "##teamcity"

      fun testReporterAttached(prefix: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_REPORTER_ATTACHED)
            .addAttribute(Attributes.DURATION_STRATEGY, "MANUAL")
      }

      fun testSuiteStarted(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_SUITE_STARTED).addAttribute(Attributes.NAME, name)
      }

      fun testSuiteFinished(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_SUITE_FINISHED).addAttribute(Attributes.NAME, name)
      }

      fun testStarted(name: String): TeamCityMessageBuilder = testStarted(TEAM_CITY_PREFIX, name)
      fun testStarted(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_STARTED).addAttribute(Attributes.NAME, name)
      }

      fun testFinished(name: String): TeamCityMessageBuilder = testFinished(TEAM_CITY_PREFIX, name)
      fun testFinished(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_FINISHED).addAttribute(Attributes.NAME, name)
      }

      // note it seems that not attaching a message renders test failed irrelevant
      fun testFailed(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_FAILED).addAttribute(Attributes.NAME, name)
      }

      fun testIgnored(name: String): TeamCityMessageBuilder = testIgnored(TEAM_CITY_PREFIX, name)
      fun testIgnored(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_IGNORED).addAttribute(Attributes.NAME, name)
      }
   }

   object Attributes {
      const val ACTUAL = "actual"
      const val EXPECTED = "expected"
      const val LOCATION_HINT = "locationHint"
      const val NAME = "name"
      const val DURATION = "duration"
      const val DURATION_STRATEGY = "durationStrategy"
      const val TYPE = "type"
      const val DETAILS = "details"
      const val MESSAGE = "message"
      const val PARENT_ID = "parent_id"
      const val ID = "id"
      const val RESULT_STATUS = "result_status"
   }

   object Messages {
      // https://youtrack.jetbrains.com/issue/IJPL-175931/set-duration-strategy-on-SMRootTestProxy
      const val TEST_REPORTER_ATTACHED = "enteredTheMatrix"
      const val TEST_SUITE_STARTED = "testSuiteStarted"
      const val TEST_SUITE_FINISHED = "testSuiteFinished"
      const val TEST_STARTED = "testStarted"
      const val TEST_FINISHED = "testFinished"
      const val TEST_IGNORED = "testIgnored"
      const val TEST_FAILED = "testFailed"
   }

   private val myText = StringBuilder(prefix).append("[$messageName")

   fun addAttribute(name: String, value: String): TeamCityMessageBuilder {
      myText
         .append(' ')
         .append(name).append("='")
         .append(Escaper.escapeForTeamCity(value))
         .append("'")
      return this
   }

   // message contains the textual representation of the error
   fun message(value: String?): TeamCityMessageBuilder =
      if (value != null) addAttribute(Attributes.MESSAGE, value.trim()) else this

   // details contains detailed information on the test failure, typically a message and an exception stacktrace
   fun details(value: String?): TeamCityMessageBuilder =
      if (value != null) addAttribute(Attributes.DETAILS, value.trim()) else this

   fun type(value: String): TeamCityMessageBuilder = addAttribute(Attributes.TYPE, value.trim())

   fun actual(value: String?): TeamCityMessageBuilder {
      if (value != null)
         addAttribute(Attributes.ACTUAL, value.trim())
      return this
   }

   fun expected(value: String?): TeamCityMessageBuilder {
      if (value != null)
         addAttribute(Attributes.EXPECTED, value.trim())
      return this
   }

   fun result(value: TestResult): TeamCityMessageBuilder = addAttribute(Attributes.RESULT_STATUS, value.name)

   fun locationHint(value: String?): TeamCityMessageBuilder =
      if (value != null) addAttribute(Attributes.LOCATION_HINT, value) else this

   // note it seems that test-failed messages require a message in order to be used
   fun withException(error: Throwable?, showDetails: Boolean = true): TeamCityMessageBuilder {
      if (error == null) return this

      val line1 = error.message?.trim()?.lines()?.firstOrNull()
      val message = if (line1.isNullOrBlank()) "Test failed" else line1
      message(escapeColons(message))

      if (showDetails) {
         // stackTraceToString fails if the error is created by a mocking framework, so we should catch
         val stacktrace: String = try {
            error.stackTraceToString()
         } catch (_: Exception) {
            "StackTrace unavailable (Sometimes caused by a mocked exception)"
         }
         details(escapeColons(stacktrace.take(4000))) // seems to be some limit to the details field
      }

      when (error) {
         is KotestAssertionFailedError -> type("comparisonFailure").expected(error.expected).actual(error.actual)
         else -> this.handlePlatformComparisonExceptions(error)
      }

      return this
   }

   // sets the test's parents id
   fun parent(value: String): TeamCityMessageBuilder = addAttribute(Attributes.PARENT_ID, value)

   // sets a unique parsable id for this test
   fun id(value: String): TeamCityMessageBuilder = addAttribute(Attributes.ID, value)

   // workaround for TC colon issue, see main javadoc
   private fun escapeColons(value: String) = when (escapeColons) {
      true -> value.replace(":", "\u02D0")
      false -> value
   }

   fun duration(duration: Duration): TeamCityMessageBuilder =
      addAttribute(Attributes.DURATION, duration.inWholeMilliseconds.toString())

   /**
    * Returns the completed string.
    */
   fun build(): String = "$myText]"
}

/**
 * Used to handle platform specific types, namely opentest4j on the JVM
 */
expect fun TeamCityMessageBuilder.handlePlatformComparisonExceptions(error: Throwable)
