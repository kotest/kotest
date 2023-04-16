package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.mpp.stacktraces

private const val INDENT = "   "

/**
 * An error that bundles multiple other [Throwable]s together.
 */
class MultiAssertionError(
   errors: List<Throwable>,
   depth: Int,
   subject: Printed? = null,
) : AssertionError(createMessage(errors, depth, subject)) {

   companion object {
      internal fun createMessage(errors: List<Throwable>, depth: Int, subject: Printed?) = buildString {
         append("The following ")

         if (errors.size == 1) {
            append("assertion")
         } else {
            append(errors.size).append(" assertions")
         }

         if (subject != null) {
            append(" for ").append(subject.value)
         }
         append(" failed:\n")

         for ((i, err) in errors.withIndex()) {
            append(INDENT.repeat(depth)).append(i + 1).append(") ").append(err.message).append("\n")
            stacktraces.throwableLocation(err)?.let {
               append(INDENT.repeat(depth + 1)).append("at ").append(it).append("\n")
            }
         }
      }
   }
}

fun multiAssertionError(errors: List<Throwable>): Throwable {
   val message = MultiAssertionError.createMessage(errors, 0, null)
   return failure(message, errors.firstOrNull { it.cause != null })
}
