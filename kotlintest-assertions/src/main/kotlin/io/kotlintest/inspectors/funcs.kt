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

fun <T> buildAssertionError(msg: String, results: List<ElementResult<T>>): String {
  val passed = results.filterIsInstance<ElementPass<T>>()
  val failed = results.filterIsInstance<ElementFail<T>>()
  val maxOutputResult = System.getProperty("kotlintest.assertions.maxOutputResult")?.toInt() ?: 10

  val builder = StringBuilder(msg)
  builder.append("\n\nThe following elements passed:\n")
  if (passed.isEmpty()) {
    builder.append("--none--")
  } else {
    builder.append(passed.take(maxOutputResult).map { it.value }.joinToString("\n"))
    if (passed.size > maxOutputResult) {
      builder.append("\n... and ${passed.size - maxOutputResult} more passed elements")
    }
  }

  builder.append("\n\nThe following elements failed:\n")
  if (failed.isEmpty()) {
    builder.append("--none--")
  } else {
    builder.append(failed.take(maxOutputResult).joinToString("\n") { convertValueToString(it.value) + " => " + exceptionToMessage(it.error) })
    if (failed.size > maxOutputResult) {
      builder.append("\n... and ${failed.size - maxOutputResult} more failed elements")
    }
  }
  throw Failures.failure(builder.toString())
}

