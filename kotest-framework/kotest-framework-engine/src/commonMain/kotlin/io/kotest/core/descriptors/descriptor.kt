package io.kotest.core.descriptors

import io.kotest.common.DescriptorPath
import io.kotest.common.KotestInternal

/**
 * A stable, consistent identifier for a test element.
 *
 * A [Descriptor] does not depend on runtime configuration and does not change between test runs
 * (unless the test or a parent test is renamed by the user).
 *
 * Descriptors are a chain of instances, with each instance containing a link to its parent, except
 * for [SpecDescriptor] which is the root of a chain.
 *
 * A descriptor for a test case always consists of a chain of at leaest one [TestDescriptor] instances,
 * with the top most parent being a [SpecDescriptor].
 *
 */
sealed interface Descriptor {

   val id: DescriptorId

   /**
    * Creates a [TestDescriptor] by appending the given name test to this [Descriptor].
    */
   fun append(name: String): TestDescriptor = TestDescriptor(this, DescriptorId(name))

   /**
    * A [Descriptor] for a spec class.
    */
   data class SpecDescriptor(
      override val id: DescriptorId,
   ) : Descriptor

   /**
    * A [Descriptor] for a test.
    */
   data class TestDescriptor(
      val parent: Descriptor,
      override val id: DescriptorId,
   ) : Descriptor

   fun ids(): List<DescriptorId> = when (this) {
      is SpecDescriptor -> listOf(this.id)
      is TestDescriptor -> this.parent.ids() + this.id
   }

   /**
    * Returns a parseable path to the test.
    *
    * @param includeSpec if true then the spec name is included in the path.
    */
   @Deprecated(
      "Use path() without the argument. Deprecated since 6.0",
      ReplaceWith("path()")
   )
   fun path(includeSpec: Boolean = true): DescriptorPath {
      require(includeSpec) { "Paths must always include the spec descriptor since 6.0" }
      return path()
   }

   fun path(): DescriptorPath = DescriptorPaths.render(this)

   @KotestInternal
   fun parts(): List<String> = when (this) {
      is SpecDescriptor -> emptyList()
      is TestDescriptor -> parent.parts() + listOf(this.id.value)
   }

   /**
    * Returns `true` if this descriptor is for a spec class.
    */
   fun isSpec() = this is SpecDescriptor

   /**
    * Returns `true` if this descriptor is for a test case.
    */
   fun isTestCase() = this is TestDescriptor

   /**
    * Returns `true` if this descriptor represents a root test case.
    */
   fun isRootTest() = this is TestDescriptor && this.parent.isSpec()

   /**
    * Returns `true` if this type equals that type. For example
    * if this is a spec and the rhs is also spec
    */
   fun isEqualType(that: Descriptor): Boolean {
      return when (this) {
         is SpecDescriptor -> that.isSpec()
         is TestDescriptor -> that.isTestCase()
      }
   }

   /**
    * Returns the depth of this node, where a [SpecDescriptor] has depth of 0,
    * a root test has depth 1 and so on.
    */
   fun depth() = parents().size - 1

   /**
    * Recursively returns any parent descriptors, with the [SpecDescriptor] being first in the list
    * and this descriptor being last.
    */
   fun parents(): List<Descriptor> = when (this) {
      is SpecDescriptor -> emptyList()
      is TestDescriptor -> parent.parents() + parent
   }

   /**
    * Returns `true` if this [descriptor] is the immediate parent of the given [descriptor].
    */
   fun isParentOf(descriptor: Descriptor): Boolean = when (descriptor) {
      is SpecDescriptor -> false // nothing can be the parent of a spec
      is TestDescriptor -> this == descriptor.parent
   }

   /**
    * Returns `true` if this [descriptor] is ancestor (1..nth-parent) of the given [descriptor].
    */
   fun isAncestorOf(descriptor: Descriptor): Boolean = when (descriptor) {
      is SpecDescriptor -> false // nothing can be an ancestor of a spec
      is TestDescriptor -> isParentOf(descriptor) || isAncestorOf(descriptor.parent)
   }

   /**
    * Returns `true` if this [descriptor] is the immediate child of the given [descriptor].
    */
   fun isChildOf(descriptor: Descriptor): Boolean = descriptor.isParentOf(this)

   /**
    * Returns `true` if this [descriptor] is a child, grandchild, etc of the given [descriptor].
    */
   fun isDescendentOf(descriptor: Descriptor): Boolean = descriptor.isAncestorOf(this)

   /**
    * Returns `true` if this [descriptor] is an ancestor of, or the same as, the given [descriptor].
    */
   @Deprecated(
      "Confusing nomenclature. Use isPrefixOf instead. Deprecated since 6.0.",
      ReplaceWith("isPrefixOf(descriptor)")
   )
   fun isOnPath(descriptor: Descriptor): Boolean = isPrefixOf(descriptor)

   /**
    * Returns `true` if this [descriptor] is an ancestor of, or the same as, the given [descriptor].
    */
   fun isPrefixOf(descriptor: Descriptor): Boolean =
      this == descriptor || this.isAncestorOf(descriptor)

   /**
    * Returns true if this [descriptor] shares a common path with the given [descriptor]. Specifically,
    * this [descriptor] could be a parent or child of the given [descriptor].
    */
   fun hasSharedPath(descriptor: Descriptor): Boolean {
      return this == descriptor || this.isAncestorOf(descriptor) || this.isDescendentOf(descriptor)
   }

   /**
    * Returns the prefix of the descriptor starting with the root (spec)
    */
   fun getTreePrefix(): List<Descriptor> {
      val ret = mutableListOf<Descriptor>()
      var x = this
      loop@ while (true) {
         ret.add(0, x)
         when (x) {
            is SpecDescriptor -> {
               break@loop
            }

            is TestDescriptor -> {
               x = x.parent
            }
         }
      }
      return ret
   }

   /**
    * Returns the [SpecDescriptor] parent for this [Descriptor].
    * If this is already a spec descriptor, then returns itself.
    */
   fun spec(): SpecDescriptor = when (this) {
      is SpecDescriptor -> this
      is TestDescriptor -> this.parent.spec()
   }
}

