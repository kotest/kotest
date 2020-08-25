package io.kotest.core.test

import io.kotest.core.config.configuration

/**
 * An ADT that models the name of a [Description].
 */
sealed class DescriptionName {

   abstract val name: String
   abstract val displayName: String

   /**
    * Models the name of a spec.
    *
    * @param qualifiedName the fully qualified class name of this spec.
    * @param name the simple class name for this spec
    * @param displayName the name to be used for display purposes.
    */
   data class SpecName(
      val qualifiedName: String,
      override val name: String,
      override val displayName: String,
   ) : DescriptionName()

   /**
    * Models the name of a test case. A test case can sometimes have a prefix and or/ suffix set
    * eg when using BehaviorSpec or WordSpec.
    *
    * @param name the name as the user entered it
    * @param displayName the name to be used for display purposes
    * @param focus if the test name was specified with a f: prefix
    * @param bang if the test name was specified with a ! prefix
    */
   data class TestName(
      override val name: String,
      override val displayName: String,
      val focus: Boolean,
      val bang: Boolean,
   ) : DescriptionName() {

      init {
         require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
         require(displayName.isNotBlank() && displayName.isNotEmpty()) { "Cannot create test with blank or empty displayName" }
         require(!focus || !bang) { "Bang and focus cannot both be true" }
      }
   }
}

fun createTestName(name: String) = createTestName(null, name, false)

fun createTestName(prefix: String?, name: String, defaultIncludeAffix: Boolean): DescriptionName.TestName =
   createTestName(
      prefix,
      name,
      configuration.testNameCase,
      configuration.includeTestScopeAffixes ?: defaultIncludeAffix
   )

/**
 * Creates a [DescriptionName.TestName] correctly handling focus, bang, prefix and suffix.
 * If a prefix is specified the focus/bang is moved to before the prefix.
 *
 * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
 * and ends up with !when disable, so that it is correctly parsed by the test runtime.
 *
 * @param prefix optional prefix that some specs may specify, such as "Given:"
 * @param name the user supplied name for the test
 * @param suffix optional suffix that some specs may specify such as "should"

 * @param testNameCase a [TestNameCase] parameter to adjust the capitalisation of the display name
 * @param includeAffixesInDisplayName if true then the prefix and/or suffix will be included in the display name.
 */
fun createTestName(
   prefix: String?,
   name: String,
   testNameCase: TestNameCase,
   includeAffixesInDisplayName: Boolean,
): DescriptionName.TestName {

   val trimmedName = name.replace("\n", "").trim()

   val (focus, bang, croppedName) = when {
      trimmedName.startsWith("!") -> Triple(first = false, second = true, third = trimmedName.drop(1).trim())
      trimmedName.startsWith("f:") -> Triple(first = true, second = false, third = trimmedName.drop(2).trim())
      else -> Triple(first = false, second = false, third = trimmedName)
   }

   val withPrefix = when (includeAffixesInDisplayName) {
      true -> prefix ?: ""
      false -> ""
   }

   val displayName = if (withPrefix.isBlank()) {
      when (testNameCase) {
         TestNameCase.Sentence -> croppedName.capitalize()
         TestNameCase.InitialLowercase -> croppedName.uncapitalize()
         TestNameCase.Lowercase -> croppedName.toLowerCase()
         else -> croppedName
      }
   } else {
      when (testNameCase) {
         TestNameCase.Sentence -> "${withPrefix.capitalize()}${croppedName.uncapitalize()}"
         TestNameCase.InitialLowercase -> "${withPrefix.uncapitalize()}${croppedName.uncapitalize()}"
         TestNameCase.Lowercase -> "${withPrefix.toLowerCase()}${croppedName.toLowerCase()}"
         else -> "$withPrefix$croppedName"
      }
   }

   return DescriptionName.TestName(name, displayName, focus, bang)
}

private fun String.uncapitalize() =
   this[0].toLowerCase() + substring(1 until this.length)
