package io.kotest.properties

import io.kotest.assertions.Failures
import io.kotest.assertions.show.show

fun propertyTestFailureMessage(attempt: Int,
                               inputs: List<PropertyFailureInput<out Any?>>,
                               cause: AssertionError): String {
  val sb = StringBuilder()
  sb.append("Property failed for")
  sb.append("\n")
  inputs.withIndex().forEach {
    val input = if (it.value.shrunk == it.value.original) {
      "Arg ${it.index}: ${it.value.shrunk.show()}"
    } else {
      "Arg ${it.index}: ${it.value.shrunk.show()} (shrunk from ${it.value.original})"
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
