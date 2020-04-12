package io.kotest.assertions

/**
 * Returns a string error message from the given throwable.
 * If the type is an [AssertionError] then the message is taken from the exceptions own message,
 * otherwise the exception is converted to a string.
 */
fun exceptionToMessage(t: Throwable): String =
   when (t) {
      is AssertionError -> when (t.message) {
         null -> t.toString()
         else -> t.message!!
      }
      else -> t.toString()
   }
