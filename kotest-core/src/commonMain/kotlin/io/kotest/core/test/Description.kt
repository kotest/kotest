@file:Suppress("PrivatePropertyName")

package io.kotest.core.test

import io.kotest.core.spec.Spec
import io.kotest.core.spec.description
import kotlin.reflect.KClass

/**
 * The description gives the full path to a [TestCase].
 *
 * It contains the name of every parent, with the root at index 0.
 * And it includes the name of the test case it represents.
 *
 * This is useful when you want to write generic extensions and you
 * need to be able to filter on certain tests only.
 *
 * @param parents each parent test case
 * @param name the name of this test case
 */
@Suppress("MemberVisibilityCanBePrivate")
data class Description(val parents: List<TestName>, val name: TestName) {

   private val PathSeperator = " -- "

   companion object {

      operator fun invoke(parents: List<String>, name: String): Description =
         Description(parents.map { TestName(null, it) }, TestName(null, name))

      /**
       * Creates a Spec level description object for the given name.
       */
      fun spec(name: String) = spec(TestName(null, name))

      fun spec(name: TestName) = Description(emptyList(), name)

      /**
       * Creates a Spec level description from the given [Spec] instance.
       */
      fun spec(spec: Spec): Description = spec::class.description()

      fun spec(kclass: KClass<out Spec>) = kclass.description()

      /**
       * Creates a Spec level description from the given instance, with no guarantees the instance is a Spec.
       */
      fun specUnsafe(spec: Any) = spec::class.description()

      fun test(name: String) = Description(emptyList(), TestName(null, name))
   }

   fun append(name: String) = append(null, name)
   fun append(prefix: String?, name: String) = append(TestName(prefix, name))
   fun append(name: TestName) = Description(this.parents + this.name, name)

   /**
    * Returns the parent of this description, unless it is a spec then it will throw
    */
   fun parent(): Description = if (isSpec()) error("Cannot call .parent() on a spec") else
      Description(parents.dropLast(1), parents.last())

   /**
    * Returns true if this description is for a spec.
    */
   fun isSpec(): Boolean = parents.isEmpty()

   /**
    * Returns a [Description] that models the spec that this description belongs to.
    * Requires at least one parent, otherwise this description is a spec already.
    */
   fun spec(): Description = spec(parents.first())

   /**
    * Returns the programmatic path of the description. This is all the components joined
    * together with the test path seperator, without prefixes on the names.
    */
   fun path(): String = (parents.map { it.name } + name.name).joinToString(PathSeperator)

   fun tail() = if (parents.isEmpty()) throw NoSuchElementException() else
      Description(parents.drop(1), name)

   fun fullName(): String = (parents.map { it.displayName() } + name.displayName()).joinToString(" ")

   /**
    * Returns a String version of this description, which is
    * the parents + this name concatenated with slashes.
    */
   fun id(): String = (parents.map { it.displayName() } + listOf(name.displayName())).joinToString("/")

   fun names(): List<TestName> = parents + name

   fun depth() = names().size

   /**
    * Returns true if this description is the immediate parent of the given dargument.
    * Ignores test prefixes when comparing.
    */
   fun isParentOf(description: Description): Boolean {
      return if (description.isSpec())
         false
      else // I am the parent if my path is equal to this other descriptions parent's path
         path() == description.parent().path()
   }

   /**
    * Returns true if this instance is an ancestor (nth-parent) of the supplied argument.
    * Ignores test prefixes when comparing.
    */
   fun isAncestorOf(description: Description): Boolean {
      return when {
         // this description cannot be a parent of a spec
         description.isSpec() -> false
         isParentOf(description) -> true
         else -> {
            val p = description.parent()
            isAncestorOf(p)
         }
      }
   }

   /**
    * Returns true if this instance is on the path to the given descripton. That is, if this
    * instance is either an ancestor of, of the same as, the given description.
    * Ignores test prefixes when comparing.
    */
   fun isOnPath(description: Description): Boolean = this.path() == description.path() || this.isAncestorOf(description)

   /**
    * Returns true if this description is the same as or a child, grandchild, etc of the given description.
    * Ignores test prefixes when comparing.
    */
   fun isDescendentOf(description: Description): Boolean = description.isOnPath(this)

   /**
    * Returns true if this test is a top level test. In other words, if the
    * test has no parents other than the spec itself.
    * Ignores test prefixes when comparing.
    */
   fun isTopLevel(): Boolean = parents.size == 1 && parent().isSpec()
}
