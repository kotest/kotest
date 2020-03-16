package io.kotest.assertions

import io.kotest.assertions.show.Printed

actual fun cleanStackTrace(throwable: Throwable): Throwable = throwable
actual fun failure(message: String): Throwable = AssertionError(message)
actual fun failure(message: String, cause: Throwable?): Throwable = failure(message)
actual fun failure(expected: Printed, actual: Printed): Throwable =
   failure(intellijFormatError(expected, actual))

