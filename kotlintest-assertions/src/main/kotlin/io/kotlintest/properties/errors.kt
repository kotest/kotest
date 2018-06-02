package io.kotlintest.properties

import convertValueToString

fun propertyTestFailureMessage(attempt: Int,
                               inputs: List<PropertyFailureInput<out Any?>>,
                               cause: AssertionError): String {
  val sb = StringBuilder()
  sb.appendln("Property failed for")
  inputs.withIndex().forEach {
    val input = if (it.value.shrunk == it.value.original) {
      "Arg ${it.index}: ${convertValueToString(it.value.shrunk)}"
    } else {
      "Arg ${it.index}: ${convertValueToString(it.value.shrunk)} (shrunk from ${it.value.original})"
    }
    sb.appendln(input)
  }
  sb.appendln("after $attempt attempts")
  sb.append("Caused by: ${cause.message?.trim()}")
  return sb.toString()
}

data class PropertyFailureInput<T>(val original: T?, val shrunk: T?)

class PropertyAssertionError(val e: AssertionError,
                             val attempt: Int,
                             val inputs: List<PropertyFailureInput<out Any?>>) : AssertionError(propertyTestFailureMessage(attempt, inputs, e), e)