package io.kotest.core.names

import io.kotest.core.config.configuration

/**
 * Models the name of a [io.kotest.core.test.TestCase] as entered by a user.
 *
 * A test case can sometimes have a prefix and/or suffix set by the spec style,
 * eg when using BehaviorSpec or WordSpec.
 *
 * Test names can be prefixed with ! or f: to indicate bang or focus respectively.
 *
 * @param testName the name exactly as the user entered it but with focus or bang stripped
 * @param focus if the test name was specified with f: prefix
 * @param bang if the test name was specified with ! prefix
 * @param prefix if the test style includes a test name prefix, such as "should"
 * @param suffix if the test style includes a test name suffix, such as "when"
 * @param defaultAffixes if the test style recommends test affixes by default, such as BehaviorSpec
 */
data class TestName(
   val testName: String,
   val focus: Boolean,
   val bang: Boolean,
   val prefix: String?,
   val suffix: String?,
   val defaultAffixes: Boolean,
) {

   companion object {

      operator fun invoke(name: String): TestName = TestName(null, name, null, false)

      operator fun invoke(prefix: String?, name: String, defaultAffixes: Boolean): TestName =
         TestName(prefix, name, null, defaultAffixes)

      operator fun invoke(
         prefix: String?,
         name: String,
         suffix: String?,
         defaultAffixes: Boolean,
      ): TestName {

         val trimmed = if (configuration.removeTestNameWhitespace) {
            name.removeAllExtraWhitespaces()
         } else {
            name.removeNewLineCharacter()
         }

         val (focus, bang, parsedName) = when {
            trimmed.startsWith("!") -> Triple(first = false, second = true, third = trimmed.drop(1).trim())
            trimmed.startsWith("f:") -> Triple(first = true, second = false, third = trimmed.drop(2).trim())
            else -> Triple(first = false, second = false, third = trimmed)
         }

         return TestName(parsedName, focus, bang, prefix, suffix, defaultAffixes)
      }
   }

   init {
      require(testName.isNotBlank() && testName.isNotEmpty()) { "Cannot create test with blank or empty name" }
      require(!focus || !bang) { "Bang and focus cannot both be true" }
   }
}

private fun String.removeAllExtraWhitespaces() = this.split(Regex("\\s")).filterNot { it == "" }.joinToString(" ")
private fun String.removeNewLineCharacter() = this.replace("\n", "").trim()
