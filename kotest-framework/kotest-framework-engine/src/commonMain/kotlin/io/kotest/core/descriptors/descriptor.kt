package io.kotest.core.descriptors

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor.SpecDescriptor
import io.kotest.core.descriptors.Descriptor.TestDescriptor
import io.kotest.core.names.TestName
import kotlin.reflect.KClass

@Deprecated("Use TestPath from io.kotest.common", ReplaceWith("io.kotest.common.TestPath"))
typealias TestPath = io.kotest.common.TestPath

/**
 * A parseable, stable, consistent identifier for a test element.
 *
 * The id should not depend on runtime configuration and should not change between test runs,
 * unless the test, or a parent test, has been modified by the user.
 */
sealed interface Descriptor {

   val id: DescriptorId

   /**
    * A [Descriptor] for a spec class.
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
    * @param includeSpec if true then the spec name is included in the path.
    */
   fun path(includeSpec: Boolean = true): io.kotest.common.TestPath = when (this) {
      is SpecDescriptor -> if (includeSpec) io.kotest.common.TestPath(this.id.value) else error("Cannot call path on spec with includeSpec=false")
      is TestDescriptor -> when (this.parent) {
         is SpecDescriptor -> when (includeSpec) {
            true -> io.kotest.common.TestPath(this.parent.id.value + SpecDelimiter + this.id.value)
            false -> io.kotest.common.TestPath(this.id.value)
         }
         is TestDescriptor -> io.kotest.common.TestPath(this.parent.path(includeSpec).value + TestDelimiter + this.id.value)
      }
   }

   @KotestInternal
   fun parts(): List<String> = when (this) {
      is SpecDescriptor -> emptyList()
      is TestDescriptor -> parent.parts() + listOf(this.id.value)
   }

   /**
    * Returns `true` if this descriptor is for a class based test file.
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
    * Returns the depth of this node, where the [SpecDescriptor] has depth of 0,
    * a root test has depth 1 and so on.
    */
   fun depth() = parents().size - 1

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
    * Returns `true` if this descriptor is the immediate parent of the given [descriptor].
    */
   fun isParentOf(descriptor: Descriptor): Boolean = when (descriptor) {
      is SpecDescriptor -> false // nothing can be the parent of a spec
      is TestDescriptor -> this == descriptor.parent
   }

   /**
    * Returns `true` if this descriptor is ancestor (1..nth-parent) of the given [descriptor].
    */
   fun isAncestorOf(descriptor: Descriptor): Boolean = when (descriptor) {
      is SpecDescriptor -> false // nothing can be an ancestor of a spec
      is TestDescriptor -> isParentOf(descriptor) || isAncestorOf(descriptor.parent)
   }

   /**
    * Returns `true` if this descriptor is the immediate child of the given [descriptor].
    */
   fun isChildOf(descriptor: Descriptor): Boolean = descriptor.isParentOf(this)

   /**
    * Returns `true` if this descriptor is a child, grandchild, etc of the given [descriptor].
    */
   fun isDescendentOf(descriptor: Descriptor): Boolean = descriptor.isAncestorOf(this)

   /**
    * Returns `true` if this instance is on the path to the given description. That is, if this
    * instance is either an ancestor of, of the same as, the given description.
    */
   fun isOnPath(description: Descriptor): Boolean =
      this == description || this.isAncestorOf(description)

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

data class DescriptorId(
   val value: String,
) {

   /**
    * Treats the lhs and rhs both as wildcard regex one by one and check if it matches the other
    */
   fun wildCardMatch(id: DescriptorId): Boolean {
      val thisRegex = with(this.value) {
         ("\\Q$this\\E").replace("*", "\\E.*\\Q").toRegex()
      }
      val thatRegex = with(id.value) {
         ("\\Q$this\\E").replace("*", "\\E.*\\Q").toRegex()
      }
      return (thisRegex.matches(id.value) || thatRegex.matches(this.value))
   }
}

fun SpecDescriptor.append(name: TestName): TestDescriptor =
   TestDescriptor(this, DescriptorId(name.testName))

fun TestDescriptor.append(name: TestName): TestDescriptor =
   this.append(name.testName)

fun Descriptor.append(name: String): TestDescriptor =
   TestDescriptor(this, DescriptorId(name))

/**
 * Returns the [TestDescriptor] that is the root for this [TestDescriptor].
 * This may be the same descriptor that this method is invoked on, if that descriptor
 * is a root test.
 */
tailrec fun TestDescriptor.root(): TestDescriptor {
   return when (parent) {
      is SpecDescriptor -> this // if my parent is a spec, then I am a root
      is TestDescriptor -> parent.root()
   }
}
