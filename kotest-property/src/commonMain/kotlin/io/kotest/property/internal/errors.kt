package io.kotest.property.internal

import io.kotest.assertions.Failures
import io.kotest.assertions.show.show
import io.kotest.fp.Tuple2
import io.kotest.fp.Tuple3
import io.kotest.fp.Tuple4
import io.kotest.fp.Tuple5

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

// creates an exception for failed, shrunk, values and throws
fun <A> fail(
   value: A, // the original values
   shrink: A, // the shrunk values
   e: Throwable, // the underlying failure reason,
   attempts: Int
) {
   val inputs = listOf(
      PropertyFailureInput(value, shrink)
   )
   throw propertyAssertionError(e, attempts, inputs)
}

// creates an exception without specifying parameter details and throws
fun fail(
   e: Throwable, // the underlying failure reason,
   attempts: Int
) {
   throw propertyAssertionError(e, attempts, emptyList())
}

// creates an exception for failed, shrunk, values and throws
fun <A, B> fail(
   values: Tuple2<A, B>, // the original values
   shrink: Tuple2<A, B>, // the shrunk values
   e: Throwable, // the underlying failure reason,
   attempts: Int
) {
   val inputs = listOf(
      PropertyFailureInput(values.a, shrink.a),
      PropertyFailureInput(values.b, shrink.b)
   )
   throw propertyAssertionError(e, attempts, inputs)
}

// creates an exception for failed, shrunk, values and throws
fun <A, B, C> fail(
   values: Tuple3<A, B, C>, // the original values
   shrink: Tuple3<A, B, C>, // the shrunk values
   e: Throwable, // the underlying failure reason,
   attempts: Int
) {
   val inputs = listOf(
      PropertyFailureInput(values.a, shrink.a),
      PropertyFailureInput(values.b, shrink.b),
      PropertyFailureInput(values.c, shrink.c)
   )
   throw propertyAssertionError(e, attempts, inputs)
}

// creates an exception for failed, shrunk, values and throws
fun <A, B, C, D> fail(
   values: Tuple4<A, B, C, D>, // the original values
   shrink: Tuple4<A, B, C, D>, // the shrunk values
   e: Throwable, // the underlying failure reason,
   attempts: Int
) {
   val inputs = listOf(
      PropertyFailureInput(values.a, shrink.a),
      PropertyFailureInput(values.b, shrink.b),
      PropertyFailureInput(values.c, shrink.c),
      PropertyFailureInput(values.d, shrink.d)
   )
   throw propertyAssertionError(e, attempts, inputs)
}

// creates an exception for failed, shrunk, values and throws
fun <A, B, C, D, E> fail(
   values: Tuple5<A, B, C, D, E>, // the original values
   shrink: Tuple5<A, B, C, D, E>, // the shrunk values
   e: Throwable, // the underlying failure reason,
   attempts: Int
) {
   val inputs = listOf(
      PropertyFailureInput(values.a, shrink.a),
      PropertyFailureInput(values.b, shrink.b),
      PropertyFailureInput(values.c, shrink.c),
      PropertyFailureInput(values.d, shrink.d),
      PropertyFailureInput(values.e, shrink.e)
   )
   throw propertyAssertionError(e, attempts, inputs)
}
