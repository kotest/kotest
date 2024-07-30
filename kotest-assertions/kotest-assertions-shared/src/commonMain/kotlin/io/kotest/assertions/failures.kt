package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.PrintedWithType
import io.kotest.assertions.print.printed
import io.kotest.mpp.stacktraces

data class Expected(val value: Printed)
data class Actual(val value: Printed)

data class ExpectedWithType(val value: PrintedWithType) {
   fun toExpected() = Expected(value.value.printed())
}

data class ActualWithType(val value: PrintedWithType) {
   fun toActual() = Actual(value.value.printed())
}

/**
 * Creates the most appropriate error from the given message, wrapping in clue context(s)
 * if any are set.
 */
fun failure(message: String): AssertionError = failure(message, null)

/**
 * Creates an [AssertionError] from the given message, wrapping in clue context(s)
 * if any are set, and setting the cause as [cause] on platforms that supported nested exceptions.
 *
 * If the platform supports stack traces,
 * then the stack is cleaned of `io.kotest` lines.
 */
fun failure(message: String, cause: Throwable?): AssertionError {
   return Exceptions.createAssertionError(clueContextAsString() + message, cause)
}

/**
 * Creates a [Throwable] from expected and actual values, appending clue context(s)
 * if any are set. The error message is generated in the intellij 'diff' format.
 *
 * This function should be used for "comparison" failures, such as "a" shouldBe "b".
 * For other types of errors (eg timeout, or expected exception but none was thrown) prefer
 * the failure methods that take an explicit message.
 *
 * The given values should have already been [Printed] using the Show typeclass.
 *
 * If the platform supports stack traces,
 * then the stack is cleaned of `io.kotest` lines.
 */
fun failure(expected: Expected, actual: Actual, prependMessage: String = ""): Throwable {
   return Exceptions.createAssertionError(
      prependMessage + clueContextAsString() + intellijFormatError(expected, actual),
      null,
      expected,
      actual
   )
}

fun failureWithTypeInformation(
   expected: ExpectedWithType,
   actual: ActualWithType,
   prependMessage: String = ""
): Throwable {
   if (actual.value.type == expected.value.type) return failure(
      expected.toExpected(),
      actual.toActual(),
      prependMessage
   )
   return Exceptions.createAssertionError(
      prependMessage + clueContextAsString() + intellijFormatErrorWithTypeInformation(expected, actual),
      null,
      expected.toExpected(),
      actual.toActual()
   )
}
