package io.kotest.core.plan

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.plan.Descriptor.EngineDescriptor
import io.kotest.core.plan.Descriptor.SpecDescriptor
import io.kotest.core.plan.Descriptor.TestDescriptor
import io.kotest.core.test.Description
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestId
import io.kotest.core.test.TestPath
import io.kotest.core.test.TestType

/**
 *  A [TestPlan] is a tree that contains the tests for a project, generated at runtime.
 *
 *  It consists of a root 'engine' node, which may contain one or more 'spec' nodes.
 *  Each spec node may contain one or more test nodes, and each test node in turn can contain
 *  further test nodes. Test nodes can be of type 'container' (if they permit nested tests) or 'test'
 *  if they are leaf tests.
 *
 *  An example topology looks like:
 *
 *  + engine
 *    + spec
 *      + container
 *        - test
 *      - test
 *    + spec
 *      - test
 */
@ExperimentalKotest
data class TestPlan(val root: EngineNode)

/**
 * A [Node] is an ADT that represents specs and tests in the [TestPlan] tree.
 * They contain the runtime information pertaining to each node - for example, whether
 * a node is enabled or disabled, which tags were present, and so on.
 *
 * There are four types of nodes - a single [EngineDescriptor] at the root,
 * with [SpecDescriptor]s  as direct children of the engine.
 *
 * Then [TestDescriptor]s are children of the spec descriptors and comprise leaf nodes.
 *
 * This class is intended as a long term replacement for [Description].
 */
@ExperimentalKotest
sealed class Node {

   /**
    * Returns an ephemeral id that can be used to uniquely refer to this test during a test run.
    *
    */
   abstract val id: TestId

   /**
    * Returns a parsable name for this node that is consistent across test runs.
    *
    * This should be used to refer to tests outside of the run, for example, as an identifer
    * in a test reporting system where you want to track the results of a test over time.
    *
    * The intellij plugin also uses this name to refer to tests when invoking individual
    * tests from the IDE.
    *
    * Any particular name is not guaranteed to be unique, but the combination of all names
    * in the form of the [testPath] is guaranteed to be unique.
    */
   abstract val name: Name

   /**
    * Returns a human readable name used for reports and displays.
    */
   abstract val displayName: DisplayName

   /**
    * Returns true if this node is for a spec.
    */
   fun isSpec() = this is SpecNode

   /**
    * Returns true if this node is the root engine node.
    */
   fun isEngine() = this is EngineNode

   /**
    * Returns true if this descriptor is for a test case.
    */
   fun isTestCase() = this is TestNode

   /**
    * Returns true if this node represents a root test case.
    */
   fun isRootTest() = this is TestNode && this.parent.isSpec()
}

/**
 * Returns a parseable path to this node.
 * Includes the engine, spec, and all tests, in that order.
 * This path is safe to be used programmatically.
 * The test path doesn't include prefix/suffix information added by some spec styles.
 */
@ExperimentalKotest
fun Node.testPath(): TestPath = when (this) {
   EngineNode -> TestPath(this.name.value)
   is SpecNode -> EngineDescriptor.testPath().append(this.name.value)
   is TestNode -> parent.testPath().append(this.name.value)
}

/**
 * Recursively returns all parent nodes, with the engine first, spec second, and so on.
 */
@ExperimentalKotest
fun Node.parents(): List<Node> = when (this) {
   EngineNode -> emptyList()
   is SpecNode -> listOf(EngineNode)
   is TestNode -> parent.parents() + parent
}

/**
 * Returns true if this node is the immediate parent of the given [node].
 */
@ExperimentalKotest
fun Node.isParentOf(node: Node): Boolean = when (node) {
   // nothing can be the parent of the engine node
   EngineNode -> false
   // the only parent of a spec node can be the engine itself
   is SpecNode -> this is EngineNode
   // if the parameter is a test node, then its parent should be me
   is TestNode -> this.id == node.parent.id
}

/**
 * Returns true if this node is the immediate child of the given [node].
 */
@ExperimentalKotest
fun Node.isChildOf(node: Node): Boolean = node.isParentOf(this)

/**
 * Returns true if this node is a child, grandchild, etc of the given [node].
 */
@ExperimentalKotest
fun Node.isDescendentOf(node: Node): Boolean = this.isAncestorOf(node)

