package io.kotlintest.runner.console

class ServiceMessageBuilder(val command: String) {

  private val myText = StringBuilder("##teamcity[")

  fun addAttribute(name: String, value: String): ServiceMessageBuilder {
    myText.append(' ').append(name).append("='").append(value).append('\'')
    return this
  }

  internal fun ignoreComment(value: String): ServiceMessageBuilder = addAttribute("ignoreComment", value)
  internal fun message(value: String): ServiceMessageBuilder = addAttribute("message", value)
  internal fun locationHint(value: String): ServiceMessageBuilder = addAttribute("locationHint", value)

  override fun toString(): String = "$myText]"

  companion object {


    fun testSuiteStarted(name: String): ServiceMessageBuilder {
      return ServiceMessageBuilder(ServiceMessageTypes.TEST_SUITE_STARTED).addAttribute("name", name)
    }

    fun testSuiteFinished(name: String): ServiceMessageBuilder {
      return ServiceMessageBuilder(ServiceMessageTypes.TEST_SUITE_FINISHED).addAttribute("name", name)
    }

    fun testStarted(name: String): ServiceMessageBuilder {
      return ServiceMessageBuilder(ServiceMessageTypes.TEST_STARTED).addAttribute("name", name)
    }

    fun testFinished(name: String): ServiceMessageBuilder {
      return ServiceMessageBuilder(ServiceMessageTypes.TEST_FINISHED).addAttribute("name", name)
    }

    fun testStdOut(name: String): ServiceMessageBuilder {
      return ServiceMessageBuilder(ServiceMessageTypes.TEST_STD_OUT).addAttribute("name", name)
    }

    fun testStdErr(name: String): ServiceMessageBuilder {
      return ServiceMessageBuilder(ServiceMessageTypes.TEST_STD_ERR).addAttribute("name", name)
    }

    fun testFailed(name: String): ServiceMessageBuilder {
      return ServiceMessageBuilder(ServiceMessageTypes.TEST_FAILED).addAttribute("name", name)
    }

    fun testIgnored(name: String): ServiceMessageBuilder {
      return ServiceMessageBuilder(ServiceMessageTypes.TEST_IGNORED).addAttribute("name", name)
    }
  }
}