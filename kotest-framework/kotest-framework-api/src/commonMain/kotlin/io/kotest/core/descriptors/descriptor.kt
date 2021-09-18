package io.kotest.core.descriptors

import io.kotest.core.descriptors.Descriptor.SpecDescriptor
import io.kotest.core.descriptors.Descriptor.TestDescriptor
import io.kotest.core.names.TestName
import kotlin.reflect.KClass

/**
 * A parseable, stable, consistent identifer for this test element.
 *
 * The id should not depend on runtime configuration and should not change between test runs,
 * unless the test, or a parent test, has been modified by the user.
 */
sealed interface Descriptor {

   val id: DescriptorId

   /**
    * A [Descriptor] for a spec class or a script file.
    */
   data class SpecDescriptor(
      override val id: DescriptorId,
      val kclass: KClass<*>,
   ) : Descriptor

   /**
    * A [Descriptor] for a test.
    */
   data class TestDescriptor(
      val parent: Descriptor,
      override val id: DescriptorId,
   ) : Descriptor

   companion object {
      const val SpecDelimiter = "/"
      const val TestDelimiter = " -- "
   }

   fun ids(): List<DescriptorId> = when (this) {
      is SpecDescriptor -> listOf(this.id)
      is TestDescriptor -> this.parent.ids() + this.id
   }

   /**
    * Returns a parseable path to the test.
    *
    * Includes the spec, and all parent tests.
    */
   fun path(): TestPath = when (this) {
      is SpecDescriptor -> TestPath(this.id.value)
      is TestDescriptor -> when (this.parent) {
         is SpecDescriptor -> TestPath(parent.id.value + SpecDelimiter + this.id.value)
         is TestDescriptor -> TestPath(parent.path().value + TestDelimiter + this.id.value)
      }
   }

   /**
    * Returns true if this descriptor is for a class based test file.
    */
   fun isSpec() = this is SpecDescriptor

   /**
    * Returns true if this descriptor is for a test case.
    */
   fun isTestCase() = this is TestDescriptor

   /**
    * Returns true if this descriptor represents a root test case.
    */
   fun isRootTest() = this is TestDescriptor && this.parent.isSpec()

   /**
    * Returns the depth of this node, where the [SpecDescriptor] has depth of 0,
    * a root test has depth 1 and so on.
    */
   fun depth() = parents().size

   /**
    * Recursively returns any parent descriptors, with the spec being first in the list
    * and this being last.
    */
   fun parents(): List<Descriptor> = when (this) {
      is SpecDescriptor -> emptyList()
      is TestDescriptor -> parent.parents() + parent
   }

   fun chain() = parents() + this

   /**
    * Returns true if this descriptor is the immediate parent of the given [descriptor].
    */
   fun isParentOf(descriptor: Descriptor): Boolean = when (descriptor) {
      is SpecDescriptor -> false // nothing can be the parent of a spec
      is TestDescriptor -> this.id == descriptor.parent.id
   }

   /**
    * Returns true if this descriptor is ancestor (1..nth-parent) of the given [descriptor].
    */
   fun isAncestorOf(descriptor: Descriptor): Boolean = when (descriptor) {
      is SpecDescriptor -> false // nothing can be an ancestor of a spec
      is TestDescriptor -> this.id == descriptor.parent.id || isAncestorOf(descriptor.parent)
   }

   /**
    * Returns true if this descriptor is the immediate child of the given [descriptor].
    */
   fun isChildOf(descriptor: Descriptor): Boolean = descriptor.isParentOf(this)

   /**
    * Returns true if this descriptor is a child, grandchild, etc of the given [descriptor].
    */
   fun isDescendentOf(descriptor: Descriptor): Boolean = descriptor.isAncestorOf(this)

   /**
    * Returns true if this instance is on the path to the given description. That is, if this
    * instance is either an ancestor of, of the same as, the given description.
    * Ignores test prefixes when comparing.
    */
   fun isOnPath(description: Descriptor): Boolean =
      this.path() == description.path() || this.isAncestorOf(description)
}

data class DescriptorId(val value: String)

data class TestPath(val value: String)

fun SpecDescriptor.append(name: TestName): TestDescriptor =
   TestDescriptor(this, DescriptorId(name.testName))

fun TestDescriptor.append(name: TestName): TestDescriptor =
   this.append(name.testName)

fun Descriptor.append(name: String): TestDescriptor =
   TestDescriptor(this, DescriptorId(name))

/**
 * Returns the [SpecDescriptor] parent for this [TestDescriptor].
 */
fun TestDescriptor.spec(): SpecDescriptor = when (parent) {
   is SpecDescriptor -> parent
   is TestDescriptor -> parent.spec()
}
