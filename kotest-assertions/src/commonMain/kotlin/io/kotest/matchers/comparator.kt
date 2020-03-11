package io.kotest.matchers

import io.kotest.assertions.Failures
import io.kotest.assertions.clueContextAsString
import io.kotest.assertions.diffLargeString
import io.kotest.assertions.stringRepr
import io.kotest.mpp.sysprop

abstract class ValueComparator<T> {
   internal open fun compare(actual: T?, expected: T?): Throwable? {
      return when {
         actual == null && expected != null -> generateError(actual, expected)
         actual != expected -> generateError(actual, expected)
         else -> null
      }
   }

   private fun largeStringDiffMinSize() = sysprop("kotest.assertions.multi-line-diff-size", "50").toInt()
   internal open fun generateError(actual: T?, expected: T?): Throwable {
      val (expectedRepr, actualRepr) = diffLargeString(
         stringRepr(expected),
         stringRepr(actual),
         largeStringDiffMinSize()
      )
      return getThrowableWith(expectedRepr, actualRepr)
   }

   private fun getThrowableWith(expectedRepr: String, actualRepr: String) =
      Failures.failure(clueContextAsString() + equalsErrorMessage(expectedRepr, actualRepr), expectedRepr, actualRepr)
}

internal val regexComparator = object : ValueComparator<Regex?>() {
   override fun compare(actual: Regex?, expected: Regex?): Throwable? {
      return when {
         actual == null && expected != null -> generateError(actual, expected)
         patternOrOptionsAreNotEqual(actual, expected) -> generateError(actual, expected)
         else -> null
      }
   }

   private fun patternOrOptionsAreNotEqual(actual: Regex?, expected: Regex?) =
      actual?.pattern == expected?.pattern || actual?.options == expected?.options
}

internal val mapComparator = object : ValueComparator<Map<*, *>?>() {
   override fun compare(actual: Map<*, *>?, expected: Map<*, *>?): Throwable? {
      return if (actual != expected) generateError(actual, expected)
      else null
   }

   override fun generateError(actual: Map<*, *>?, expected: Map<*, *>?): Throwable {
      return Failures.failure(buildFailureMessage(actual, expected))
   }
}

internal val defaultComparator = object : ValueComparator<Any?>() {}

private fun Map<*, *>.toFormattedString(): String {
   if (isEmpty()) return "{}"
   val indentation = "  "
   val newLine = "\n"
   return toList()
      .joinToString(
         separator = ",$newLine",
         prefix = "{$newLine",
         postfix = "$newLine}",
         limit = 10
      ) { "$indentation${stringRepr(it.first)} = ${stringRepr(it.second)}" }
}

private fun buildFailureMessage(actual: Map<*, *>?, expected: Map<*, *>?): String {
   val keysDifferentAt = when {
      actual != null && expected != null -> {
         val keysHavingDifferentValues = actual.keys.filterNot { expected[it] == actual[it] }
         "Values differed at keys ${keysHavingDifferentValues.joinToString(limit = 10)}"
      }
      else -> ""
   }
   return "Expected\n${expected?.toFormattedString()}\nto be equal to\n${actual?.toFormattedString()}\n$keysDifferentAt"
}

