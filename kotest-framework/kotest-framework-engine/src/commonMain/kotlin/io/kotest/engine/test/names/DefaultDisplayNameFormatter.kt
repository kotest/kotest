package io.kotest.engine.test.names

import io.kotest.core.Platform
import io.kotest.core.annotation.DisplayName
import io.kotest.core.names.TestNameCase
import io.kotest.core.platform
import io.kotest.core.test.TestCase
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.names.DisplayNameFormatter
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * A default implementation of [DisplayNameFormatter].
 *
 * Used when there are no registered [io.kotest.core.extensions.DisplayNameFormatterExtension]s.
 *
 * This formatter will use the [DisplayName] annotation if present, otherwise it will use the test name.
 * It takes into account [TestNameCase] settings.
 */
class DefaultDisplayNameFormatter(
   private val projectConfigResolver: ProjectConfigResolver,
   private val testConfigResolver: TestConfigResolver,
) : DisplayNameFormatter {

   constructor() : this(ProjectConfigResolver(), TestConfigResolver())

   override fun format(testCase: TestCase): String {

      val prefix = when (projectConfigResolver.includeTestScopeAffixes(testCase)) {
         true -> testCase.name.prefix ?: ""
         false -> ""
      }

      val suffix = when (projectConfigResolver.includeTestScopeAffixes(testCase)) {
         true -> testCase.name.suffix ?: ""
         false -> ""
      }

      val displayName = if (prefix.isBlank()) {
         when (projectConfigResolver.testNameCase()) {
            TestNameCase.Sentence -> testCase.name.name.capital() + suffix
            TestNameCase.InitialLowercase -> testCase.name.name.uncapitalize() + suffix
            TestNameCase.Lowercase -> testCase.name.name.lowercase() + suffix
            else -> testCase.name.name + suffix
         }
      } else {
         when (projectConfigResolver.testNameCase()) {
            TestNameCase.Sentence -> "${prefix.capital()}${testCase.name.name.uncapitalize()}$suffix"
            TestNameCase.InitialLowercase -> "${prefix.uncapitalize()}${testCase.name.name.uncapitalize()}$suffix"
            TestNameCase.Lowercase -> "${prefix.lowercase()}${testCase.name.name.lowercase()}$suffix"
            else -> "$prefix${testCase.name.name}$suffix"
         }
      }

      val name = if (projectConfigResolver.testNameAppendTags()) {
         return appendTagsInDisplayName(testCase, displayName)
      } else {
         displayName
      }

      return when (val parent = testCase.parent) {
         null -> name
         else -> if (projectConfigResolver.displayFullTestPath()) format(parent) + " " + name else name
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

   private fun appendTagsInDisplayName(testCase: TestCase, displayName: String): String {
      val tagNames = testConfigResolver.tags(testCase).joinToString(", ")
      return if (tagNames.isBlank()) {
         displayName
      } else {
         "${displayName}[tags = $tagNames]"
      }
   }

   private fun String.capital() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

   private fun String.uncapitalize() =
      this[0].lowercaseChar() + substring(1 until this.length)
}
