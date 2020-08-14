package io.kotest.core.test

import kotlin.reflect.KClass

/**
 * A description is a pointer to a [Spec] or a [TestCase].
 *
 * It contains the reference to it's own parent, and the name
 * of the test case it represents.
 *
 * It contains a [DescriptionType]: Spec, Container or Test, which can be used to write generic
 * extensions where you need to be able to filter on certain tests only.
 *
 * @param type specifies if this description points to a spec, container or test.
 * @param parent the parent of this description, unless this is a spec in which case the parent is null.
 * @param name the name of this test case.
 * @param specClass The class of the spec that this description describes, or the classname of the spec
 * that the test case is contained within.
 */
data class Description(
   val parent: Description?,
   val name: TestName,
   val type: DescriptionType,
   val specClass: KClass<*>
) {

   /**
    * Returns true if this description points to a [TestCase] that is a root test.
    * A root test case is a description whose immediate parent is a spec.
    */
   fun isRootTestCase(): Boolean = parent?.isSpec() ?: false

   /**
    * Returns true if this description points to a [Spec].
    */
   fun isSpec(): Boolean = type == DescriptionType.Spec

   /**
    * Returns true if this description points to a leaf test case.
    */
   fun isTest(): Boolean = type == DescriptionType.Test

   /**
    * Returns true if this description points to a container test case.
    */
   fun Description.isContainer(): Boolean = type == DescriptionType.Container

   @Deprecated("use isRootTestCase(). Will be removed in 4.4")
   fun isTopLevel() = isRootTestCase()

   /**
    * Return an a-zA-Z0-9_. name for this test that can be safely used as an id.
    */
   fun id(): TestId {
      val id = names(false).joinToString("/") { it.name.replace(" ", "_").replace("[^a-zA-Z0-9_.]".toRegex(), "") }
      return TestId(id)
   }

   /**
    * Returns each level of this description as a [TestName], including the spec.
    *
    * Eg,
    *
    * context("a context") {
    *   context("and another") {
    *     test("a test") { }
    *   }
    * }
    *
    * Would return a list of four components - the name of the spec, 'a context', 'and another', 'a test'.
    *
    * @param includeSpec if true then the spec name is also included.
    */
   fun names(includeSpec: Boolean = true): List<TestName> = chain().map { it.name }

   /**
    * Returns the all parent [Description]s for this this description, with the spec as the first element,
    * and the immediate parent of this descripiton as the last.
    */
   fun parents(): List<Description> = if (parent == null) emptyList() else parent.parents() + parent

   /**
    * Returns the [Description] that represents the spec that this description belongs to.
    * This will recurse to the head of the description chain.
    * If this description is already a spec, will return itself.
    */
   fun spec(): Description = parent?.spec() ?: this

   /**
    * Returns all descriptions from the spec to this test, with the spec as the first element,
    * and this description as the last.
    */
   fun chain(): List<Description> = if (parent == null) listOf(this) else parent.chain() + this

   /**
    * Returns a programatic test path for this description. The test path is a string that can be
    * used to unambigiously refer to this test. This path is not suitable for display purposes as it
    * takes no account of casing, prefixes, and uses a delimiter between test names.
    */
   fun path(includeSpec: Boolean = true): TestPath =
      TestPath(names(includeSpec).joinToString(TestPathSeperator) { it.name })

   /**
    * Returns a path formatted for display.
    */
   fun displayPath(
      includeSpec: Boolean = true,
      testNameCase: TestNameCase = TestNameCase.AsIs,
      includeTestScopePrefixes: Boolean = false
   ): String = names(includeSpec).joinToString(" ") { it.format(testNameCase, includeTestScopePrefixes) }

   /**
    * Returns true if this description is the immediate parent of the given argument.
    * Ignores test prefixes when comparing.
    */
   fun isParentOf(description: Description): Boolean {
      // if the given arg is a spec, then nothing can be it's parent
      if (description.isSpec()) return false
      // I am the parent if my path is equal to this other descriptions parent's path
      return path() == description.parent?.path()
   }

   /**
    * Returns true if this instance is an ancestor (0..nth-parent) of the given argument.
    * Ignores test prefixes when comparing.
    */
   fun isAncestorOf(description: Description): Boolean {
      return when (val p = description.parent) {
         // nothing can be an ancestor of a description without a parent
         null -> false
         // I am an ancestor of the arg if I am it's parent, or it's parent's parent recursively
         else -> isParentOf(description) || isAncestorOf(p)
      }
   }

   /**
    * Returns true if this description is the same as or a child, grandchild, etc of the given description.
    * Ignores test prefixes when comparing.
    */
   fun isDescendentOf(description: Description): Boolean = description.isOnPath(this)

   /**
    * Returns true if this instance is on the path to the given descripton. That is, if this
    * instance is either an ancestor of, of the same as, the given description.
    * Ignores test prefixes when comparing.
    */
   fun isOnPath(description: Description): Boolean =
      this.path() == description.path() || this.isAncestorOf(description)

   /**
    * Returns a new [Description] with the name and type as specified, and this description as the parent.
    */
   fun append(name: TestName, type: DescriptionType) = Description(this, name, type, specClass)

   /**
    * Returns a new [Description] with the given name, type derived from the given [TestType],
    * and this description as the parent.
    */
   fun append(name: TestName, type: TestType): Description {
      return Description(
         this, name, when (type) {
            TestType.Container -> DescriptionType.Container
            TestType.Test -> DescriptionType.Test
         }, specClass
      )
   }

   /**
    * Returns a new [Description] with the given name, type set as [DescriptionType.Test],
    * and this description as the parent.
    */
   fun appendTest(name: String) = append(TestName(name), DescriptionType.Test)
   fun appendTest(name: TestName) = append(name, DescriptionType.Test)

   /**
    * Returns a new [Description] with the given name, type set as [DescriptionType.Container],
    * and this description as the parent.
    */
   fun appendContainer(name: String) = append(TestName(name), DescriptionType.Container)
   fun appendContainer(name: TestName) = append(name, DescriptionType.Container)
}

data class TestId(val value: String)
data class TestPath(val value: String)

const val TestPathSeperator = " -- "

enum class DescriptionType {
   Spec, Container, Test
}
