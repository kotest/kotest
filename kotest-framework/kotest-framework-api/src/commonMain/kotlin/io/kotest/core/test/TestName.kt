package io.kotest.core.test

/**
 * Models the name of a test. A test can sometimes have a prefix set, eg when using FeatureSpec or BehaviorSpec.
 * This prefix can be disabled in output through project config flags.
 *
 * @param an optional prefix that some specs may specify, such as "Given:"
 * @param name the user supplied name for the test
 * @param focus if the test name was specified with f:
 * @param bang if the test name was specified with the ! prefix
 */
data class TestName(val prefix: String?, val name: String, val focus: Boolean, val bang: Boolean) {
   init {
      require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
      require(!focus || !bang) { "Bang and focus cannot both be true" }
   }

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
}

/**
 * Creates a test name correctly handling focus, bang and prefix. If a prefix is specified,
 * the focus/bang is moved to before the prefix.
 *
 * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
 * and ends up with !when disable, so that it is correctly parsed by the test runtime.
 *
 * @param testNameCase a [TestNameCase] parameter to adjust the captialize of the
 * returned display name.
 *
 * @param includeTestScopePrefixes if true then the prefixes of specs like BehaviorSpec will be
 * included in the test name
 */
fun TestName.format(testNameCase: TestNameCase, includeTestScopePrefixes: Boolean): String {

   val flattened = name.trim().replace("\n", "")
   val withPrefix = if (includeTestScopePrefixes && prefix != null) prefix else ""

   val name = if (withPrefix.isBlank()) {
      when (testNameCase) {
         TestNameCase.Sentence -> flattened.capitalize()
         TestNameCase.InitialLowercase -> flattened.uncapitalize()
         TestNameCase.Lowercase -> flattened.toLowerCase()
         else -> flattened
      }
   } else {
      when (testNameCase) {
         TestNameCase.Sentence -> "${withPrefix.capitalize()}${flattened.uncapitalize()}"
         TestNameCase.InitialLowercase -> "${withPrefix.uncapitalize()}${flattened.uncapitalize()}"
         TestNameCase.Lowercase -> "${withPrefix.toLowerCase()}${flattened.toLowerCase()}"
         else -> "$withPrefix$flattened"
      }
   }

   return when {
      focus -> "f:$name"
      bang -> "!$name"
      else -> name
   }
}

private fun String.uncapitalize() =
   this[0].toLowerCase() + substring(1 until this.length)
