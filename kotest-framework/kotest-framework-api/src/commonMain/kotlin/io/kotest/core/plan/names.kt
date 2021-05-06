package io.kotest.core.plan

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.spec.DisplayName as DisplayNameAnno
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestNameCase
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Models the name of a [Node] in the test plan.
 *
 * Each member of this ADT has at least a name that can be used for parsing purposes, and
 * a display name - that may have been customized by the user or the framework - for reporting purposes.
 *
 * This class is a long term replacement for [DescriptionName].
 */
@ExperimentalKotest
sealed class NodeName {

   companion object {

      /**
       * Returns a [SpecName] for the given class.
       *
       * For name, the following is used, in order of preference:
       * - The fully qualified name of the class (JVM)
       * - The simple name of the class (all platforms)
       *
       * For display name, the following are used, in order of preference:
       * - If the spec has been annotated with @DisplayName and annotations are supported on that platform (JVM)
       * - The fully qualified name of the class (JVM)
       * - The simple name of the class
       * - The [toString] on the [kclass] instance.
       *
       * Note: This name must be globally unique. Two specs, even in different packages,
       * cannot share the same names, so if @DisplayName is used, developers must ensure it does not
       * clash with another spec.
       */
      fun fromSpec(kclass: KClass<*>): SpecName {
         val name = kclass.bestName()
         val displayName = kclass.annotation<DisplayNameAnno>()?.name ?: kclass.simpleName ?: this.toString()
         return SpecName(name, displayName)
      }

      @Deprecated("For compatibility until Descriptions are removed")
      internal fun fromTestName(name: DescriptionName.TestName): TestName {
         return TestName(
            name = name.name,
            displayName = name.displayName,
            prefix = null,
            suffix = null,
            focus = name.focus,
            bang = name.bang,
         )
      }

      /**
       * Parses a [TestName] correctly handling focus, bang, prefix and suffix.
       * If a prefix is specified the focus/bang is moved to before the prefix in the display name.
       *
       * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
       * and ends up with !when disable, so that it is correctly parsed by the test runtime.
       *
       * @param prefix optional prefix that some specs may specify, such as "Given:"
       * @param name the user supplied name for the test
       * @param suffix optional suffix that some specs may specify such as "should"

       * @param testNameCase a [TestNameCase] parameter to adjust the capitalisation of the display name
       * @param includeAffixesInDisplayName if true then the prefix and/or suffix will be included in the display name.
       * if false, they will not be included in the display name. If null, the default from config will be used.
       */
      @ExperimentalKotest
      @PublishedApi
      internal fun parseTestName(
         prefix: String?,
         name: String,
         suffix: String?,
         testNameCase: TestNameCase,
         removeTestNameWhiteSpec: Boolean,
         includeAffixesInDisplayName: Boolean,
      ): TestName {

         val trimmedName = if (removeTestNameWhiteSpec) {
            name.removeAllExtraWhitespaces()
         } else {
            name.removeNewLineCharacter()
         }

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

         return TestName(
            name,
            displayName,
            prefix,
            suffix,
            focus,
            bang
         )
      }
   }

   /**
    * A parsable name for this node.
    *
    * This can be used to refer to tests outside of the run, for example, as an identifer
    * in a test reporting system where you want to track the results of a test over time.
    *
    * The intellij plugin also uses the name to refer to tests when invoking individual
    * tests from the IDE.
    *
    * Any particular name is not guaranteed to be unique, but the combination of all names
    * in the form of the [testPath] is guaranteed to be unique.
    */
   abstract val name: String

   /**
    * The name of this node as used for display purposes.
    * This may differ from [name].
    */
   abstract val displayName: String
}

@ExperimentalKotest
object EngineName : NodeName() {
   override val name: String = "kotest"
   override val displayName: String = "kotest"
}

@ExperimentalKotest
data class SpecName(
   override val name: String,
   override val displayName: String,
) : NodeName()

/**
 * Models the name of a test case.
 *
 * A test name can sometimes have affixes set automatically from the framework.
 * For example, when using BehaviorSpec, tests can have "given", "when", "then" prepended.
 *
 * These are contained in the [TestName] as the params [prefix] and [suffix]. They can be null.
 *
 * The [focus] and [bang] fields are set if the user entered the name prefixed with f: or !.
 *
 * The [name] is the internal name, with focus, bang removed and without affixes.
 *
 * The [displayName] is the name as entered by the user, including focus, bang and any added affixes.
 */
@ExperimentalKotest
data class TestName(
   override val name: String,
   override val displayName: String,
   val prefix: String? = null,
   val suffix: String? = null,
   val focus: Boolean = false,
   val bang: Boolean = false,
) : NodeName() {

   init {
      require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
      require(!focus || !bang) { "Bang and focus cannot both be true" }
   }
}

/**
 * Creates a new test name from the given name, parsing for focus and bang.
 * Test name case and affixes will use settings from config.
 */
@ExperimentalKotest
@PublishedApi
internal fun parseTestName(name: String, specAffixDefault: Boolean = false): TestName =
   NodeName.parseTestName(
      null,
      name,
      null,
      configuration.testNameCase,
      configuration.removeTestNameWhitespace,
      configuration.includeTestScopeAffixes ?: specAffixDefault
   )

private fun String.uncapitalize() =
   this[0].toLowerCase() + substring(1 until this.length)

private fun String.removeAllExtraWhitespaces() = this.split(Regex("\\s")).filterNot { it == "" }.joinToString(" ")
private fun String.removeNewLineCharacter() = this.replace("\n", "").trim()
