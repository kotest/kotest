package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.common.stacktrace.stacktraces

private const val INDENT = "   "

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
