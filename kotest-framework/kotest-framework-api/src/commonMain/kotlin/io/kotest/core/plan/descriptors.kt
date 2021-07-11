@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.kotest.core.plan

import io.kotest.common.ExperimentalKotest
import io.kotest.core.SourceRef
import io.kotest.core.plan.Descriptor.EngineDescriptor
import io.kotest.core.plan.Descriptor.SpecDescriptor
import io.kotest.core.plan.Descriptor.TestDescriptor
import io.kotest.core.script.ScriptSpec
import io.kotest.core.test.Description
import io.kotest.core.test.TestId
import io.kotest.core.test.TestPath
import io.kotest.core.test.TestType
import io.kotest.mpp.bestName
import io.kotest.mpp.qualifiedNameOrNull
import kotlin.reflect.KClass

/**
 * A [Descriptor] is an ADT that represents nodes in the [TestPlan] tree.
 *
 * There are four types of descriptors - a single [EngineDescriptor] at the root,
 * with [SpecDescriptor]s  as direct children of the engine.
 *
 * Then [TestDescriptor]s are children of the spec descriptors and comprise leaf nodes.
 *
 * This class is intended as a long term replacement for [Description].
 *
 * For example:
 *
 * - kotest
 *   - spec
 *     - container
 *       - test
 *       - test
 *   - spec
 *     - test
 *     - container
 *       - container
 *         - test
 */
@ExperimentalKotest
sealed class Descriptor {

   companion object {

      /**
       * Returns a [SpecDescriptor] for a spec class.
       *
       * If the spec has been annotated with [DisplayName] (on supported platforms), then that will be used
       * for the display name, otherwise the default is to use the fully qualified class name.
       *
       * Note: The display name must be globally unique. Two specs, even in different packages,
       * cannot share the same names, so if [DisplayName] is used, developers must ensure it does not
       * clash with another spec.
       */
      fun fromSpecClass(kclass: KClass<*>): SpecDescriptor {
         val name = kclass.bestName()
         val display = kclass.displayName()
         return SpecDescriptor(
            name = Name(name),
            displayName = DisplayName(display),
            classname = kclass.qualifiedNameOrNull(),
            script = false,
            source = Source.ClassSource(kclass.bestName() + ".kt")
         )
      }

      /**
       * Returns a [SpecDescriptor] for a script file.
       */
      fun fromScriptClass(kclass: KClass<*>): SpecDescriptor {
         val name = kclass.bestName()
         val display = kclass.simpleName ?: this.toString()
         val fqn = kclass.qualifiedNameOrNull()
         return SpecDescriptor(
            name = Name(name),
            displayName = DisplayName(display),
            classname = fqn,
            script = true,
            source = Source.ClassSource(kclass.bestName() + ".kt")
         )
      }
   }

   /**
    * Returns an ephemeral id that can be used to uniquely refer to this test during a test run.
    * In 4.4 this will be the test name without special characters, but to allow for proper unicode
    * tests, this will change to a numeric id. Do not rely on the format
    */
   @ExperimentalKotest
   fun id(): TestId = TestId(name.value.replace("[^a-zA-Z0-9]".toRegex(), "_"))

   /**
    * Returns a parsable name for this descriptor.
    */
   abstract val name: Name

   /**
    * Returns a human readable name used for reports and displays.
    */
   abstract val displayName: DisplayName

   /**
    * Returns true if this descriptor is for a class based test file.
    */
   fun isSpec() = this is SpecDescriptor

   /**
    * Returns true if this descriptor is the root engine node.
    */
   fun isEngine() = this is EngineDescriptor

   /**
    * Returns true if this descriptor is for a root test case.
    */
   fun isTestCase() = this is TestDescriptor

   /**
    * Returns true if this descriptor represents a root test case.
    */
   fun isRootTest() = this is TestDescriptor && this.parent.isSpec()

   /**
    * Returns the depth of this node, where the [EngineDescriptor] is depth 0, a [SpecDescriptor] is depth 1, and so on.
    */
   fun depth() = parents().size

   /**
    * Returns true if this descriptor is a container of other descriptors.
    */
   fun isContainer() = when (this) {
      EngineDescriptor -> true
      is SpecDescriptor -> true
      is TestDescriptor -> this.type == TestType.Container
   }

   /**
    * Recursively returns any parent descriptors.
    */
   fun parents(): List<Descriptor> = when (this) {
      EngineDescriptor -> emptyList()
      is SpecDescriptor -> listOf(EngineDescriptor)
      is TestDescriptor -> parent.parents() + parent
   }

   /**
    * Returns the [SpecDescriptor] parent for this descriptor, if any.
    *
    * If this descriptor is itself a spec, then this function will return itself.
    *
    * If this descriptor is the [EngineDescriptor], a [ScriptDescriptor], or a [TestDescriptor] located
    * inside a script, then this function returns null.
    */
   fun spec(): SpecDescriptor? = when (this) {
      EngineDescriptor -> null
      is SpecDescriptor -> this
      is TestDescriptor -> parent.spec()
   }

