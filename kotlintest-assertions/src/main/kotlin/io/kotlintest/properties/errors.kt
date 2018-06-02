package io.kotlintest.properties

import convertValueToString

fun propertyTestFailureMessage(attempt: Int,
                               inputs: List<PropertyFailureInput<out Any?>>,
                               cause: AssertionError): String {
  val sb = StringBuilder()
  sb.append("Property failed for")
  sb.append("\n")
  inputs.withIndex().forEach {
    val input = if (it.value.shrunk == it.value.original) {
      "Arg ${it.index}: ${convertValueToString(it.value.shrunk)}"
    } else {
      "Arg ${it.index}: ${convertValueToString(it.value.shrunk)} (shrunk from ${it.value.original})"
    }
    sb.append(input)
    sb.append("\n")
  }
  sb.append("after $attempt attempts\n")
  sb.append("Caused by: ${cause.message?.trim()}")
  return sb.toString()
}

data class PropertyFailureInput<T>(val original: T?, val shrunk: T?)

class PropertyAssertionError(val e: AssertionError,
                             val attempt: Int,
                             val inputs: List<PropertyFailureInput<out Any?>>) : AssertionError(propertyTestFailureMessage(attempt, inputs, e), e)