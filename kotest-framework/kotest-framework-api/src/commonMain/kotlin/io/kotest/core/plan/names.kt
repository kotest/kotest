package io.kotest.core.plan

import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestNameCase
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Models the name of a [TestPlanNode] in the test plan.
 *
 * Each member of this ADT has a [name] for that component only, and a [fullName] which models
 * the name including parent names.
 *
 * This class is a long term replacement for [Description].
 */
@ExperimentalKotest
sealed class NodeName {

   companion object {
      const val NodeNameSeparator = "/"

      /**
       * Returns a [NodeName] for this spec class.
       *
       * If the spec has been annotated with [DisplayName] (on supported platforms), then that will be used,
       * otherwise the default is to use the fully qualified class name.
       *
       * Note: This name must be globally unique. Two specs, even in different packages,
       * cannot share the same name, so if [DisplayName] is used, developers must ensure it does not
       * clash with another spec.
       */
      fun fromSpecClass(kclass: KClass<out Spec>): SpecName {
         val name = kclass.bestName()
         val displayName = kclass.annotation<DisplayName>()?.name ?: name
         val fullName = listOf(EngineName.name, name).joinToString(NodeNameSeparator)
         return SpecName(name, displayName, fullName, name)
      }
   }

   /**
    * The internal name for this node.
    * Does not include any parents. For that, see [fullName].
    */
   abstract val name: String

   /**
    * The name of this node as used for display purposes.
    * This may differ from [name].
    */
   abstract val displayName: String

   /**
    * The full internal name for a node. For example, for a test, this would be the full test path,
    * including any parents, the spec name, and the engine name.
    */
   abstract val fullName: String

   object EngineName : NodeName() {
      override val name: String = "kotest"
      override val displayName: String = "kotest"
      override val fullName: String = "kotest"
   }

   /**
    * Models the name of a spec.
    *
    * The [name] for a spec is usually the class name.
    * The [displayName] is also the class name, unless overriden by [DisplayName].
    *
    */
   data class SpecName(
      override val name: String,
      override val displayName: String,
      override val fullName: String,
      val fqn: String,
   ) : NodeName() {

      /**
       * Returns a new [TestName] by appending the given [name] to this spec name.
       * The name will be parsed for focus, bang.
       */
      fun append(name: String): TestName = parseTestName(fullName, name)
   }

   /**
    * Models the name of a test case.
    *
    * A test name can sometimes have a affixes set automatically from the framework.
    * For example, when using BehaviorSpec, tests can have "given", "when", "then" prepended.
    *
    * The [name] is the internal name, with focus, bang removed and without affixes.
    * The [displayName] is the name as entered by the user, including affixes added by the framework.
    */
   data class TestName(
      override val name: String,
      override val displayName: String,
      override val fullName: String,
      val prefix: String? = null,
      val suffix: String? = null,
      val focus: Boolean = false,
      val bang: Boolean = false,
   ) : NodeName() {

      init {
         require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
         require(fullName.isNotBlank() && fullName.isNotEmpty()) { "Cannot create test with blank or empty fqn" }
         require(!focus || !bang) { "Bang and focus cannot both be true" }
      }

      /**
       * Returns a new [TestName] by appending the given [name] to this name.
       * The name will be parsed for focus, bang.
       */
      fun append(name: String): TestName = parseTestName(fullName, name)
   }
}

/**
 * Creates a new test name from the given name, parsing for focus and bang.
 * Test name case and affixes will use settings from config.
 */
@ExperimentalKotest
fun parseTestName(parent: String, name: String, specAffixDefault: Boolean = false): NodeName.TestName =
   parseTestName(
      parent,
      null,
      name,
      null,
      configuration.testNameCase,
      configuration.removeTestNameWhitespace,
      configuration.includeTestScopeAffixes ?: specAffixDefault
   )

/**
 * Parses a [NodeName.TestName] correctly handling focus, bang, prefix and suffix.
 * If a prefix is specified the focus/bang is moved to before the prefix in the display name.
 *
 * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
 * and ends up with !when disable, so that it is correctly parsed by the test runtime.
 *
 * @param parent the parent full name.
 * @param prefix optional prefix that some specs may specify, such as "Given:"
 * @param name the user supplied name for the test
 * @param suffix optional suffix that some specs may specify such as "should"

 * @param testNameCase a [TestNameCase] parameter to adjust the capitalisation of the display name
 * @param includeAffixesInDisplayName if true then the prefix and/or suffix will be included in the display name.
 * if false, they will not be included in the display name. If null, the default from config will be used.
 */
@ExperimentalKotest
fun parseTestName(
   parent: String,
   prefix: String?,
   name: String,
   suffix: String?,
   testNameCase: TestNameCase,
   removeTestNameWhiteSpec: Boolean,
   includeAffixesInDisplayName: Boolean,
): NodeName.TestName {

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

   return NodeName.TestName(
      name,
      displayName,
      listOf(parent, name).joinToString(NodeName.NodeNameSeparator),
      prefix,
      suffix,
      focus,
      bang
   )
}

private fun String.uncapitalize() =
   this[0].toLowerCase() + substring(1 until this.length)

private fun String.removeAllExtraWhitespaces() = this.split(Regex("\\s")).filterNot { it == "" }.joinToString(" ")
private fun String.removeNewLineCharacter() = this.replace("\n", "").trim()
