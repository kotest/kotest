package io.kotest.assertions

/**
 * Exception class that enhances [withClue] and [asClue] functions by capturing additional context (clue)
 * and attaching it to the thrown exception.
 *
 * @property clue Additional context to enrich the assertion error message if an assertion fails.
 * @property message The error message, combining the clue and the original exception message.
 * @property cause The original exception that triggered this one.
 *
 * This class simplifies debugging by bundling relevant clues into the exception when using [withClue] and [asClue].
 */
class ExceptionWithClue(
   val clue: String,
   message: String?,
   cause: Throwable?,
) : RuntimeException(message, cause) {
   companion object {
      @Suppress("ThrowableNotThrown")
      fun from(clue: String, e: Exception): ExceptionWithClue =
         ExceptionWithClue(
            clue = clue,
            message = clueContextAsString() + (e.message ?: ""),
            cause = e,
         )
   }
}
