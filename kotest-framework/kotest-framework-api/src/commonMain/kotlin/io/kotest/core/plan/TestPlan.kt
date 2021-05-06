package io.kotest.core.plan

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.plan.Descriptor.EngineDescriptor
import io.kotest.core.plan.Descriptor.SpecDescriptor
import io.kotest.core.plan.Descriptor.TestDescriptor
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.spec.isSpecEnabled
import io.kotest.core.test.Description
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestPath
import io.kotest.core.test.TestType
import io.kotest.mpp.qualifiedNameOrNull
import kotlin.reflect.KClass

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
    * Returns information about the names used for this node.
    * See [NodeName] for full details of the available fields.
    */
   abstract val name: NodeName

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
 * Returns a unique path to this node.
 * All test paths from the same test case form an equivalence relation.
 *
 * Includes the engine, spec, and all tests, in that order.
 * This path is safe to be used programmatically.
 * The test path doesn't include prefix/suffix information added by some spec styles.
 */
@ExperimentalKotest
fun Node.testPath(): TestPath = when (this) {
   EngineNode -> TestPath(this.name.name)
   is SpecNode -> EngineDescriptor.testPath().append(this.name.name)
   is TestNode -> parent.testPath().append(this.name.name)
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
   is TestNode -> this.testPath() == node.parent.testPath()
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
fun Node.contains(node: Node): Boolean = this.testPath() == node.testPath() || this.isAncestorOf(node)

/**
 * Returns true if this node is ancestor of the given [node]. An ancestor means that it is
 * a parent, or a grandparent, or a great-grandparent and so on.
 *
 * A node is not an ancestor of itself.
 */
@ExperimentalKotest
fun Node.isAncestorOf(node: Node): Boolean {
   // nodes cannot be ancestors of themselves.
   if (node.testPath() == this.testPath()) return false
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
   override val name: NodeName = EngineName
}

/**
 * A [Node] for a [TestCase]. A test may contain other nested tests, in which case its type is
 * set to [TestType.Container]. If a test does not allow nested tests, then it's type is [TestType.Test].
 *
 * Note: Just because a type has the value container does not mean it contains nested tests. It just means
 * that the DSL permits it to accept nested tests. Because tests are simply functions, there is no way to know
 * if nested tests exist until that function has been executed at runtime.
 *
 * @param parent all tests have a parent - either another [TestNode] or a [SpecNode].
 * @param source a [Source] for the definition of this test.
 * @param tags all runtime tags applicable to this test
 * @param enabled describes whether a test is enabled at runtime, and if not with the reason if provided.
 */
@ExperimentalKotest
data class TestNode(
   val parent: Node,
   override val name: NodeName,
   val type: TestType,
   val source: Source,
   val tags: Set<Tag>,
   val enabled: Enabled,
   val severity: TestCaseSeverityLevel?,
) : Node()

/**
 * A [Node] for a [Spec]. A spec is a container of tests, for instance a class, an object
 * or a Kotlin Script.
 *
 * @param name the [Name] for this spec.
 * @param qualifiedName the fully qualified class name if available, or null.
 * @param type specs can be three types - classes, objects or scripts
 * @param source a [Source] for the definition of this spec.
 * @param tags all tags applied to the definition of this spec - does not include tags nested in the constructor
 * @param enabled describes whether a test is enabled at runtime, and if not with the reason if provided.
 */
@ExperimentalKotest
data class SpecNode(
   override val name: NodeName,
   val qualifiedName: String?,
   val type: SpecType,
   val source: Source,
   val tags: Set<Tag>,
   val enabled: Enabled,
) : Node()

/**
 * Creates a root level [TestNode] by appending to the this [SpecNode].
 */
@ExperimentalKotest
fun SpecNode.append(
   name: NodeName,
   type: TestType,
   source: Source,
   tags: Set<Tag>,
   enabled: Enabled,
   severity: TestCaseSeverityLevel?,
): TestNode =
   TestNode(
      parent = this,
      name = name,
      type = type,
      source = source,
      tags = tags,
      enabled = enabled,
      severity = severity,
   )


/**
 * Adds a nested test to this [TestNode].
 */
@ExperimentalKotest
fun TestNode.append(
   name: NodeName,
   type: TestType,
   source: Source,
   tags: Set<Tag>,
   enabled: Enabled,
   severity: TestCaseSeverityLevel?,
): TestNode =
   TestNode(
      parent = this,
      name = name,
      type = type,
      source = source,
      tags = tags,
      enabled = enabled,
      severity = severity,
   )

enum class SpecType {
   Class, Object, Script
}

/**
 * Returns a [SpecNode] for this kclass.
 *
 * See [SpecName] for rules on how the name is generated.
 */
@ExperimentalKotest
fun KClass<out Spec>.toNode(): SpecNode {
   return SpecNode(
      name = NodeName.fromSpec(this),
      qualifiedName = this.qualifiedNameOrNull(),
      type = SpecType.Class,
      source = Source.Line(sourceRef().fileName, sourceRef().lineNumber),
      tags = emptySet(),
      enabled = isSpecEnabled(this),
   )
}