   /**
    * Returns true if this descriptor is the immediate parent of the given [descriptor].
    */
   fun isParentOf(descriptor: Descriptor): Boolean = when (descriptor) {
      // nothing can be the parent of the engine
      EngineDescriptor -> false
      // only the engine can be the parent of a spec
      is SpecDescriptor -> this is EngineDescriptor
      is TestDescriptor -> this.id() == descriptor.parent.id()
   }

   /**
    * Returns true if this descriptor is ancestor (1..nth-parent) of the given [descriptor].
    */
   fun isAncestorOf(descriptor: Descriptor): Boolean = when (descriptor) {
      // nothing can be the ancestor of the engine
      EngineDescriptor -> false
      // only the engine can be a ancestor of a spec or script
      is SpecDescriptor -> this is EngineDescriptor
      is TestDescriptor -> isParentOf(descriptor) || isAncestorOf(descriptor.parent)
   }

   /**
    * Returns true if this descriptor is the immediate child of the given [descriptor].
    */
   fun isChildOf(descriptor: Descriptor): Boolean = when (this) {
      EngineDescriptor -> false
      is SpecDescriptor -> descriptor is EngineDescriptor
      is TestDescriptor -> parent.id() == descriptor.id()
   }

   /**
    * Returns true if this node is a child, grandchild, etc of the given [descriptor].
    */
   fun isDescendentOf(descriptor: Descriptor): Boolean = when (this) {
      // the engine cannot be a descendant of any other node
      EngineDescriptor -> false
      // a spec can only be a descendant of the engine
      is SpecDescriptor -> this is EngineDescriptor
      is TestDescriptor -> isChildOf(descriptor) || descriptor.isDescendentOf(descriptor)
   }

   /**
    * Returns true if this node is part of the path to the given [descriptor]. That is, if this
    * instance is either an ancestor of, of the same as, the given node.
    */
   fun contains(descriptor: Descriptor): Boolean = this.id() == descriptor.id() || this.isAncestorOf(descriptor)

   /**
    * Returns a parseable path to the test.
    * Includes the engine, spec or script, and all parent tests.
    */
   fun testPath(): TestPath = when (this) {
      EngineDescriptor -> TestPath(this.name.value)
      is SpecDescriptor -> EngineDescriptor.testPath().append(this.name.value)
      is TestDescriptor -> parent.testPath().append(this.name.value)
   }

   abstract fun append(name: Name, displayName: DisplayName, type: TestType, source: Source.TestSource): TestDescriptor

   object EngineDescriptor : Descriptor() {
      override val name: Name = Name("kotest")
      override val displayName: DisplayName = DisplayName("kotest")
      override fun append(name: Name, displayName: DisplayName, type: TestType, source: Source.TestSource) =
         error("Cannot register a test on the engine")
   }

   /**
    * A [Descriptor] for a spec class or a script file.
    *
    * @param name the fully qualified class name if available, otherwise the simple class name
    * @param displayName the simple class name unless overriden by a @DisplayName annotation. Note, only spec
    * classes are able to specify a display name annotation.
    * @param classname the fully qualified class name if available, or null.
    * @param script true if this spec is a kotlin script.
    */
   data class SpecDescriptor(
      override val name: Name,
      override val displayName: DisplayName,
      val classname: String?,
      val script: Boolean,
      val source: Source.ClassSource,
   ) : Descriptor() {
      override fun append(name: Name, displayName: DisplayName, type: TestType, source: Source.TestSource) =
         TestDescriptor(this, name, displayName, type, source)
   }

   data class TestDescriptor(
      val parent: Descriptor,
      override val name: Name,
      override val displayName: DisplayName,
      val type: TestType,
      val source: Source.TestSource,
   ) : Descriptor() {
      override fun append(
         name: Name,
         displayName: DisplayName,
         type: TestType,
         source: Source.TestSource
      ): TestDescriptor {
         if (this@TestDescriptor.type == TestType.Test) error("Cannot register test on TestType.Test")
         return TestDescriptor(this, name, displayName, type, source)
      }
   }
}

expect fun KClass<*>.displayName(): String

/**
 * Creates a [Descriptor] from the deprecated descriptions.
 */
fun Description.toDescriptor(sourceRef: SourceRef): Descriptor {
   return when (this) {
      is Description.Spec ->
         if (this.kclass.bestName() == ScriptSpec::class.bestName())
            Descriptor.fromScriptClass(this.kclass)
         else
            Descriptor.fromSpecClass(this.kclass)
      is Description.Test -> Descriptor.TestDescriptor(
         parent = this.parent.toDescriptor(sourceRef),
         name = Name(this.name.name),
         displayName = DisplayName(this.name.displayName),
         type = this.type,
         source = Source.TestSource(sourceRef.fileName, sourceRef.lineNumber)
      )
   }
}

data class Name(val value: String)
data class DisplayName(val value: String)

fun TestPath.append(component: String) = TestPath(listOf(this.value, component).joinToString("/"))
