package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.matchers.ErrorCollector

actual fun ErrorCollector.collectErrors(): AssertionError? {
   fun prefixWithSubjectInformation(e: AssertionError, subject: Printed): AssertionError {
      // we are just adapting the original exception to add a subject information, so we don't need to
      // worry about comparison values because they will be in the original message
      return AssertionErrorBuilder.create()
         .withMessage("The following assertion for ${subject.value} failed:\n" + e.message)
         .withCause(e.cause)
         .build()
   }

   val failures = errors()
   clear()

   return if (failures.size == 1 && failures[0] is AssertionError) {
      val e = failures[0] as AssertionError
      subject?.let { prefixWithSubjectInformation(e, it) } ?: e
   } else {
      failures.toAssertionError(depth, subject)
   }
}
