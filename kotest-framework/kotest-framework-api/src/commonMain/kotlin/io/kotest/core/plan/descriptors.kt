@file:Suppress("PropertyName")

package io.kotest.core.plan

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestType
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

sealed class Descriptor {

   abstract val id: DescriptorId

   abstract val displayName: DisplayName

   /**
    * A [Descriptor] for a top level container of tests, for instance a class, an object
    * or a Kotlin Script. In Kotest these top level containers are called Specs.
    *
    * @param kclass the KClass reference for the spec.
    * @param displayName formatted name for the spec
    * @param type specs can be three types - classes, objects or scripts
    */
   data class SpecDescriptor(
      val kclass: KClass<*>,
      override val displayName: DisplayName,
      val type: SpecType,
   ) : Descriptor() {

      companion object {
         operator fun invoke(spec: Spec): SpecDescriptor = invoke(spec::class)

         operator fun invoke(kclass: KClass<*>): SpecDescriptor = SpecDescriptor(
            kclass,
            kclass.displayName(),
            SpecType.Class,
         )

         fun script(kclass: KClass<*>): SpecDescriptor = SpecDescriptor(
            kclass,
            kclass.displayName(),
            SpecType.Script,
         )
      }

      override val id: DescriptorId = DescriptorId(kclass.bestName())
   }

   /**
    * References a test at runtime.
    *
    * A test may allow other nested tests, in which case its type is set to [TestType.Container].
    * If a test does not allow nested tests, then it's type is [TestType.Test].
    *
    * Note: Just because a type has the value container does not mean it contains nested tests. It just means
    * that the DSL permits it to accept nested tests.
    *
    * @param parent nested tests have a link to their parent test descriptor
    * @param name
    * @param displayName a formatted version of this test's name for use in reports or displays
    */
   data class TestDescriptor(
      val parent: Descriptor,
      val name: TestName,
      override val displayName: DisplayName,
      val type: TestType,
   ) : Descriptor() {

      override val id: DescriptorId = DescriptorId(name.testName)

      fun spec(): SpecDescriptor = when (parent) {
         is TestDescriptor -> parent.spec()
         is SpecDescriptor -> parent
      }

      fun displayPath(includeSpec: Boolean, separator: String = " "): DisplayPath = when (parent) {
         is SpecDescriptor ->
            if (includeSpec)
               DisplayPath(parent.displayName.value).append(this.displayName.value, separator)
            else
               DisplayPath(this.displayName.value)
         is TestDescriptor -> parent.displayPath(includeSpec).append(this.displayName.value, separator)
      }

      fun testPath(includeSpec: Boolean = false): TestPath = when (parent) {
         is SpecDescriptor ->
            if (includeSpec)
               TestPath(parent.kclass.bestName()).append(name.testName)
            else
               TestPath(name.testName)
         is TestDescriptor -> TestPath(name.testName)
      }

      /**
       * Returns true if this test is a top level test.
       *
       * A top level test is one that is defined at the 'root' of a spec, or, in other words,
       * has no test parent.
       */
      fun isTopLevel() = parent is SpecDescriptor
   }

   fun append(name: TestName, displayName: DisplayName, type: TestType): TestDescriptor {
      return TestDescriptor(this, name, displayName, type)
   }

   fun ids(): List<DescriptorId> = when (this) {
      is SpecDescriptor -> listOf(id)
      is TestDescriptor -> parent.ids() + this.id
   }

   /**
    * Returns 0 for a spec, 1 for a top level test and so on.
    */
   fun depth(): Int = when (this) {
      is SpecDescriptor -> 0
      is TestDescriptor -> this.parent.depth() + 1
   }

   /**
    * Returns true if this descriptor is the immediate parent of the given [descriptor].
    */
   fun isParentOf(descriptor: Descriptor): Boolean = when (descriptor) {
      // nothing can be the parent of a spec
      // only the engine can be the parent of a spec
      is SpecDescriptor -> false
      is TestDescriptor -> this.id == descriptor.parent.id
   }

