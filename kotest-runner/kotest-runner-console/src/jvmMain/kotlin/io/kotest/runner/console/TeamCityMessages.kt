package io.kotest.runner.console

import java.io.PrintWriter
import java.io.StringWriter
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Message format:
 *
 * https://www.jetbrains.com/help/teamcity/build-script-interaction-with-teamcity.html
 * https://confluence.jetbrains.com/display/TCD65/Build+Script+Interaction+with+TeamCity#BuildScriptInteractionwithTeamCity-servMsgsServiceMessages
 *
 * Some message implementations:
 *
 * https://github.com/JetBrains/intellij-community/blob/master/plugins/testng/testSources/com/theoryinpractice/testng/configuration/TestNGTreeHierarchyTest.java
 * https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/junit4/JUnit4TestListener.java
 */
class TeamCityMessages(prefix: String?, command: String) {

   private val myText = StringBuilder(prefix ?: "##teamcity").append("[$command")

   fun addAttribute(name: String, value: String): TeamCityMessages {
      myText
         .append(' ')
         .append(name).append("='")
         .append(value.replace("[", "|[").replace("]", "|]").replace("'", "|'").replace("\n".toRegex(), "|n"))
         .append("'")
      return this
   }

   internal fun ignoreComment(value: String): TeamCityMessages = addAttribute("ignoreComment", value)
   internal fun message(value: String): TeamCityMessages = addAttribute("message", value.trim())
   private fun details(value: String): TeamCityMessages = addAttribute("details", value.trim())
   internal fun type(value: String): TeamCityMessages = addAttribute("type", value.trim())
   internal fun actual(value: String): TeamCityMessages = addAttribute("actual", value.trim())
   internal fun expected(value: String): TeamCityMessages = addAttribute("expected", value.trim())
   internal fun locationHint(value: String): TeamCityMessages = addAttribute("locationHint", value)

   @OptIn(ExperimentalTime::class)
   internal fun duration(duration: Duration): TeamCityMessages =
      addAttribute("duration", duration.toLongMilliseconds().toString())

   override fun toString(): String = "$myText]"

   fun withException(error: Throwable?): TeamCityMessages {
      if (error == null) return this

      val line1 = error.message?.lines()?.firstOrNull()
      val message = if (line1.isNullOrBlank()) "Test failed" else line1
      message(message)

      error.stackTrace?.let {
         val writer = StringWriter()
         error.printStackTrace(PrintWriter(writer))
         val stack = writer.buffer.toString()
         details(stack)
      }

      when (error) {
         is org.opentest4j.AssertionFailedError ->
            if (error.isActualDefined && error.isExpectedDefined) {
               type("comparisonFailure")
                  .actual(error.actual.stringRepresentation)
                  .expected(error.expected.stringRepresentation)
            }
      }
      return this
   }

   companion object {

      private const val TEST_SUITE_STARTED = "testSuiteStarted"
      private const val TEST_SUITE_FINISHED = "testSuiteFinished"
      private const val TEST_STARTED = "testStarted"
      private const val TEST_FINISHED = "testFinished"
      private const val TEST_IGNORED = "testIgnored"
      private const val TEST_STD_OUT = "testStdOut"
      private const val TEST_STD_ERR = "testStdErr"
      private const val TEST_FAILED = "testFailed"

      fun testSuiteStarted(prefix: String?, name: String): TeamCityMessages {
         return TeamCityMessages(prefix, TEST_SUITE_STARTED).addAttribute("name", name)
      }

      fun testSuiteFinished(prefix: String?, name: String): TeamCityMessages {
         return TeamCityMessages(prefix, TEST_SUITE_FINISHED).addAttribute("name", name)
      }

      fun testStarted(prefix: String?, name: String): TeamCityMessages {
         return TeamCityMessages(prefix, TEST_STARTED).addAttribute("name", name)
      }

      fun testFinished(prefix: String?, name: String): TeamCityMessages {
         return TeamCityMessages(prefix, TEST_FINISHED).addAttribute("name", name)
      }

      fun testStdOut(prefix: String?, name: String): TeamCityMessages {
         return TeamCityMessages(prefix, TEST_STD_OUT).addAttribute("name", name)
      }

      fun testStdErr(prefix: String?, name: String): TeamCityMessages {
         return TeamCityMessages(prefix, TEST_STD_ERR).addAttribute("name", name)
      }

      fun testFailed(prefix: String?, name: String): TeamCityMessages {
         return TeamCityMessages(prefix, TEST_FAILED).addAttribute("name", name)
      }

      fun testIgnored(prefix: String?, name: String): TeamCityMessages {
         return TeamCityMessages(prefix, TEST_IGNORED).addAttribute("name", name)
      }
   }
}
