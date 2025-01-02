package io.kotest.engine.test.names

import io.kotest.core.Platform
import io.kotest.core.annotation.DisplayName
import io.kotest.core.platform
import io.kotest.core.config.ProjectConfiguration
import io.kotest.engine.names.DisplayNameFormatter
import io.kotest.core.names.TestNameCase
import io.kotest.core.test.TestCase
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * A default implementation of [DisplayNameFormatter].
 * Used when there are no registered [io.kotest.core.extensions.DisplayNameFormatterExtension]s.
 */
class DefaultDisplayNameFormatter(
   private val configuration: ProjectConfiguration,
) : DisplayNameFormatter {

   @Suppress("unused")
   constructor() : this(ProjectConfiguration())

   override fun format(testCase: TestCase): String {

      val prefix = when (configuration.includeTestScopeAffixes ?: testCase.name.defaultAffixes) {
         true -> testCase.name.prefix ?: ""
         false -> ""
      }

      val suffix = when (configuration.includeTestScopeAffixes ?: testCase.name.defaultAffixes) {
         true -> testCase.name.suffix ?: ""
         false -> ""
      }

      val displayName = if (prefix.isBlank()) {
         when (configuration.testNameCase) {
            TestNameCase.Sentence -> testCase.name.name.capital() + suffix
            TestNameCase.InitialLowercase -> testCase.name.name.uncapitalize() + suffix
            TestNameCase.Lowercase -> testCase.name.name.lowercase() + suffix
            else -> testCase.name.name + suffix
         }
      } else {
         when (configuration.testNameCase) {
            TestNameCase.Sentence -> "${prefix.capital()}${testCase.name.name.uncapitalize()}$suffix"
            TestNameCase.InitialLowercase -> "${prefix.uncapitalize()}${testCase.name.name.uncapitalize()}$suffix"
            TestNameCase.Lowercase -> "${prefix.lowercase()}${testCase.name.name.lowercase()}$suffix"
            else -> "$prefix${testCase.name.name}$suffix"
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
      return when (platform) {
         Platform.JVM -> kclass.annotation<DisplayName>()?.name ?: kclass.bestName()
         else -> kclass.bestName()
      }
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
