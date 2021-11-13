package io.kotest.engine.test.names

import io.kotest.core.config.Configuration
import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.DisplayNameFormatterExtension
import io.kotest.core.names.DisplayNameFormatter
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.DisplayName
import io.kotest.core.test.TestCase
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

fun getDisplayNameFormatter(registry: ExtensionRegistry, configuration: Configuration): DisplayNameFormatter {
   return registry.all()
      .filterIsInstance<DisplayNameFormatterExtension>()
      .firstOrNull()
      ?.formatter() ?: DefaultDisplayNameFormatter(configuration)
}

/**
 * A default implementation of [DisplayNameFormatter].
 * Used when there are no registered [io.kotest.core.extensions.DisplayNameFormatterExtension]s.
 */
class DefaultDisplayNameFormatter(
   private val configuration: Configuration,
) : DisplayNameFormatter {

   override fun format(testCase: TestCase): String {

      val withPrefix = when (configuration.includeTestScopeAffixes ?: testCase.name.defaultAffixes) {
         true -> testCase.name.prefix ?: ""
         false -> ""
      }

      val displayName = if (withPrefix.isBlank()) {
         when (configuration.testNameCase) {
            TestNameCase.Sentence -> testCase.name.testName.capital()
            TestNameCase.InitialLowercase -> testCase.name.testName.uncapitalize()
            TestNameCase.Lowercase -> testCase.name.testName.lowercase()
            else -> testCase.name.testName
         }
      } else {
         when (configuration.testNameCase) {
            TestNameCase.Sentence -> "${withPrefix.capital()}${testCase.name.testName.uncapitalize()}"
            TestNameCase.InitialLowercase -> "${withPrefix.uncapitalize()}${testCase.name.testName.uncapitalize()}"
            TestNameCase.Lowercase -> "${withPrefix.lowercase()}${testCase.name.testName.lowercase()}"
            else -> "$withPrefix${testCase.name.testName}"
         }
      }

      val name = if (configuration.testNameAppendTags) {
         return appendTagsInDisplayName(testCase, displayName)
      } else {
         displayName
      }

      return when (val parent = testCase.parent) {
         null -> name
         else -> if (configuration.displayFullTestPath) format(parent) + " " + name else name
      }
   }

   /**
    * Returns a formatted display name for this spec class.
    *
    * If the spec has been annotated with [DisplayName] (on supported platforms), then that will be used,
    * otherwise the default is to use the fully qualified class name.
    *
    * Note: This name must be globally unique. Two specs, even in different packages,
    * cannot share the same names, so if [DisplayName] is used, developers must ensure it does not
    * clash with another spec.
    */
   override fun format(kclass: KClass<*>): String {
      return kclass.annotation<DisplayName>()?.name ?: kclass.bestName()
   }
}

fun appendTagsInDisplayName(testCase: TestCase, displayName: String): String {
   val tagNames = testCase.config.tags.joinToString(", ")
   return if (tagNames.isBlank()) {
      displayName
   } else {
      "${displayName}[tags = $tagNames]"
   }
}

private fun String.capital() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

private fun String.uncapitalize() =
   this[0].lowercaseChar() + substring(1 until this.length)
