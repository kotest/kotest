package io.kotlintest.properties

import io.kotlintest.Failures
import io.kotlintest.convertValueToString

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

internal fun propertyAssertionError(e: AssertionError,
                                    attempt: Int,
                                    inputs: List<PropertyFailureInput<out Any?>>): AssertionError {
  return Failures.failure(propertyTestFailureMessage(attempt, inputs, e), e)
}
