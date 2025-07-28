package io.kotest.data

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.MultiAssertionErrorBuilder
import io.kotest.assertions.print.print

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@PublishedApi
internal class ErrorCollector {
   private val errors = mutableListOf<Throwable>()

   fun append(t: Throwable) {
      errors += t
   }

   fun assertAll() {
      if (errors.size == 1) {
         throw errors[0]
      } else if (errors.size > 1) {
         throw MultiAssertionErrorBuilder.create(errors).build()
      }
   }
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@PublishedApi
internal fun error(e: Throwable, headers: List<String>, values: List<*>): Throwable {
   val valuesInPrintableFormat = values.map { value ->
      value?.print()?.value
   }
   val params = headers.zip(valuesInPrintableFormat).joinToString(", ")
   // Include class name for non-assertion errors, since the class is often meaningful and there might not
   // be a message (e.g. NullPointerException)
   val message = when (e) {
      is AssertionError -> e.message
      else -> e.toString()
   }

   return AssertionErrorBuilder.create()
      .withMessage("Test failed for $params with error $message")
      .withCause(e)
      .build()
}

@Deprecated("Use withData as the preferred way of data driven testing. This was deprecated in 6.0")
@PublishedApi
internal fun forNoneError(headers: List<String>, values: List<*>): Throwable {
   val params = headers.zip(values).joinToString(", ")
   return AssertionErrorBuilder.create().withMessage("Test passed for $params but expected failure").build()
}
