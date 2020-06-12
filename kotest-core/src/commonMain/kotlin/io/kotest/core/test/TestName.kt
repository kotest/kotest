package io.kotest.core.test

import io.kotest.core.config.Project

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
    */
   fun displayName(): String {
      val flattened = name.trim().replace("\n", "")
      val withPrefix = if (Project.includeTestScopePrefixes() && prefix != null) prefix else ""
      return when {
         focus -> "f:$withPrefix$flattened"
         bang -> "!$withPrefix$flattened"
         else -> "$withPrefix$flattened"
      }
   }
}

/**
 * Returns true if this test is a focused test.
 * That is, if the name starts with "f:".
 */
fun TestCase.isFocused() = this.description.name.focus

/**
 * Returns true if this test is disabled by being prefixed with a !
 */
fun TestCase.isBang(): Boolean = this.description.name.bang

