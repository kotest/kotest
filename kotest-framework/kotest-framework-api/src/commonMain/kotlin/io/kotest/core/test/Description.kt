@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.core.test

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * A description is an ADT that models a pointer to a [Spec] or a [TestCase].
 */
sealed class Description {

   abstract val name: DescriptionName

   data class SpecDescription(val kclass: KClass<out Spec>, override val name: DescriptionName.SpecName) : Description()

   data class TestDescription(
      val parent: Description,
      override val name: DescriptionName.TestName,
      val type: TestType
   ) : Description()

   fun isSpec() = this is SpecDescription
   fun isContainer() = this is TestDescription && type == TestType.Container
   fun isTest() = this is TestDescription && type == TestType.Container
   fun isRootTest() = this is TestDescription && parent.isSpec()

   fun parents(): List<Description> = when (this) {
      is SpecDescription -> emptyList()
      is TestDescription -> parent.parents() + listOf(this)
   }

   fun append(name: DescriptionName.TestName, type: TestType): TestDescription = when (type) {
      TestType.Test -> appendTest(name)
      TestType.Container -> appendContainer(name)
   }

   fun appendContainer(name: String): TestDescription = appendContainer(DescriptionName.TestName(name))
   fun appendContainer(name: DescriptionName.TestName): TestDescription =
      TestDescription(this, name, TestType.Container)

   fun appendTest(name: String): TestDescription = appendTest(DescriptionName.TestName(name))
   fun appendTest(name: DescriptionName.TestName): TestDescription = TestDescription(this, name, TestType.Test)

   /**
    * Returns all descriptions from the spec to this test, with the spec as the first element,
    * and this description as the last.
    */
   fun chain(): List<Description> = when (this) {
      is SpecDescription -> listOf(this)
      is TestDescription -> parent.chain() + listOf(this)
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
    * Returns the [SpecDescription] that is the root for this description.
    * If this description is already a spec, then will return itself.
    */
   fun spec(): SpecDescription = when (this) {
      is SpecDescription -> this
      is TestDescription -> parent.spec()
   }

   /**
    * Returns the name of this description formatted for display.
    */
   fun displayName() = when (this) {
      is SpecDescription -> name.displayName
      is TestDescription -> name.displayName()
   }

   /**
    * Returns a parsable path to the test excluding the spec name.
    * The test path doesn't include prefix/suffix information.
    */
   fun testPath(): TestPath = TestPath(testNames().joinToString(TestPathSeperator) { it.name })

   /**
    * Returns a path to this test excluding the spec, formatted for display.
    * The display path includes prefix/suffix if enabled.
    */
   fun testDisplayPath(): DisplayPath = DisplayPath(testNames().joinToString(" ") { it.displayName() })

   /**
    * Returns a path to this test including the spec, formatted for display.
    * The display path includes prefix/suffix if enabled.
    */
   fun displayPath(): DisplayPath = DisplayPath(names().joinToString(" ") { it.displayName() })

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
      is SpecDescription -> false
      is TestDescription -> id() == description.parent.id()
   }

   /**
    * Returns true if this description is an ancestor (0..nth-parent) of the given argument.
    * Ignores test prefixes when comparing.
    */
   fun isAncestorOf(description: Description): Boolean = when (description) {
      // nothing can be an ancestor of a spec
      is SpecDescription -> false
      is TestDescription -> isParentOf(description) || isAncestorOf(description.parent)
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
      this.testPath() == description.testPath() || this.isAncestorOf(description)
}

data class TestId(val value: String)
data class TestPath(val value: String)
data class DisplayPath(val value: String)

const val TestPathSeperator = " -- "
