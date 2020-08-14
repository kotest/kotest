@file:Suppress("PrivatePropertyName")

package io.kotest.core.test

import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.Spec
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.js.JsName
import kotlin.reflect.KClass

/**
 * The description is a pointer to a [Spec] or a [TestCase].
 *
 * It contains the reference to it's own parent, and the name
 * of the test case it represents.
 *
 * It contains a type: Spec, Container or Test.
 *
 * This is useful when you want to write generic extensions and you
 * need to be able to filter on certain tests only.
 *
 * @param parent the parent of this descripton, unless this is a spec in which case the parent is null.
 * @param name the name of this test case
 */
@Suppress("MemberVisibilityCanBePrivate")
data class Description(
   val parent: Description?,
   val specClass: KClass<out Spec>,
   val name: TestName,
   val type: DescriptionType
) {

   private val TestPathSeperator = " -- "

   companion object {

      /**
       * Creates a Spec level description from the given [Spec] instance.
       */
      fun spec(spec: Spec): Description = spec(spec::class)

      /**
       * Returns a [Description] that can be used for a spec.
       *
       * If the spec has been annotated with @DisplayName (on supported platforms), then that will be used,
       * otherwise the default is to use the fully qualified class name.
       *
       * Note: This name must be globally unique. Two specs, even in different packages,
       * cannot share the same name, so if @DisplayName is used, developers must ensure it does not
       * clash with another spec.
       */
      fun spec(kclass: KClass<out Spec>): Description {
         val name = kclass.annotation<DisplayName>()?.name ?: kclass.bestName()
         return Description(null, kclass, TestName(name), DescriptionType.Spec)
      }

      /**
       * Creates a Spec level description from the given instance, with no guarantees the instance is a Spec.
       */
      fun specUnsafe(spec: Any) = spec(spec::class as KClass<out Spec>)
   }

   /**
    * Returns a new [Description] with the name and type as specified, and this description as the parent
    */
   fun append(name: TestName, type: DescriptionType) = Description(this, specClass, name, type)

   /**
    * Returns a new [Description] with the name and type as specified, and this description as the parent
    */
   fun append(name: TestName, type: TestType) = Description(this, specClass, name, type.descriptionType())

   @Deprecated("this doesn't specify the type but is needed for backwards compatibility")
   fun append(name: String) = Description(this, specClass, TestName(name), DescriptionType.Test)

   fun appendTest(name: String) = append(TestName(name), DescriptionType.Test)

   fun appendContainer(name: String) = append(TestName(name), DescriptionType.Container)

   /**
    * Returns the parent of this description, unless it is a spec then it will throw
    */
   @JsName("getParent")
   fun parent(): Description = parent ?: error("Cannot call .parent() on a spec")

   /**
    * Returns true if this description is for a spec.
    */
   fun isSpec(): Boolean = type == DescriptionType.Spec

   /**
    * Returns the [Description] that represents the spec that _this_ description belongs to.
    * This will recurse to the top of the description tree to find the spec.
    * If this description is already a spec, will return itself.
    */
   fun spec(): Description = parent?.spec() ?: this

   /**
    * Returns all descriptions in this tree, starting with the root element and proceeding to this description
    * as the final element in the list.
    */
   fun chain(): List<Description> = if (parent == null) listOf(this) else parent.chain() + this

   /**
    * Returns the programmatic path of the description. This is all the components joined
    * together with the [TestPathSeperator], without prefixes on the names.
    */
   fun path(): String = if (parent == null) name.name else parent.path() + TestPathSeperator + name.name

   /**
    * Returns the full name including the spec.
    */
   fun fullName(): String = if (parent == null) name.displayName() else parent.fullName() + " " + name.displayName()

   fun fullNameWithoutSpec(): String =
      if (parent == null) "" else (parent.fullNameWithoutSpec() + " " + name.displayName()).trim()

   /**
    * Returns a version of this description suitable for ids, which is
    * the parents + this name concatenated with slashes.
    */
   fun id(): String = if (parent == null) name.displayName() else parent.id() + "/" + name.displayName()

   /**
    * Returns each component of this description as a [TestName].
    * Eg,
    *
    * context("a context") {
    *   context("and another") {
    *     test("a test") { }
    *   }
    * }
    *
    * Would return a list of four components - the name of the spec, 'a context', 'and another', 'a test'.
    */
   fun names(): List<TestName> = if (parent == null) listOf(name) else parent.names() + name

   fun depth() = names().size

   /**
    * Returns true if this description is the immediate parent of the given argument.
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
    * Returns true if this test is a top level / root test. In other words, if the
    * test has no parents other than the spec itself.
    * Ignores test prefixes when comparing.
    */
   fun isTopLevel(): Boolean = parent?.type == DescriptionType.Spec
}

fun KClass<out Spec>.toDescription() = Description.spec(this)

enum class DescriptionType {
   Spec, Container, Test
}

fun TestType.descriptionType(): DescriptionType = when (this) {
   TestType.Container -> DescriptionType.Container
   TestType.Test -> DescriptionType.Test
}
