package io.kotest.equals.types

import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.diffLargeString
import io.kotest.common.isIntellij
import io.kotest.equals.EqualityResult
import io.kotest.equals.EqualityVerifier
import io.kotest.equals.SimpleEqualityResult

class StringEqualityVerifier(
   private val ignoreCase: Boolean,
) : EqualityVerifier<String> {
   private val whiteSpaces = Regex("[\n\r\t]")

   override fun name(): String = "string equality${if (ignoreCase) " ignoring case" else ""}"

   override fun areEqual(actual: String, expected: String): EqualityResult {
      val equal = { EqualityResult.equal(actual, expected, this) }
      val notEqual = { EqualityResult.notEqual(actual, expected, this) }

      return when {
         expected.equals(actual, ignoreCase = ignoreCase) -> equal()
         equalIgnoringWhitespace(actual, expected)
         -> notEqual().withDetails {
            showEscapedStringDifference(
               message = "Contents match by ${name()}, but whitespaces differ; output has been escaped to show whitespaces",
               expected = expected,
               actual = actual,
            )
         }
         useDiff(expected, actual) -> diff(expected, actual, notEqual())
         else -> notEqual().withDetails {
            showEscapedStringDifference(
               message = "Contents are not equal by ${name()}; output has been escaped to show whitespaces",
               expected = expected,
               actual = actual,
            )
         }
      }
   }

   fun ignoringCase(): StringEqualityVerifier {
      return StringEqualityVerifier(ignoreCase = true)
   }

   fun caseSensitive(): StringEqualityVerifier {
      return StringEqualityVerifier(ignoreCase = false)
   }

   private fun showEscapedStringDifference(message: String, expected: String, actual: String): String {
      return """
               | $message
               | Expected: ${escapeWhitespaces(expected)}
               | Actual  : ${escapeWhitespaces(actual)}
            """.trimMargin()
   }

   private fun escapeWhitespaces(input: String): String {
      return input
         .replace("\n", "\\n")
         .replace("\r", "\\r")
         .replace("\t", "\\t")
   }

   private fun equalIgnoringWhitespace(actual: String, expected: String): Boolean {
      val a = whiteSpaces.replace(expected, "")
      val b = whiteSpaces.replace(actual, "")
      return a.equals(b, ignoreCase = ignoreCase)
   }

   private fun diff(expected: String, actual: String, result: SimpleEqualityResult): EqualityResult {
      return when (val diff = diffLargeString(expected, actual)) {
         null -> result
         else -> result.withDetails {
            """
            | Contents are not equal by ${name()}; showing diff
            | Expected: ${diff.first}
            | Actual:   ${diff.second}
            """.trimMargin()
         }
      }
   }

   private fun useDiff(expected: String, actual: String): Boolean {
      if (isIntellij()) return false
      val minSizeForDiff = AssertionsConfig.largeStringDiffMinSize
      return expected.lines().size >= minSizeForDiff
         && actual.lines().size >= minSizeForDiff &&
         AssertionsConfig.multiLineDiff != "simple"
   }
}
