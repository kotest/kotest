@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.core.test

import kotlin.reflect.KClass

/**
 * A description is an ADT that models a pointer to a [Spec] or a [TestCase].
 */
sealed class Description {

   abstract val name: DescriptionName

   data class Spec(val kclass: KClass<out io.kotest.core.spec.Spec>, override val name: DescriptionName.SpecName) :
      Description()

   data class Test(
      val parent: Description,
      override val name: DescriptionName.TestName,
      val type: TestType,
   ) : Description()

   fun isSpec() = this is Spec
   fun isContainer() = this is Test && type == TestType.Container
   fun isTest() = this is Test && type == TestType.Container
   fun isRootTest() = this is Test && parent.isSpec()

   /**
    * Returns the depth of this description, where a [Spec] is 0, and a root test is 1, and so on.
    */
   fun depth() = parents().size

   /**
    * Returns all parents of this description, excluding the spec itself.
    */
   fun parents(): List<Description> = when (this) {
      is Spec -> emptyList()
      is Test -> parent.parents() + listOf(this)
   }

   fun append(name: DescriptionName.TestName, type: TestType): Test = when (type) {
      TestType.Test -> appendTest(name)
      TestType.Container -> appendContainer(name)
   }

   fun appendContainer(name: String): Test = appendContainer(createTestName(null, name, false))
   fun appendContainer(name: DescriptionName.TestName): Test =
      Test(this, name, TestType.Container)

   fun appendTest(name: String): Test = appendTest(createTestName(null, name, false))
   fun appendTest(name: DescriptionName.TestName): Test = Test(this, name, TestType.Test)

   /**
    * Returns all descriptions from the spec to this test, with the spec as the first element,
    * and this description as the last.
    */
   fun chain(): List<Description> = when (this) {
      is Spec -> listOf(this)
      is Test -> parent.chain() + listOf(this)
   }

   /**
    * Returns each level of this description as a [DescriptionName], including the spec.
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
   fun names(): List<DescriptionName> = chain().map { it.name }

   /**
    * Returns a list of names for this description without the spec, ie from a root test to this description.
    * In other words, all the names excluding the spec.
    */
   fun testNames(): List<DescriptionName.TestName> = names().filterIsInstance<DescriptionName.TestName>()

   /**
    * Returns the [Spec] that is the root for this description.
    * If this description is already a spec, then will return itself.
    */
   fun spec(): Spec = when (this) {
      is Spec -> this
      is Test -> parent.spec()
   }

   /**
    * Returns the name of this description formatted for display.
    */
   fun displayName() = name.displayName

   /**
    * Returns a parsable path to the test including the spec name.
    * The test path doesn't include prefix/suffix information.
    */
   fun path(): TestPath = TestPath(names().joinToString(TestPathSeparator) { it.name })

   /**
    * Returns a parsable path to the test excluding the spec name.
    * The test path doesn't include prefix/suffix information.
    */
   fun testPath(): TestPath = TestPath(testNames().joinToString(TestPathSeparator) { it.name })

   /**
    * Returns a path to this test excluding the spec, formatted for display.
    * The display path includes prefix/suffix if enabled.
    */
   fun testDisplayPath(): DisplayPath = DisplayPath(testNames().joinToString(" ") { it.displayName })

   /**
    * Returns a path to this test including the spec, formatted for display.
    * The display path includes prefix/suffix if enabled.
    */
   fun displayPath(): DisplayPath = DisplayPath(names().joinToString(" ") { it.displayName })

   /**
    * Returns a parseable consistent identifier for this description including the spec name.
    */
   fun id(): TestId {
      val id = chain().joinToString("/") { it.displayName().replace(" ", "_").replace("[^a-zA-Z0-9_.]".toRegex(), "") }
      return TestId(id)
   }

   /**
    * Returns true if this description is the immediate parent of the given argument.
    * Ignores test prefixes when comparing.
    */
   fun isParentOf(description: Description): Boolean = when (description) {
      // nothing can be the parent of a spec
      is Spec -> false
      is Test -> id() == description.parent.id()
   }

   /**
    * Returns true if this description is an ancestor (0..nth-parent) of the given argument.
    * Ignores test prefixes when comparing.
    */
   fun isAncestorOf(description: Description): Boolean = when (description) {
      // nothing can be an ancestor of a spec
      is Spec -> false
      is Test -> isParentOf(description) || isAncestorOf(description.parent)
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
   fun isOnPath(description: Description): Boolean = this.path() == description.path() || this.isAncestorOf(description)
}

data class TestId(val value: String)
data class TestPath(val value: String)
data class DisplayPath(val value: String)

const val TestPathSeparator = " -- "
