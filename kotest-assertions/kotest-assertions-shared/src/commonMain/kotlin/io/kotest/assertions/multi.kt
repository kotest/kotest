package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.common.stacktrace.stacktraces

/**
 * An error that wraps one or more [Throwable]s.
 */
class MultiAssertionError(val errors: List<Throwable>, message: String) : AssertionError(message) {
   init {
      require(errors.size > 1) { "MultiAssertionError must contain at least two errors" }
   }
}

class MultiAssertionErrorBuilder(private val errors: List<Throwable>) {

   companion object {
      fun create(errors: List<Throwable>): MultiAssertionErrorBuilder = MultiAssertionErrorBuilder(errors)
   }

   /**
    * Creates an [AssertionError] from the given list of [errors].
    * The message will be created from the errors.
    */
   fun build(): MultiAssertionError {
      val message = createMessage(errors, 0, null)
      return MultiAssertionError(errors, message)
   }
}

private const val INDENT = "   "

@Deprecated("what to do with this?")
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
