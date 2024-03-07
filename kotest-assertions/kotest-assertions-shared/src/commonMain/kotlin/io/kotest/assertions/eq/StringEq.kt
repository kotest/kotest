package io.kotest.assertions.eq

import io.kotest.assertions.*
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.print
import io.kotest.assertions.print.printWithType
import io.kotest.common.isIntellij

/**
 * An [Eq] implementation for String's that generates diffs for errors when the string inputs
 * are of a certain size.
 *
 * The min size for the diff is retrieved by [AssertionsConfig.largeStringDiffMinSize], which can be modified by setting
 * the system property "kotest.assertions.multi-line-diff-size"
 *
 * E.g.:
 * ```
 *     -Dkotest.assertions.multi-line-diff-size=42
 * ```
 */
object StringEq : Eq<String> {

   override fun equals(actual: String, expected: String, strictNumberEq: Boolean): Throwable? {
      return when {
         actual == expected -> null
         equalIgnoringWhitespace(actual, expected) -> {
            failure(
               Expected(Printed(escapeLineBreaks(expected))),
               Actual(Printed(escapeLineBreaks(actual))),
               "(contents match, but line-breaks differ; output has been escaped to show line-breaks)\n"
            )
         }
         useDiff(expected, actual) -> diff(expected, actual)
         else -> failureWithTypeInformation(ExpectedWithType(expected.printWithType()), ActualWithType(actual.printWithType()))
      }
   }

   private fun equalIgnoringWhitespace(actual: String, expected: String): Boolean {
      val a = linebreaks.replace(expected, "")
      val b = linebreaks.replace(actual, "")
      return a == b
   }

   private fun diff(expected: String, actual: String): Throwable {
      val result = diffLargeString(expected, actual)
      return if (result == null)
         failure(Expected(expected.print()), Actual(actual.print()))
      else
         failure(Expected(Printed(result.first)), Actual(Printed(result.second)))
   }

   private fun useDiff(expected: String, actual: String): Boolean {
      if (isIntellij()) return false
      val minSizeForDiff = AssertionsConfig.largeStringDiffMinSize
      return expected.lines().size >= minSizeForDiff
         && actual.lines().size >= minSizeForDiff &&
         AssertionsConfig.multiLineDiff != "simple"
   }
}

private val linebreaks = Regex("\r?\n|\r")

fun escapeLineBreaks(input: String): String {
   return input
      .replace("\n", "\\n")
      .replace("\r", "\\r")
}