   /**
    * Returns true if this descriptor is ancestor (1..nth-parent) of the given [descriptor].
    */
   fun isAncestorOf(descriptor: Descriptor): Boolean = when (descriptor) {
      // only the engine can be a ancestor of a spec or script
      is SpecDescriptor -> false //this is EngineDescriptor
      is TestDescriptor -> isParentOf(descriptor) || isAncestorOf(descriptor.parent)
   }

   /**
    * Returns true if this descriptor is the immediate child of the given [descriptor].
    */
   fun isChildOf(descriptor: Descriptor): Boolean = when (this) {
      is SpecDescriptor -> false //descriptor is EngineDescriptor
      is TestDescriptor -> parent.id == descriptor.id
   }

   /**
    * Returns true if this [descriptor] is a child, grandchild, etc of the given [descriptor].
    */
   fun isDescendentOf(descriptor: Descriptor): Boolean = when (this) {
      // a spec can only be a descendant of the engine
      is SpecDescriptor -> false // this is EngineDescriptor
      is TestDescriptor -> isChildOf(descriptor) || descriptor.isDescendentOf(descriptor)
   }

   /**
    * Returns true if this instance is on the path to the given [descriptor]. That is, if this
    * instance is either an ancestor of, of the same as, the given descriptor.
    * Ignores test prefixes when comparing.
    */
   fun isOnPath(descriptor: Descriptor): Boolean = this.isDescendentOf(descriptor) || this.isAncestorOf(descriptor)
}

/**
 * Returns a [SpecDescriptor] for this kclass.
 *
 * If the spec has been annotated with @DisplayName (on supported platforms), then that will be used,
 * otherwise the default is to use the fully qualified class name.
 *
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name, so if @DisplayName is used, developers must ensure it does not
 * clash with another spec.
 */
fun KClass<*>.toDescriptor(): Descriptor.SpecDescriptor = Descriptor.SpecDescriptor(this)

//   /**
//    * Returns true if this descriptor is a container of other descriptors.
//    */
//   fun isContainer() = when (this) {
//      is SpecDescriptor -> true
//      is TestDescriptor -> this.type == TestType.Container
//   }
//
//   private val _chain by lazy {
//      when (this) {
//         is SpecDescriptor -> listOf(this)
//         is TestDescriptor -> parent.chain() + listOf(this)
//      }
//   }
//
//   /**
//    * Returns all [Descriptor]s from the spec to this test, with the spec as the first element,
//    * and this descriptor as the last.
//    */
//   fun chain(): List<Descriptor> = _chain
//
//   /**
//    * Returns the depth of this description, where a [Spec] is 0, and a root test is 1, and so on.
//    */
//   fun depth() = parents().size
//
//   private val _parents by lazy {
//      when (this) {
//         is SpecDescriptor -> emptyList()
//         is TestDescriptor -> parent.parents() + listOf(this)
//      }
//   }
//
//   /**
//    * Returns all test parents of this description.
//    */
//   fun parents(): List<Descriptor> = _parents

//   fun testNames() = names().filterIsInstance<TestName>()

//   private val _names by lazy { chain().map { it.name } }

//   /**
//    * Returns the [SpecDescriptor] parent for this descriptor, if any.
//    *
//    * If this descriptor is a [TestDescriptor] then the ultimate parent spec will be returned.
//    * If this descriptor is a [SpecDescriptor], then this function will return itself.
//    * If this descriptor is an [EngineDescriptor], then this function will return null.
//    */
//   fun spec(): SpecDescriptor? = when (this) {
//      is SpecDescriptor -> this
//      is TestDescriptor -> parent.spec()
//   }
//

//
//   private val _ids by lazy {
//      when (this) {
//         is SpecDescriptor -> listOf(id)
//         is TestDescriptor -> parent.ids() + id
//      }
//   }

data class DisplayName(val value: String)

data class DisplayPath(val value: String) {
   fun append(other: String, separator: String): DisplayPath = DisplayPath("$value$separator$other")
}

data class DescriptorId(val value: String)

enum class SpecType {
   Class, Script
}
