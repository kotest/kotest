package io.kotest.core.test

import io.kotest.core.Tag
import io.kotest.core.config.Configuration
import io.kotest.core.plan.DisplayName
import io.kotest.core.plan.TestName

/**
 * Generates test names for display / reporting purposes.
 */
class TestNameFormatter(
   private val includeAffixes: Boolean?,
   private val includeTags: Boolean,
   private val includeFocusBang: Boolean,
   private val removeTestNameWhitespace: Boolean,
   private val testNameCase: TestNameCase,
) {

   companion object {
      operator fun invoke(configuration: Configuration): TestNameFormatter {
         return TestNameFormatter(
            includeAffixes = configuration.includeTestScopeAffixes ?: false,
            includeTags = configuration.testNameAppendTags,
            includeFocusBang = false,
            removeTestNameWhitespace = configuration.removeTestNameWhitespace,
            testNameCase = configuration.testNameCase
         )
      }
   }

//   fun path(testCase: TestCase, separator: String): String {
//      return testCase.chain().joinToString(separator) { format(it) }
//   }

   fun format(name: TestName, tags: Set<Tag>): DisplayName {

      val withAffixes = when (includeAffixes ?: name.defaultAffixes) {
         true -> "${name.prefix ?: ""}${name.testName}${name.suffix ?: ""}"
         false -> ""
      }

      val tagNames = tags.joinToString(", ")

      val displayName = if (withAffixes.isBlank()) {
         when (testNameCase) {
            TestNameCase.Sentence -> name.testName.capital()
            TestNameCase.InitialLowercase -> name.testName.uncapitalize()
            TestNameCase.Lowercase -> name.testName.lowercase()
            else -> name.testName
         }
      } else {
         when (testNameCase) {
            TestNameCase.Sentence -> "${withAffixes.capital()}${name.testName.uncapitalize()}"
            TestNameCase.InitialLowercase -> "${withAffixes.uncapitalize()}${name.testName.uncapitalize()}"
            TestNameCase.Lowercase -> "${withAffixes.lowercase()}${name.testName.lowercase()}"
            else -> "$withAffixes$name.testName"
         }
      }

      val str = buildString {
         if (includeTags)
            append("[tags = $tagNames]")
      }

      return DisplayName(str)
   }

   private fun String.capital() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

   private fun String.uncapitalize() =
      this[0].lowercaseChar() + substring(1 until this.length)
}
