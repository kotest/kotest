package io.kotlintest.runner.console

class TeamCityMessageBuilder(command: String) {


  private val myText = StringBuilder("##teamcity[$command")

  fun addAttribute(name: String, value: String): TeamCityMessageBuilder {
    myText.append(' ').append(name).append("='").append(value).append('\'')
    return this
  }

  internal fun ignoreComment(value: String): TeamCityMessageBuilder = addAttribute("ignoreComment", value)
  internal fun message(value: String): TeamCityMessageBuilder = addAttribute("message", value)
  internal fun locationHint(value: String): TeamCityMessageBuilder = addAttribute("locationHint", value)

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

    fun testSuiteStarted(name: String): TeamCityMessageBuilder {
      return TeamCityMessageBuilder(TEST_SUITE_STARTED).addAttribute("name", name)
    }

    fun testSuiteFinished(name: String): TeamCityMessageBuilder {
      return TeamCityMessageBuilder(TEST_SUITE_FINISHED).addAttribute("name", name)
    }

    fun testStarted(name: String): TeamCityMessageBuilder {
      return TeamCityMessageBuilder(TEST_STARTED).addAttribute("name", name)
    }

    fun testFinished(name: String): TeamCityMessageBuilder {
      return TeamCityMessageBuilder(TEST_FINISHED).addAttribute("name", name)
    }

    fun testStdOut(name: String): TeamCityMessageBuilder {
      return TeamCityMessageBuilder(TEST_STD_OUT).addAttribute("name", name)
    }

    fun testStdErr(name: String): TeamCityMessageBuilder {
      return TeamCityMessageBuilder(TEST_STD_ERR).addAttribute("name", name)
    }

    fun testFailed(name: String): TeamCityMessageBuilder {
      return TeamCityMessageBuilder(TEST_FAILED).addAttribute("name", name)
    }

    fun testIgnored(name: String): TeamCityMessageBuilder {
      return TeamCityMessageBuilder(TEST_IGNORED).addAttribute("name", name)
    }
  }
}