package io.kotest.assertions

import io.kotest.mpp.throwableLocation

/** An error that bundles multiple other [Throwable]s together */
class MultiAssertionError(errors: List<Throwable>) : AssertionError(createMessage(errors)) {
  companion object {
    private fun createMessage(errors: List<Throwable>) = buildString {
      append("\nThe following ")

      if (errors.size == 1) {
        append("assertion")
      } else {
        append(errors.size).append(" assertions")
      }
      append(" failed:\n")

      for ((i, err) in errors.withIndex()) {
        append(i + 1).append(") ").append(err.message).append("\n")
        err.throwableLocation()?.let {
          append("\tat ").append(it).append("\n")
        }
      }
    }
  }
}
