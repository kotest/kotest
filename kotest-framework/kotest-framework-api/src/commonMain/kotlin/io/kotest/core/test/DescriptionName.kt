package io.kotest.core.test

/**
 * An ADT that models the name of a [Description].
 */
sealed class DescriptionName {

   /**
    * Models the name of a spec.
    *
    * @param qualifiedName the full qualified class name of this spec
    */
   data class SpecName(val qualifiedName: String, val displayName: String) : DescriptionName()

   /**
    * Models the name of a test case. A test case can sometimes have a prefix and or/ suffix set
    * eg when using BehaviorSpec or WordSpec.
    *
    * @param prefix optional prefix that some specs may specify, such as "Given:"
    * @param name the user supplied name for the test
    * @param suffix optional suffix that some specs may specify such as "should"
    * @param focus if the test name was specified with f:
    * @param bang if the test name was specified with the ! prefix
    * @param testNameCase a [TestNameCase] parameter to adjust the captialize of the display name
    * @param includeAffixesInDisplayName if true then the prefix and/or suffix will be included in the display name.
    */
   data class TestName(
      val prefix: String?,
      val name: String,
      val suffix: String?,
      val focus: Boolean,
      val bang: Boolean,
      val testNameCase: TestNameCase,
      val includeAffixesInDisplayName: Boolean
   ) : DescriptionName() {

      init {
         require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
         require(!focus || !bang) { "Bang and focus cannot both be true" }
      }

      /**
       * Creates a display name correctly handling focus, bang, prefix and suffix.
       * If a prefix is specified the focus/bang is moved to before the prefix.
       *
       * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
       * and ends up with !when disable, so that it is correctly parsed by the test runtime.
       *
       */
      fun displayName(): String {

         val flattened = name.trim().replace("\n", "")
         val withPrefix = when (includeAffixesInDisplayName) {
            true -> prefix ?: ""
            false -> ""
         }

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

      companion object {

         operator fun invoke(name: String) = invoke(null, name, false)

         operator fun invoke(name: String, includePrefixByDefault: Boolean): TestName =
            invoke(null, name, includePrefixByDefault)

         operator fun invoke(prefix: String?, name: String): TestName =
            invoke(prefix, name, false)

         operator fun invoke(prefix: String?, name: String, includePrefixByDefault: Boolean): TestName {
            return when {
               name.trim().startsWith("!") -> TestName(
                  prefix,
                  name.trim().drop(1).trim(),
                  null,
                  focus = false,
                  bang = true,
                  testNameCase = TestNameCase.AsIs,
                  includeAffixesInDisplayName = includePrefixByDefault,
               )
               name.trim().startsWith("f:") -> TestName(
                  prefix,
                  name.trim().drop(2).trim(),
                  null,
                  focus = true,
                  bang = false,
                  testNameCase = TestNameCase.AsIs,
                  includeAffixesInDisplayName = includePrefixByDefault,
               )
               else -> TestName(
                  prefix,
                  name,
                  null,
                  focus = false,
                  bang = false,
                  testNameCase = TestNameCase.AsIs,
                  includeAffixesInDisplayName = includePrefixByDefault,
               )
            }
         }
      }
   }
}

private fun String.uncapitalize() =
   this[0].toLowerCase() + substring(1 until this.length)