/**
 * Returns true if this node can contain other nodes.
 */
@ExperimentalKotest
fun Node.isContainer() = when (this) {
   EngineNode -> true
   is SpecNode -> true
   is TestNode -> this.type == TestType.Container
}

/**
 * Returns true if this node is part of the path to the given [node]. That is, if this
 * instance is either an ancestor of, of the same as, the given node.
 */
@ExperimentalKotest
fun Node.contains(node: Node): Boolean = this.id == node.id || this.isAncestorOf(node)

/**
 * Returns true if this node is ancestor of the given [node]. An ancestor means that it is
 * a parent, or a grandparent, or a great-grandparent and so on.
 *
 * A node is not an ancestor of itself.
 */
@ExperimentalKotest
fun Node.isAncestorOf(node: Node): Boolean {
   // nodes cannot be ancestors of themselves.
   if (node.id == this.id) return false
   return when (node) {
      // nothing can be an ancestor of the engine node
      EngineNode -> false
      // only the engine can be a ancestor of a spec
      is SpecNode -> this is EngineNode
      is TestNode -> this.isParentOf(node) || this.isAncestorOf(node.parent)
   }
}

/**
 * Every test plan has a single [EngineNode] as the root.
 */
@ExperimentalKotest
object EngineNode : Node() {
   override val id: TestId = TestId("kotest")
   override val name: Name = Name("kotest")
   override val displayName: DisplayName = DisplayName("kotest")
}

/**
 * A [Node] for a [Spec]. A spec is a container of tests, for instance a class, an object
 * or a Kotlin Script.
 *
 * @param name the fully qualified class name if available, otherwise the simple class name.
 * @param displayName the simple class name unless overriden by a @DisplayName annotation.
 * @param classname the fully qualified class name if available, or null.
 * @param type specs can be three types - classes, objects or scripts
 * @param source a [Source] for the definition of this spec.
 * @param tags all runtime tags applicable to this test
 * @param enabled describes whether a test is enabled at runtime, and if not with the reason if provided.
 */
@ExperimentalKotest
data class SpecNode(
   override val id: TestId,
   override val name: Name,
   override val displayName: DisplayName,
   val classname: String?,
   val type: SpecType,
   val source: Source,
   val tags: Set<Tag>,
   val enabled: Enabled,
) : Node()

/**
 * Adds a root level test to this [SpecNode].
 */
@ExperimentalKotest
fun SpecNode.append(
   name: Name,
   displayName: DisplayName,
   type: TestType,
   source: Source,
   tags: Set<Tag>,
   enabled: Enabled
): TestNode =
   TestNode(
      id = TestId.generate(),
      parent = this,
      name = name,
      displayName = displayName,
      type = type,
      source = source,
      tags = tags, enabled = enabled
   )

/**
 * A [Node] for a [TestCase]. A test may contain other nested tests, in which case its type is
 * set to [TestType.Container]. If a test does not allow nested tests, then it's type is [TestType.Test].
 *
 * Note: Just because a test is set to container does not mean it contains nested tests. It just means
 * that the DSL permits it to accept nested tests.
 *
 * @param parent all tests have a parent - either another [TestNode] or a [SpecNode].
 * @param source a [Source] for the definition of this test.
 * @param tags all runtime tags applicable to this test
 * @param enabled describes whether a test is enabled at runtime, and if not with the reason if provided.
 */
@ExperimentalKotest
data class TestNode(
   override val id: TestId,
   val parent: Node,
   override val name: Name,
   override val displayName: DisplayName,
   val type: TestType,
   val source: Source,
   val tags: Set<Tag>,
   val enabled: Enabled,
) : Node()

/**
 * Adds a nested test to this [TestNode].
 */
@ExperimentalKotest
fun TestNode.append(
   name: Name,
   displayName: DisplayName,
   type: TestType,
   source: Source,
   tags: Set<Tag>,
   enabled: Enabled
): TestNode =
   TestNode(
      id = TestId.generate(),
      parent = this,
      name = name,
      displayName = displayName,
      type = type,
      source = source,
      tags = tags, enabled = enabled
   )

data class Name(val value: String)
data class DisplayName(val value: String)

enum class SpecType {
   Class, Object, Script
}
