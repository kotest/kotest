package io.kotest.runner.console

import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Message format:
 * https://confluence.jetbrains.com/display/TCD65/Build+Script+Interaction+with+TeamCity#BuildScriptInteractionwithTeamCity-servMsgsServiceMessages
 *
 * Some message implementations:
 *
 * https://github.com/JetBrains/intellij-community/blob/master/plugins/testng/testSources/com/theoryinpractice/testng/configuration/TestNGTreeHierarchyTest.java
 * https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/junit4/JUnit4TestListener.java
 */
class TeamCityMessages(command: String) {

   private val myText = StringBuilder("##teamcity[$command")

   fun addAttribute(name: String, value: String): TeamCityMessages {
      myText.append(' ').append(name).append("='").append(value).append('\'')
      return this
   }

   internal fun ignoreComment(value: String): TeamCityMessages = addAttribute("ignoreComment", value)
   internal fun message(value: String): TeamCityMessages = addAttribute("message", value.trim())
   internal fun locationHint(value: String): TeamCityMessages = addAttribute("locationHint", value)

   @OptIn(ExperimentalTime::class)
   internal fun duration(duration: Duration): TeamCityMessages =
      addAttribute("duration", duration.toLongMilliseconds().toString())

   override fun toString(): String = "$myText]"

   companion object {

      private const val TEST_SUITE_STARTED = "testSuiteStarted"
      private const val TEST_SUITE_FINISHED = "testSuiteFinished"
      private const val TEST_STARTED = "testStarted"
      private const val TEST_FINISHED = "testFinished"
      private const val TEST_IGNORED = "testIgnored"
      private const val TEST_STD_OUT = "testStdOut"
      private const val TEST_STD_ERR = "testStdErr"
      private const val TEST_FAILED = "testFailed"

      fun testSuiteStarted(name: String): TeamCityMessages {
         return TeamCityMessages(TEST_SUITE_STARTED).addAttribute("name", name)
      }

      fun testSuiteFinished(name: String): TeamCityMessages {
         return TeamCityMessages(TEST_SUITE_FINISHED).addAttribute("name", name)
      }

      fun testStarted(name: String): TeamCityMessages {
         return TeamCityMessages(TEST_STARTED).addAttribute("name", name)
      }

      fun testFinished(name: String): TeamCityMessages {
         return TeamCityMessages(TEST_FINISHED).addAttribute("name", name)
      }

      fun testStdOut(name: String): TeamCityMessages {
         return TeamCityMessages(TEST_STD_OUT).addAttribute("name", name)
      }

      fun testStdErr(name: String): TeamCityMessages {
         return TeamCityMessages(TEST_STD_ERR).addAttribute("name", name)
      }

      fun testFailed(name: String): TeamCityMessages {
         return TeamCityMessages(TEST_FAILED).addAttribute("name", name)
      }

      fun testIgnored(name: String): TeamCityMessages {
         return TeamCityMessages(TEST_IGNORED).addAttribute("name", name)
      }
   }
}
