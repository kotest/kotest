package io.kotest.assertions

import io.kotest.assertions.print.Printed


/**
 * An error that bundles multiple other [Throwable]s together.
 */
class MultiAssertionError : AssertionError {

   constructor(errors: List<Throwable>, depth: Int, subject: Printed? = null) : super(
      createMessage(
         errors,
         depth,
         subject
      )
   )

   constructor(message: String) : super(message)
}

fun multiAssertionError(errors: List<Throwable>): Throwable {
   val message = createMessage(errors, 0, null)
   return failure(message, errors.firstOrNull { it.cause != null })
}
