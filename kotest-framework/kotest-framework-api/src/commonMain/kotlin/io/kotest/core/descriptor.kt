package io.kotest.core

import io.kotest.core.plan.DisplayName
import io.kotest.core.plan.displayName
import io.kotest.core.test.TestPath
import io.kotest.core.test.TestType
import io.kotest.mpp.bestName
import io.kotest.mpp.qualifiedNameOrNull
import kotlin.reflect.KClass

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
         val display = kclass.displayName() ?: kclass.simpleName ?: this.toString()
         return SpecDescriptor(
            id = DescriptorId(kclass.bestName()),
            displayName = DisplayName(display),
            qualifiedName = kclass.qualifiedNameOrNull(),
            type = SpecType.Class,
         )
      }

      /**
       * Returns a [SpecDescriptor] for a script file.
       */
      fun fromScriptClass(kclass: KClass<*>): SpecDescriptor {
         val display = kclass.simpleName ?: this.toString()
         val fqn = kclass.qualifiedNameOrNull()
         return SpecDescriptor(
            id = DescriptorId(kclass.bestName()),
            displayName = DisplayName(display),
            qualifiedName = fqn,
            type = SpecType.Script,
         )
      }
   }

   /**
    * A parseable, consistent identifer for a test or spec.
    *
    * The id should not depend on runtime configuration and should not change between test runs,
    * unless the test, or a parent test, has been modified by the user.
    */
   abstract val id: DescriptorId

   /**
    * Returns a name suitable for display or reporting purposes.
    * May be ephemeral.
    */
   abstract val displayName: DisplayName

   /**
    * Returns true if this descriptor is for a class based test file.
    */
   fun isSpec() = this is SpecDescriptor

   /**
    * Returns true if this descriptor is for a root test case.
    */
   fun isTestCase() = this is TestDescriptor

   /**
    * Returns true if this descriptor represents a root test case.
    */
   fun isRootTest() = this is TestDescriptor && this.parent.isSpec()

   /**
    * Returns the depth of this node, where the [SpecDescriptor] is depth 0, a root test is depth 1 and so on.
    */
   fun depth() = parents().size

//   /**
//    * Returns true if this descriptor is a container of other descriptors.
//    */
//   fun isContainer() = when (this) {
//      EngineDescriptor -> true
//      is SpecDescriptor -> true
//      is TestDescriptor -> this.type == TestType.Container
//   }

   /**
    * Recursively returns any parent descriptors.
    */
   fun parents(): List<Descriptor> = when (this) {
      is SpecDescriptor -> listOf(this)
      is TestDescriptor -> parent.parents() + parent
   }

//   /**
//    * Returns the [SpecDescriptor] parent for this descriptor, if any.
//    *
//    * If this descriptor is itself a spec, then this function will return itself.
//    *
//    * If this descriptor is the [EngineDescriptor], a [ScriptDescriptor], or a [TestDescriptor] located
//    * inside a script, then this function returns null.
//    */
//   fun spec(): SpecDescriptor? = when (this) {
//      is SpecDescriptor -> this
//      is TestDescriptor -> parent.spec()
//   }
//
//   /**
//    * Returns true if this descriptor is the immediate parent of the given [descriptor].
//    */
//   fun isParentOf(descriptor: Descriptor): Boolean = when (descriptor) {
//      // nothing can be the parent of the engine
//      EngineDescriptor -> false
//      // only the engine can be the parent of a spec
//      is SpecDescriptor -> this is EngineDescriptor
//      is TestDescriptor -> this.id() == descriptor.parent.id()
//   }
//
//   /**
//    * Returns true if this descriptor is ancestor (1..nth-parent) of the given [descriptor].
//    */
//   fun isAncestorOf(descriptor: Descriptor): Boolean = when (descriptor) {
//      // nothing can be the ancestor of the engine
//      EngineDescriptor -> false
//      // only the engine can be a ancestor of a spec or script
//      is SpecDescriptor -> this is EngineDescriptor
//      is TestDescriptor -> isParentOf(descriptor) || isAncestorOf(descriptor.parent)
//   }
//
//   /**
//    * Returns true if this descriptor is the immediate child of the given [descriptor].
//    */
//   fun isChildOf(descriptor: Descriptor): Boolean = when (this) {
//      is SpecDescriptor -> descriptor is EngineDescriptor
//      is TestDescriptor -> parent.id() == descriptor.id()
//   }
//
//   /**
//    * Returns true if this node is a child, grandchild, etc of the given [descriptor].
//    */
//   fun isDescendentOf(descriptor: Descriptor): Boolean = when (this) {
//      // the engine cannot be a descendant of any other node
//      EngineDescriptor -> false
//      // a spec can only be a descendant of the engine
//      is SpecDescriptor -> this is EngineDescriptor
//      is TestDescriptor -> isChildOf(descriptor) || descriptor.isDescendentOf(descriptor)
//   }
//
//   /**
//    * Returns true if this node is part of the path to the given [descriptor]. That is, if this
//    * instance is either an ancestor of, of the same as, the given node.
//    */
//   fun contains(descriptor: Descriptor): Boolean = this.id() == descriptor.id() || this.isAncestorOf(descriptor)
//

   fun ids(): List<DescriptorId> = when (this) {
      is SpecDescriptor -> listOf(this.id)
      is TestDescriptor -> this.parent.ids() + this.id
   }

   /**
    * Returns a parseable path to the test.
    * Includes the spec or script, and all parent tests.
    */
   fun testPath(): TestPath = TestPath(ids().joinToString("/"))

//   abstract fun append(name: Name, displayName: DisplayName, type: TestType, source: Source.TestSource): TestDescriptor

   /**
    * A [Descriptor] for a spec class or a script file.
    *
    * @param displayName a name suitable for reporting
    * @param qualifiedName the fully qualified class name if available, or null.
    * @param type true if this spec is a kotlin script.
    */
   data class SpecDescriptor(
      override val id: DescriptorId,
      override val displayName: DisplayName,
      val qualifiedName: String?,
      val type: SpecType,
   ) : Descriptor() {
//      override fun append(name: Name, displayName: DisplayName, type: TestType, source: Source.TestSource) =
//         TestDescriptor(this, name, displayName, type, source)
   }

   data class TestDescriptor(
      override val id: DescriptorId,
      val parent: Descriptor,
      override val displayName: DisplayName,
      val type: TestType,
   ) : Descriptor() {
//      override fun append(
//         name: Name,
//         displayName: DisplayName,
//         type: TestType,
//         source: Source.TestSource
//      ): TestDescriptor {
//         if (this@TestDescriptor.type == TestType.Test) error("Cannot register test on TestType.Test")
//         return TestDescriptor(this, name, displayName, type, source)
//      }
   }
}

enum class SpecType {
   Class, Script
}

data class DescriptorId(val value: String)
