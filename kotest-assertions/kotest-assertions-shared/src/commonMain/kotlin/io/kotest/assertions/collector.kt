package io.kotest.assertions

import io.kotest.assertions.print.Printed
import io.kotest.common.ExperimentalKotest
import io.kotest.common.stacktrace.stacktraces
import io.kotest.matchers.ErrorCollectionMode
import io.kotest.matchers.ErrorCollector
import io.kotest.matchers.errorCollector

/**
 * Throws all errors currently collected in the [ErrorCollector].
 * If no errors are collected, this function does nothing.
 */
fun ErrorCollector.throwCollectedErrors() {
   collectErrors()?.let { throw it }
}

/**
 * Retuns all errors currently collected in the [ErrorCollector],
 * wrapped in a [MultiAssertionError] if there are multiple errors.
 */
expect fun ErrorCollector.collectErrors(): AssertionError?

internal fun List<Throwable>.toAssertionError(depth: Int, subject: Printed?): AssertionError? {
   return when {
      isEmpty() -> null
      size == 1 && subject != null -> AssertionError(createMessage(this, depth, subject))
      size == 1 && subject == null -> AssertionError(this[0].message)
      else -> MultiAssertionError(this, createMessage(this, depth, subject))
   }?.let {
      stacktraces.cleanStackTrace(it)
   }
}

/**
 * Pushes the provided [error] onto the [errorCollector] and throws if the configured collection mode is [ErrorCollectionMode.Hard]
 */
@ExperimentalKotest
fun ErrorCollector.pushErrorAndMaybeThrow(error: Throwable) {
   pushError(error)

   if (getCollectionMode() == ErrorCollectionMode.Hard) {
      throwCollectedErrors()
   }
}

/**
 * If we are in "soft assertion mode" will add this throwable to the
 * list of throwables for the current execution. Otherwise will
 * throw immediately.
 */
fun ErrorCollector.collectOrThrow(error: Throwable) {
   val cleanedError = stacktraces.cleanStackTrace(error)
   when (getCollectionMode()) {
      ErrorCollectionMode.Soft -> pushError(cleanedError)
      ErrorCollectionMode.Hard -> throw cleanedError
   }
}

fun ErrorCollector.pushErrors(errors: Collection<Throwable>) {
   errors.forEach {
      pushError(it)
   }
}

fun ErrorCollector.collectOrThrow(errors: Collection<Throwable>) {
   pushErrors(errors)

   if (getCollectionMode() == ErrorCollectionMode.Hard) {
      throwCollectedErrors()
   }
}
