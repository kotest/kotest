package io.kotest.core.test

import io.kotest.core.config.Project
import io.kotest.core.config.TestNameCaseOptions

/**
 * Models the name of a test. A test can sometimes have a prefix set, eg when using FeatureSpec.
 * This prefix can be disabled in output through project config flags.
 */
data class TestName(val prefix: String?, val name: String, val focus: Boolean, val bang: Boolean) {

   companion object {
      operator fun invoke(prefix: String?, name: String): TestName {
         return when {
            name.trim().startsWith("!") -> TestName(prefix, name.trim().drop(1).trim(), focus = false, bang = true)
            name.trim().startsWith("f:") -> TestName(prefix, name.trim().drop(2).trim(), focus = true, bang = false)
            else -> TestName(prefix, name, focus = false, bang = false)
         }
      }

      operator fun invoke(name: String) = invoke(null, name)
   }

   init {
      require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
      require(!focus || !bang) { "Bang and focus cannot both be true" }
   }

   /**
    * Creates a test name correctly handling focus, bang and prefix. If a prefix is specified,
    * the focus/bang is moved to before the prefix.
    *
    * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
    * and ends up with !when disable, so that it is correctly parsed by the test runtime.
    *
    * To create test names with proper case, this function's result is affected by the
    * [Project.testNameCase] property.
    */
   fun displayName(): String {
      val flattened = name.trim().replace("\n", "")
      val withPrefix = if (Project.includeTestScopePrefixes() && prefix != null) prefix else ""

      val name = if (withPrefix.isBlank()) {
         when (Project.testNameCase()) {
            TestNameCaseOptions.Sentence -> flattened.capitalize()
            TestNameCaseOptions.InitialLowercase -> flattened.uncapitalize()
            TestNameCaseOptions.Lowercase -> flattened.toLowerCase()
            else -> flattened
         }
      }
      else {
         when (Project.testNameCase()) {
            TestNameCaseOptions.Sentence -> "${withPrefix.capitalize()}${flattened.uncapitalize()}"
            TestNameCaseOptions.InitialLowercase -> "${withPrefix.uncapitalize()}${flattened.uncapitalize()}"
            TestNameCaseOptions.Lowercase -> "${withPrefix.toLowerCase()}${flattened.toLowerCase()}"
            else -> "$withPrefix$flattened"
         }
      }

      return when {
         focus -> "f:$name"
         bang -> "!$name"
         else -> name
      }
   }
}

private fun String.uncapitalize() =
   this[0].toLowerCase() + substring(1 until this.length)

/**
 * Returns true if this test is a focused test.
 * That is, if the name starts with "f:".
 */
fun TestCase.isFocused() = this.description.name.focus

/**
 * Returns true if this test is disabled by being prefixed with a !
 */
fun TestCase.isBang(): Boolean = this.description.name.bang

