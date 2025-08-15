package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.AssertionsConfig
import io.kotest.assertions.Expected
import io.kotest.assertions.diffLargeString
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.StringPrint
import io.kotest.assertions.submatching.StringPartialMatch
import io.kotest.common.sysprop

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
      val t = StringPartialMatch(expected, actual)
      return when {
         actual == expected -> null

         equalIgnoringWhitespace(actual, expected) -> AssertionErrorBuilder.create()
            .withMessage("(contents match, but line-breaks differ; output has been escaped to show line-breaks)\n")
            .withValues(
               Expected(Printed(escapeLineBreaks(expected))),
               Actual(Printed(escapeLineBreaks(actual)))
            ).build()

         useDiff(expected, actual) -> diff(expected, actual)

         t.matched -> AssertionErrorBuilder.create()
            .withMessage("Contents did not match exactly, but found the following partial match(es):\n${t.descriptionString}\n")
            .withValues(Expected(StringPrint.printUnquoted(expected)), Actual(StringPrint.printUnquoted(actual)))
            .build()

         else -> AssertionErrorBuilder.create()
            .withValues(Expected(StringPrint.printUnquoted(expected)), Actual(StringPrint.printUnquoted(actual)))
            .build()
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
         AssertionErrorBuilder.create()
            .withValues(Expected(StringPrint.printUnquoted(expected)), Actual(StringPrint.printUnquoted(actual)))
            .build()
      else
         AssertionErrorBuilder.create()
            .withValues(Expected(StringPrint.printUnquoted(expected)), Actual(StringPrint.printUnquoted(actual)))
            .build()
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

private fun escapeLineBreaks(input: String): String {
   return input
      .replace("\n", "\\n")
      .replace("\r", "\\r")
}

/**
 * Returns true if we are executing inside intellij.
 *
 * Note: This cannot be relied on for 100% accuracy.
 */
private fun isIntellij(): Boolean {
   return sysprop("idea.test.cyclic.buffer.size") != null
      || (sysprop("jboss.modules.system.pkgs") ?: "").contains("com.intellij.rt")
      || sysprop("intellij.debug.agent") != null
      || (sysprop("java.class.path") ?: "").contains("idea_rt.jar")
      || (sysprop("idea.active") != null)
}
