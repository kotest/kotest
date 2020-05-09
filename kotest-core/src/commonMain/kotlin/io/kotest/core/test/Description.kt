package io.kotest.core.test

import io.kotest.core.spec.Spec
import io.kotest.core.spec.description

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
data class Description(val parents: List<String>, val name: String) {

   companion object {
      /**
       * Creates a Spec level description object for the given name.
       */
      fun spec(name: String) = Description(emptyList(), name)

      /**
       * Creates a Spec level description from the given [Spec] instance.
       */
      fun spec(spec: Spec) = spec::class.description()

      /**
       * Creates a Spec level description from the given instance, with no guarantees the instance is a Spec.
       */
      fun specUnsafe(spec: Any) = spec::class.description()

      fun test(name: String) = Description(emptyList(), name)
   }

   fun append(name: String) =
      Description(this.parents + this.name, name)

   fun hasParent(description: Description): Boolean =
      parents.containsAll(description.parents + listOf(description.name))

   /**
    * Returns the parent of this description, unless it is a spec then it will throw
    */
   fun parent(): Description = if (isSpec()) error("Cannot call .parent() on a spec") else Description(
      parents.dropLast(1),
      parents.last()
   )

   fun isSpec(): Boolean = parents.isEmpty()

   fun spec(): Description =
      spec(parents.first())

   fun tail() = if (parents.isEmpty()) throw NoSuchElementException() else Description(
      parents.drop(1),
      name
   )

   fun fullName(): String = (parents + listOf(name)).joinToString(" ")

   /**
    * Returns a String version of this description, which is
    * the parents + this name concatenated with slashes.
    */
   fun id(): String = (parents + listOf(name)).joinToString("/")

   fun names(): List<String> = parents + name

   fun depth() = names().size

   /**
    * Returns true if this instance is the immediate parent of the supplied argument.
    */
   fun isParentOf(description: Description): Boolean =
      parents + name == description.parents

   /**
    * Returns true if this instance is an ancestor (nth-parent) of the supplied argument.
    */
   fun isAncestorOf(description: Description): Boolean {
      if (isParentOf(description))
         return true
      return if (description.isSpec()) false else {
         val p = description.parent()
         isAncestorOf(p)
      }
   }

   /**
    * Returns true if this instance is on the path to the given descripton. That is, if this
    * instance is either an ancestor of, of the same as, the given description.
    */
   fun isOnPath(description: Description): Boolean = this == description || this.isAncestorOf(description)

   /**
    * Returns true if this description is the same as or a child, grandchild, etc of the given description.
    */
   fun isDescendentOf(description: Description): Boolean = description.isOnPath(this)

   /**
    * Returns true if this test is a top level test. In other words, if the
    * test has no parents other than the spec itself.
    */
   fun isTopLevel(): Boolean = parents.size == 1 && parent().isSpec()
}
