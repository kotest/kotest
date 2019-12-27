package io.kotest.property.internal

import io.kotest.assertions.Failures
import io.kotest.assertions.show.show

fun propertyTestFailureMessage(
   attempt: Int,
   inputs: List<PropertyFailureInput<out Any?>>,
   cause: Throwable
): String {
   val sb = StringBuilder()
   if (inputs.isEmpty()) {
      sb.append("Property failed after $attempt attempts\n")
   } else {
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
   }
   // don't bother to include the exception type if it's AssertionError
   val causedBy = when (cause::class.simpleName) {
      "AssertionError" -> "Caused by: ${cause.message?.trim()}"
      else -> "Caused by ${cause::class.simpleName}: ${cause.message?.trim()}"
   }
   sb.append(causedBy)
   return sb.toString()
}

data class PropertyFailureInput<T>(val original: T?, val shrunk: T?)

fun propertyAssertionError(
   e: Throwable,
   attempt: Int,
   inputs: List<PropertyFailureInput<out Any?>>
): AssertionError {
   return Failures.failure(propertyTestFailureMessage(attempt, inputs, e), e)
}
