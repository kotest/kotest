package io.kotest.engine.teamcity

import io.kotest.common.errors.ComparisonError

/**
 * Creates a TeamCity message builder to be used for a single string.
 *
 * Message format:
 *
 * https://www.jetbrains.com/help/teamcity/service-messages.html
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
 */
class TeamCityMessageBuilder(prefix: String, messageName: String) {

   companion object {
      const val TeamCityPrefix = "##teamcity"

      fun testSuiteStarted(name: String): TeamCityMessageBuilder = testSuiteStarted(TeamCityPrefix, name)
      fun testSuiteStarted(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_SUITE_STARTED).addAttribute(Attributes.NAME, name)
      }

      fun testSuiteFinished(name: String): TeamCityMessageBuilder = testSuiteFinished(TeamCityPrefix, name)
      fun testSuiteFinished(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_SUITE_FINISHED).addAttribute(Attributes.NAME, name)
      }

      fun testStarted(name: String): TeamCityMessageBuilder = testStarted(TeamCityPrefix, name)
      fun testStarted(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_STARTED).addAttribute(Attributes.NAME, name)
      }

      fun testFinished(name: String): TeamCityMessageBuilder = testFinished(TeamCityPrefix, name)
      fun testFinished(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_FINISHED).addAttribute(Attributes.NAME, name)
      }

      fun testStdOut(name: String): TeamCityMessageBuilder = testStdOut(TeamCityPrefix, name)
      fun testStdOut(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_STD_OUT).addAttribute(Attributes.NAME, name)
      }

      fun testStdErr(name: String): TeamCityMessageBuilder = testStdErr(TeamCityPrefix, name)
      fun testStdErr(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_STD_ERR).addAttribute(Attributes.NAME, name)
      }

      fun testFailed(name: String): TeamCityMessageBuilder = testFailed(TeamCityPrefix, name)
      fun testFailed(prefix: String, name: String): TeamCityMessageBuilder {
         return TeamCityMessageBuilder(prefix, Messages.TEST_FAILED).addAttribute(Attributes.NAME, name)
      }

      fun testIgnored(name: String): TeamCityMessageBuilder = testIgnored(TeamCityPrefix, name)
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
      const val TYPE = "type"
      const val DETAILS = "details"
      const val MESSAGE = "message"
      const val PARENT_ID = "parent_id"
      const val ID = "id"
      const val TEST_TYPE = "test_type"
      const val RESULT_STATUS = "result_status"
   }

   object Messages {
      const val TEST_SUITE_STARTED = "testSuiteStarted"
      const val TEST_SUITE_FINISHED = "testSuiteFinished"
      const val TEST_STARTED = "testStarted"
      const val TEST_FINISHED = "testFinished"
      const val TEST_IGNORED = "testIgnored"
      const val TEST_STD_OUT = "testStdOut"
      const val TEST_STD_ERR = "testStdErr"
      const val TEST_FAILED = "testFailed"
   }

   private val myText = StringBuilder(prefix ?: "##teamcity").append("[$messageName")

   fun addAttribute(name: String, value: String): TeamCityMessageBuilder {
      myText
         .append(' ')
         .append(name).append("='")
         .append(Escaper.escapeForTeamCity(value))
         .append("'")
      return this
   }

   fun message(value: String?): TeamCityMessageBuilder =
      if (value != null) addAttribute(Attributes.MESSAGE, value.trim()) else this

   fun details(value: String?): TeamCityMessageBuilder =
      if (value != null) addAttribute(Attributes.DETAILS, value.trim()) else this

   fun type(value: String): TeamCityMessageBuilder = addAttribute(Attributes.TYPE, value.trim())
   fun actual(value: String): TeamCityMessageBuilder = addAttribute(Attributes.ACTUAL, value.trim())
   fun expected(value: String): TeamCityMessageBuilder = addAttribute(Attributes.EXPECTED, value.trim())
   fun locationHint(value: String): TeamCityMessageBuilder = addAttribute(Attributes.LOCATION_HINT, value)
   fun resultStatus(value: String): TeamCityMessageBuilder = addAttribute(Attributes.RESULT_STATUS, value)

   fun withException(error: Throwable?): TeamCityMessageBuilder {
      if (error == null) return this

      val line1 = error.message?.lines()?.firstOrNull()
      val message = if (line1.isNullOrBlank()) "Test failed" else line1
      message(message)

      details(error.stackTraceToString())

      when (error) {
         is ComparisonError -> type("comparisonFailure").actual(error.actualValue).expected(error.expectedValue)
      }

      return this
   }

   // sets the test's parents id
   fun parent(value: String): TeamCityMessageBuilder = addAttribute(Attributes.PARENT_ID, value)

   // sets a unique parsable id for this test
   fun id(value: String): TeamCityMessageBuilder = addAttribute(Attributes.ID, value)

   // adds a test-type flag to the message, indicating if this event is for a spec, container or test
   fun testType(value: String): TeamCityMessageBuilder = addAttribute(Attributes.TEST_TYPE, value.trim().lowercase())

   fun container() = testType("container")
   fun spec() = testType("spec")
   fun test() = testType("test")

   fun duration(durationInMillis: Long): TeamCityMessageBuilder =
      addAttribute(Attributes.DURATION, durationInMillis.toString())

   /**
    * Returns the completed string.
    */
   fun build(): String = "$myText]"
}
