package io.kotest.core.plan

import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.Spec
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
       * If the spec has been annotated with @DisplayName (on supported platforms), then that will be used,
       * otherwise the default is to use the fully qualified class name.
       *
       * Note: This name must be globally unique. Two specs, even in different packages,
       * cannot share the same name, so if @DisplayName is used, developers must ensure it does not
       * clash with another spec.
       */
      fun fromSpecClass(kclass: KClass<out Spec>): SpecName {
         val name = kclass.annotation<DisplayName>()?.name ?: kclass.bestName()
         val fullName = listOf(EngineName.name, name).joinToString(NodeNameSeparator)
         return SpecName(name, fullName)
      }
   }

   /**
    * The name for this node alone. Does not include any parents. For that, see [fullName].
    */
   abstract val name: String

   /**
    * The full name for a node. For example, for a test, this would be the full test path,
    * including any parents, the spec name, and the engine name.
    */
   abstract val fullName: String

   object EngineName : NodeName() {
      override val name: String = "kotest"
      override val fullName: String = "kotest"
   }

   /**
    * Models the name of a spec.
    *
    * The [name] for a spec is the class name, unless overriden by @DisplayName.
    */
   data class SpecName(
      override val name: String,
      override val fullName: String,
   ) : NodeName() {

      /**
       * Returns a new [TestName] by appending the given [name] to this spec name.
       */
      fun append(name: String): TestName = TestName(name, listOf(fullName, name).joinToString(NodeNameSeparator))
   }

   /**
    * Models the name of a test case.
    *
    * A test case name can sometimes have a prefix and/or suffix set automatically from the framework.
    * For example, when using BehaviorSpec, tests can have "given", "when", "then" prepended.
    *
    * The [name] is the name as entered by the user, including focus and bang prefixes, including
    * any affixes added by the engine.
    *
    * todo decide if we want to include focus/bang/affix information directly here, like description name does
    */
   data class TestName(
      override val name: String,
      override val fullName: String
   ) : NodeName() {

      init {
         require(name.isNotBlank() && name.isNotEmpty()) { "Cannot create test with blank or empty name" }
         require(fullName.isNotBlank() && fullName.isNotEmpty()) { "Cannot create test with blank or empty fqn" }
      }

      /**
       * Returns a new [TestName] by appending the given [name] to this name.
       */
      fun append(name: String): TestName = TestName(name, listOf(fullName, name).joinToString(NodeNameSeparator))
   }
}
