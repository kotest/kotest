@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.kotest.core.plan

import io.kotest.core.SourceRef
import io.kotest.core.Tag
import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.plan.TestPlanNode.EngineNode
import io.kotest.core.plan.TestPlanNode.SpecNode
import io.kotest.core.plan.TestPlanNode.TestCaseNode
import io.kotest.core.sourceRef
import io.kotest.core.spec.Spec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestId
import io.kotest.core.test.TestType
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * A [TestPlanNode] is a lightweight descriptor for a node in the [TestPlan] tree.
 *
 * The test plan tree consists of a single root [EngineNode], with children of either [SpecNode]s,
 * or [TestCaseNode]s.
 *
 * Only the [EngineNode] can contain [SpecNode]s and leaf nodes must always be [TestCaseNode]s.
 *
 * This class is intended as a long term replacement for [Description].
 *
 * For example:
 *
 * - kotest
 *   - spec
 *     - container
 *       - test
 *       - test
 *   - spec
 *     - test
 *     - container
 *       - container
 *         - test
 */
@ExperimentalKotest
sealed class TestPlanNode {

   abstract val id: TestId
   abstract val name: NodeName

   fun isSpec() = this is SpecNode
   fun isEngine() = this is EngineNode
   fun isTestCase() = this is TestCaseNode
   fun isRootTest() = this is TestCaseNode && this.parent.isSpec()

   /**
    * Returns the depth of this node, where the [EngineNode] is depth 0, a [SpecNode] is depth 1, and so on.
    */
   fun depth() = parents().size

   /**
    * Returns all parent nodes.
    */
   fun parents(): List<TestPlanNode> = when (this) {
      EngineNode -> emptyList()
      is SpecNode -> listOf(EngineNode)
      is TestCaseNode -> parent.parents() + parent
   }

   /**
    * Returns the [SpecNode] parent for this node.
    *
    * If this node is itself a spec, then this function will return itself.
    *
    * If this node is the [EngineNode], then this function returns null.
    */
   fun spec(): TestPlanNode? = when (this) {
      EngineNode -> null
      is SpecNode -> this
      is TestCaseNode -> parent.spec()
   }

   /**
    * Returns true if this node is the immediate parent of the given [node].
    */
   fun isParentOf(node: TestPlanNode): Boolean = when (node) {
      // nothing can be the parent of the engine
      EngineNode -> false
      // only the engine can be a parent of a spec
      is SpecNode -> this is EngineNode
      is TestCaseNode -> this.id == node.parent.id
   }

   /**
    * Returns true if this node is ancestor (1..nth-parent) of the given [node].
    */
   fun isAncestorOf(node: TestPlanNode): Boolean = when (node) {
      // nothing can be the ancestor of the engine
      EngineNode -> false
      // only the engine can be a ancestor of a spec
      is SpecNode -> this is EngineNode
      is TestCaseNode -> isParentOf(node) || isAncestorOf(node.parent)
   }

   /**
    * Returns true if this node is the immediate child given [node].
    */
   fun isChildOf(node: TestPlanNode): Boolean = when (this) {
      EngineNode -> false
      is SpecNode -> node is EngineNode
      is TestCaseNode -> parent.id == node.id
   }

   /**
    * Returns true if this node is a child, grandchild, etc of the given [node].
    */
   fun isDescendentOf(node: TestPlanNode): Boolean = when (this) {
      // the engine cannot be a descendant of any other node
      EngineNode -> false
      // a spec can only be a descendant of the engine
      is SpecNode -> this is EngineNode
      is TestCaseNode -> isChildOf(node) || parent.isDescendentOf(node)
   }

   /**
    * Returns true if this node is part of the path to the given [node]. That is, if this
    * instance is either an ancestor of, of the same as, the given node.
    */
   fun contains(node: TestPlanNode): Boolean = this.id == node.id || this.isAncestorOf(node)

   object EngineNode : TestPlanNode() {
      override val id: TestId = TestId("kotest")
      override val name: NodeName = NodeName.EngineName
   }

   data class SpecNode(
      override val name: NodeName.SpecName,
      val specClass: KClass<out Spec>,
      // the runtime tags applied to this spec
      val tags: Set<Tag>,
      // true if this spec is active
      val active: Boolean,
   ) : TestPlanNode() {
      override val id: TestId = TestId(Random.nextLong().toString())
   }

   data class TestCaseNode(
      val parent: TestPlanNode,
      override val name: NodeName.TestName,
      val type: TestType,
      // the runtime tags applied to this test
      val tags: Set<Tag>,
      // link to the source ref where this test is defined
      val source: SourceRef,
      // true if this test case is active
      val active: Boolean,
   ) : TestPlanNode() {
      override val id: TestId = TestId(Random.nextLong().toString())
   }
}

fun Description.toNode(spec: Spec): TestPlanNode {
   return when (this) {
      is Description.Spec -> SpecNode(NodeName.fromSpecClass(spec::class), spec::class, spec.tags(), false)

      is Description.Test -> {
         val parent = this.parent.toNode(spec)
         val name = when (parent) {
            EngineNode -> TODO()
            is SpecNode -> parent.name.append(this.displayName())
            is TestCaseNode -> parent.name.append(this.displayName())
         }
         TestCaseNode(
            parent,
            name,
            type,
            emptySet(),
            sourceRef(),
            false
         )
      }
   }
}

fun TestCase.toNode(active: Boolean): TestCaseNode =
   (description.toNode(spec) as TestCaseNode).copy(active = active)
