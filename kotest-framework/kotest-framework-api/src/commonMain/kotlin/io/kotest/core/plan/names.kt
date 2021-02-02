//package io.kotest.core.plan
//
//import io.kotest.core.config.ExperimentalKotest
//import io.kotest.core.config.configuration
//import io.kotest.core.spec.DisplayName
//import io.kotest.core.test.DescriptionName
//import io.kotest.core.test.TestNameCase
//import io.kotest.mpp.annotation
//import io.kotest.mpp.bestName
//import kotlin.reflect.KClass
//
///**
// * Models the name of a [Descriptor] in the test plan.
// *
// * Each member of this ADT has a name that can be used for parsing purposes, and a display name
// * that may have been modified by the user for reporting purposes.
// *
// * This class is a long term replacement for [DescriptionName].
// */
//@ExperimentalKotest
//sealed class Name {
//
//   companion object {
//
//      /**
//       * Returns a [SpecName] for this spec class.
//       *
//       * If the spec has been annotated with [DisplayName] (on supported platforms), then that will be used,
//       * otherwise the default is to use the fully qualified class name.
//       *
//       * Note: This name must be globally unique. Two specs, even in different packages,
//       * cannot share the same names, so if [DisplayName] is used, developers must ensure it does not
//       * clash with another spec.
//       */
//      fun fromSpecClass(kclass: KClass<*>): SpecName {
//         val name = kclass.bestName()
//         val displayName = kclass.annotation<DisplayName>()?.name ?: kclass.simpleName ?: this.toString()
//         return SpecName(name, displayName)
//      }
//
//      /**
//       * Returns a [ScriptName] for a script class.
//       *
//       * If the spec has been annotated with [DisplayName] (on supported platforms), then that will be used,
//       * otherwise the default is to use the fully qualified class name.
//       *
//       * Note: This name must be globally unique. Two specs, even in different packages,
//       * cannot share the same names, so if [DisplayName] is used, developers must ensure it does not
//       * clash with another spec.
//       */
//      fun fromScriptClass(kclass: KClass<*>): ScriptName {
//         val name = kclass.bestName()
//         val displayName = kclass.simpleName ?: this.toString()
//         return ScriptName(name, displayName)
//      }
//   }
//
//   /**
//    * A parsable name for this node.
//    */
//   abstract val name: String
//
//   /**
//    * The name of this node as used for display purposes.
//    * This may differ from [name].
//    */
//   abstract val displayName: String
//
//   object EngineName : Name() {
//      override val name: String = "kotest"
//      override val displayName: String = "kotest"
//   }
//
//   /**
//    * Models the name of a script.
//    *
//    * @param name the fully qualified class name.
//    * @param displayName the simple class name.
//    *
//    */
//   data class ScriptName(
//      override val name: String,
//      override val displayName: String,
//   ) : Name()
//
//   /**
//    * Models the name of a spec.
//    *
//    * @param name the fully qualified class name.
//    * @param displayName short class name, unless overriden by the [DisplayName] annotation.
//    *
//    */
//   data class SpecName(
//      override val name: String,
//      override val displayName: String,
//   ) : Name()
//
//   /**
//    * Models the name of a test case.
//    *
//    * A test name can sometimes have a affixes set automatically from the framework.
//    * For example, when using BehaviorSpec, tests can have "given", "when", "then" prepended.
//    *
//    * The [name] is the internal name, with focus, bang removed and without affixes.
//    * The [displayName] is the name as entered by the user, including affixes added by the framework.
//    */
//   data class TestName(
//      override val name: String,
//      override val displayName: String,
//      val prefix: String? = null,
//      val suffix: String? = null,
//      val focus: Boolean = false,
//      val bang: Boolean = false,
//   ) : Name() {
//
//      init {
//         require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
//         require(!focus || !bang) { "Bang and focus cannot both be true" }
//      }
//   }
//}
//
///**
// * Creates a new test name from the given name, parsing for focus and bang.
// * Test name case and affixes will use settings from config.
// */
//@ExperimentalKotest
//fun parseTestName(name: String, specAffixDefault: Boolean = false): Name.TestName =
//   parseTestName(
//      null,
//      name,
//      null,
//      configuration.testNameCase,
//      configuration.removeTestNameWhitespace,
//      configuration.includeTestScopeAffixes ?: specAffixDefault
//   )
//
///**
// * Parses a [Name.TestName] correctly handling focus, bang, prefix and suffix.
// * If a prefix is specified the focus/bang is moved to before the prefix in the display name.
// *
// * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
// * and ends up with !when disable, so that it is correctly parsed by the test runtime.
// *
// * @param prefix optional prefix that some specs may specify, such as "Given:"
// * @param name the user supplied name for the test
// * @param suffix optional suffix that some specs may specify such as "should"
//
// * @param testNameCase a [TestNameCase] parameter to adjust the capitalisation of the display name
// * @param includeAffixesInDisplayName if true then the prefix and/or suffix will be included in the display name.
// * if false, they will not be included in the display name. If null, the default from config will be used.
// */
//@ExperimentalKotest
//fun parseTestName(
//   prefix: String?,
//   name: String,
//   suffix: String?,
//   testNameCase: TestNameCase,
//   removeTestNameWhiteSpec: Boolean,
//   includeAffixesInDisplayName: Boolean,
//): Name.TestName {
//
//   val trimmedName = if (removeTestNameWhiteSpec) {
//      name.removeAllExtraWhitespaces()
//   } else {
//      name.removeNewLineCharacter()
//   }
//
//   val (focus, bang, croppedName) = when {
//      trimmedName.startsWith("!") -> Triple(first = false, second = true, third = trimmedName.drop(1).trim())
//      trimmedName.startsWith("f:") -> Triple(first = true, second = false, third = trimmedName.drop(2).trim())
//      else -> Triple(first = false, second = false, third = trimmedName)
//   }
//
//   val withPrefix = when (includeAffixesInDisplayName) {
//      true -> prefix ?: ""
//      false -> ""
//   }
//
//   val displayName = if (withPrefix.isBlank()) {
//      when (testNameCase) {
//         TestNameCase.Sentence -> croppedName.capitalize()
//         TestNameCase.InitialLowercase -> croppedName.uncapitalize()
//         TestNameCase.Lowercase -> croppedName.toLowerCase()
//         else -> croppedName
//      }
//   } else {
//      when (testNameCase) {
//         TestNameCase.Sentence -> "${withPrefix.capitalize()}${croppedName.uncapitalize()}"
//         TestNameCase.InitialLowercase -> "${withPrefix.uncapitalize()}${croppedName.uncapitalize()}"
//         TestNameCase.Lowercase -> "${withPrefix.toLowerCase()}${croppedName.toLowerCase()}"
//         else -> "$withPrefix$croppedName"
//      }
//   }
//
//   return Name.TestName(
//      name,
//      displayName,
//      prefix,
//      suffix,
//      focus,
//      bang
//   )
//}
//
//private fun String.uncapitalize() =
//   this[0].toLowerCase() + substring(1 until this.length)
//
//private fun String.removeAllExtraWhitespaces() = this.split(Regex("\\s")).filterNot { it == "" }.joinToString(" ")
//private fun String.removeNewLineCharacter() = this.replace("\n", "").trim()
