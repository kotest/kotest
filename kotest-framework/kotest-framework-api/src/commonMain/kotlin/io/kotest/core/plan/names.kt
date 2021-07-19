package io.kotest.core.plan

import io.kotest.core.config.configuration
import kotlin.reflect.KClass

/**
 * Returns the value of the @DisplayName annotation on JVM platforms, if present.
 * On other platforms, returns null.
 */
expect fun KClass<*>.displayName(): DisplayName

/**
 * Models the name of a test case. A test case can sometimes have a prefix and/or suffix set
 * by the spec style, eg when using BehaviorSpec or WordSpec.
 *
 * @param testName the name exactly as the user entered it but with focus or bang stripped
 * @param focus if the test name was specified with a f: prefix
 * @param bang if the test name was specified with a ! prefix
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
      operator fun invoke(name: String) = createTestName(name)
   }

   init {
      require(testName.isNotBlank() && testName.isNotEmpty()) { "Cannot create test with blank or empty name" }
      require(!focus || !bang) { "Bang and focus cannot both be true" }
   }
}

internal fun createTestName(name: String): TestName = createTestName(name, null, null, false)

/**
 * Creates a [DescriptorName.TestName] correctly handling focus, bang, prefix and suffix.
 * If a prefix is specified the focus/bang is moved to before the prefix.
 *
 * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
 * and ends up with !when disable, so that it is correctly parsed by the test runtime.
 *
 * @param prefix optional prefix that some specs may specify, such as "Given:"
 * @param name the user supplied name for the test
 * @param suffix optional suffix that some specs may specify such as "should"
 */
internal fun createTestName(
   name: String,
   prefix: String?,
   suffix: String?,
   defaultAffixes: Boolean,
): TestName {

   val trimmedName = if (configuration.removeTestNameWhitespace) {
      name.removeAllExtraWhitespaces()
   } else {
      name.removeNewLineCharacter()
   }

   val (focus, bang, croppedName) = when {
      trimmedName.startsWith("!") -> Triple(first = false, second = true, third = trimmedName.drop(1).trim())
      trimmedName.startsWith("f:") -> Triple(first = true, second = false, third = trimmedName.drop(2).trim())
      else -> Triple(first = false, second = false, third = trimmedName)
   }

   return TestName(croppedName, focus, bang, prefix, suffix, defaultAffixes)
}

private fun String.removeAllExtraWhitespaces() = this.split(Regex("\\s")).filterNot { it == "" }.joinToString(" ")
private fun String.removeNewLineCharacter() = this.replace("\n", "").trim()
