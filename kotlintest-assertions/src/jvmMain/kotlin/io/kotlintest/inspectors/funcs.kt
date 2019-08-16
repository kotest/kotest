package io.kotlintest.inspectors

import convertValueToString
import exceptionToMessage
import io.kotlintest.Failures

fun <T> runTests(col: Collection<T>, f: (T) -> Unit): List<ElementResult<T>> {
  return col.map {
    try {
      f(it)
      ElementPass(it)
    } catch (e: Throwable) {
      ElementFail(it, e)
    }
  }
}

/**
 * Build assertion error message.
 * Show 10 passed and failed results by default. You can change the number of output results by setting the system property `kotlintest.assertions.maxOutputResult`.
 *
 * E.g.:
 *
 * ```
 *     -Dkotlintest.assertions.maxOutputResult=20
 * ```
 */
fun <T> buildAssertionError(msg: String, results: List<ElementResult<T>>): String {
  val passed = results.filterIsInstance<ElementPass<T>>()
  val failed = results.filterIsInstance<ElementFail<T>>()
  val maxOutputResult = System.getProperty("kotlintest.assertions.maxOutputResult")?.toInt() ?: 10

  val builder = StringBuilder(msg)
  builder.append("\n\nThe following elements passed:\n")
  if (passed.isEmpty()) {
    builder.append("--none--")
  } else {
    builder.append(passed.take(maxOutputResult).map { it.t }.joinToString("\n"))
    if (passed.size > maxOutputResult) {
      builder.append("\n... and ${passed.size - maxOutputResult} more passed elements")
    }
  }

  builder.append("\n\nThe following elements failed:\n")
  if (failed.isEmpty()) {
    builder.append("--none--")
  } else {
    builder.append(failed.take(maxOutputResult).joinToString("\n") { convertValueToString(it.t) + " => " + exceptionToMessage(it.error) })
    if (failed.size > maxOutputResult) {
      builder.append("\n... and ${failed.size - maxOutputResult} more failed elements")
    }
  }
  throw Failures.failure(builder.toString())
}

