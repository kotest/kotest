package io.kotest.core.names

/**
 * Models the name of a [io.kotest.core.test.TestCase] as entered by a user.
 *
 * A test case can sometimes have a prefix and/or suffix set by the spec style,
 * e.g. when using BehaviorSpec or WordSpec. Note that the prefix or suffix should include
 * any whitespace required.
 *
 * Test names can also  be prefixed with `!` or `f:` to indicate bang or focus respectively.
 *
 * @param name the name as the user entered it but with focus, bang and whitespace stripped
 * @param focus if the test name was specified with the focus `f:` prefix
 * @param bang if the test name was specified with the bang `!` prefix
 * @param prefix if the test style includes a test name prefix, such as "should"
 * @param suffix if the test style includes a test name suffix, such as "when"
 * @param defaultAffixes if the test style recommends test affixes by default, such as [BehaviorSpec][io.kotest.core.spec.style.BehaviorSpec]
 */
data class TestName(
   val name: String,
   val focus: Boolean,
   val bang: Boolean,
   val prefix: String?,
   val suffix: String?,
   val defaultAffixes: Boolean,
) {
   init {
      require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
      require(!focus || !bang) { "Bang and focus cannot both be true" }
   }
}

data class TestNameBuilder(
   val rawname: String,
   val prefix: String?,
   val suffix: String?,
   val defaultAffixes: Boolean,
) {

   companion object {
      fun builder(rawname: String) = TestNameBuilder(
         rawname = rawname,
         prefix = null,
         suffix = null,
         defaultAffixes = false
      )
   }

   fun withDefaultAffixes(): TestNameBuilder {
      return copy(defaultAffixes = true)
   }

   fun withPrefix(prefix: String): TestNameBuilder {
      return copy(prefix = prefix)
   }

   fun withSuffix(suffix: String): TestNameBuilder {
      return copy(suffix = suffix)
   }

   fun build(): TestName {
      val trimmed = rawname.removeAllExtraWhitespaces()
      val (focus, bang, parsedName) = when {
         trimmed.startsWith("!") -> Triple(first = false, second = true, third = trimmed.drop(1).trim())
         trimmed.startsWith("f:") -> Triple(first = true, second = false, third = trimmed.drop(2).trim())
         else -> Triple(first = false, second = false, third = trimmed)
      }

      return TestName(parsedName, focus, bang, prefix, suffix, defaultAffixes)
   }

   fun String.removeAllExtraWhitespaces() = this.split(Regex("\\s")).filterNot { it == "" }.joinToString(" ")
}
