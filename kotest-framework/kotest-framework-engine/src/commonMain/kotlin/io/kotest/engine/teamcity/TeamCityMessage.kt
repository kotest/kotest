package io.kotest.engine.teamcity

import io.kotest.engine.test.TestResult
import kotlin.time.Duration

/**
 * [TeamCityMessage] generates team city style strings that intellij parses for test results.
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
 * @param prefix the opening string used to signal that the output is a team city service message.
 *               The default required by test infrastructure is '##teamcity'.
 *               Can be overriden to help with testing.
 *
 * @param escapeColons team city uses colons in its format and does not support colons inside messages properly,
 *                      and there is no escape, so we have to mangle colons if the output is going to the console.
 *                      See https://teamcity-support.jetbrains.com/hc/en-us/community/posts/206882875-Colons-in-test-service-messages-are-confusing-Team-City-
 */
class TeamCityMessage(
   private val prefix: String,
   private val type: String,
   private val escapeColons: Boolean = false,
   builder: TeamCityMessage.() -> Unit,
) {

   companion object {
      const val TEAM_CITY_PREFIX = "##teamcity"
   }

   object Attributes {
      const val ACTUAL = "actual"
      const val EXPECTED = "expected"
      const val NAME = "name"
      const val OUT = "out"
      const val DURATION = "duration"
      const val TYPE = "type"
      const val DETAILS = "details"
      const val MESSAGE = "message"
      const val RESULT_STATUS = "result_status"
   }

   object Types {
      // https://youtrack.jetbrains.com/issue/IJPL-175931/set-duration-strategy-on-SMRootTestProxy
      const val TEST_REPORTER_ATTACHED = "enteredTheMatrix"
      const val TEST_SUITE_STARTED = "testSuiteStarted"
      const val TEST_SUITE_FINISHED = "testSuiteFinished"
      const val TEST_STARTED = "testStarted"
      const val TEST_FINISHED = "testFinished"
      const val TEST_IGNORED = "testIgnored"
      const val TEST_FAILED = "testFailed"
   }

   private val attributes = mutableListOf<Pair<String, String>>()

   init {
      builder(this)
   }

   fun attribute(name: String, value: String) {
      attributes.add(Pair(name, value))
   }

   // message contains the textual representation of the error
   fun message(value: String?) {
      if (value != null) attribute(Attributes.MESSAGE, value.trim())
   }

   fun name(name: String) {
      attribute(Attributes.NAME, name.trim())
   }

   fun out(out: String) {
      return attribute(Attributes.OUT, escapeColonsIn(out))
   }

   /**
    * The details attribute contains detailed information on the test failure,
    * typically a message and an exception stacktrace.
    */
   fun details(value: String?) =
      if (value != null) attribute(Attributes.DETAILS, value.trim()) else this

   fun type(value: String) = attribute(Attributes.TYPE, value.trim())

   fun actual(value: String?) {
      if (value != null)
         attribute(Attributes.ACTUAL, value.trim())
   }

   fun expected(value: String?) {
      if (value != null)
         attribute(Attributes.EXPECTED, value.trim())
   }

   fun result(value: TestResult) = attribute(Attributes.RESULT_STATUS, value.name)

   // note it seems that test-failed messages require a message to be included
   fun exception(error: Throwable?, showDetails: Boolean = true): TeamCityMessage {
      if (error == null) return this

      val line1 = error.message?.trim()?.lines()?.firstOrNull()
      val message = if (line1.isNullOrBlank()) "Test failed" else line1
      message(escapeColonsIn(message))

      if (showDetails) {
         // stackTraceToString fails if the error is created by a mocking framework, so we should catch
         val stacktrace: String = try {
            error.stackTraceToString()
         } catch (_: Exception) {
            "StackTrace unavailable (Sometimes caused by a mocked exception)"
         }
         details(escapeColonsIn(stacktrace)) // seems to be some limit to the details field
      }

      return this
   }

   // workaround for TC colon issue, see main Javadoc
   private fun escapeColonsIn(value: String) = when (escapeColons) {
      true -> value.replace(":", "\u02D0")
      false -> value
   }

   fun duration(duration: Duration) =
      attribute(Attributes.DURATION, duration.inWholeMilliseconds.toString())

   /**
    * Returns the completed string.
    */
   fun build(): String {
      return buildString {
         append(prefix)
         append("[$type")
         attributes.forEach { (name, value) ->
            append(' ')
            append(name).append("='")
            append(Escaper.escapeForTeamCity(value))
            append("'")
         }
         append("]")
      }
   }

   fun output() {
      println(build())
   }
}
