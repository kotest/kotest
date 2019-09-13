package io.kotest.assertions

fun exceptionToMessage(t: Throwable): String =
  when (t) {
    is AssertionError -> when (t.message) {
      null -> t.toString()
      else -> t.message!!
    }
    else -> t.toString()
  }
