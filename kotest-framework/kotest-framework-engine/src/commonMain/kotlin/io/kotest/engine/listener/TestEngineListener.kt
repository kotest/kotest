package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import kotlin.reflect.KClass

/**
 * Listener that is notified of test engine execution events.
 *
 * Each callback method is provided a [Node].
 *
 * This is public but should be considered internal.
 */
@KotestInternal
interface TestEngineListener {

   /**
    * Invoked when the execution of a leaf test or subtree is about to start.
    *
    * The [node] may represent a tree of tests (a container), in which case
    * any child events will fire before [executionFinished] is invoked
    * for this node.
    *
    * If this event is invoked, then [executionIgnored] will not be invoked
    * for the same node.
    *
    * @param node the leaf test or subtree
    */
   suspend fun executionStarted(node: Node)

   /**
    * Invoked when the execution of a leaf node or subtree has been skipped.
    *
    * The [Node] may be a leaf test or a container of tests. Once a node is
    * marked as ignored, then no further events will fire for either children
    * or this node.
    *
    * @param node   the leaf test or subtree
    * @param reason a human-readable message detailing why execution
    *               was skipped
    */
   suspend fun executionIgnored(node: Node, reason: String?)

   /**
    * Invoked when the execution of a leaf node or subtree has completed.
    *
    * The [Node] may be a leaf test or a container of tests. Once a node is
    * marked as finished, then all child events have fired, and no further
    * events will fire for this node.
    *
    * @param node   the leaf test or subtree
    * @param result the result of the test or container
    */
   suspend fun executionFinished(node: Node, result: TestResult)
}

abstract class AbstractTestEngineListener : TestEngineListener {
   override suspend fun executionFinished(node: Node, result: TestResult) {}
   override suspend fun executionIgnored(node: Node, reason: String?) {}
   override suspend fun executionStarted(node: Node) {}
}


@KotestInternal
sealed interface Node {
   // the root element in the test tree
   data class Engine(val context: EngineContext) : Node
   data class Spec(val kclass: KClass<*>) : Node
   data class Test(val testCase: TestCase) : Node
}

@KotestInternal
val NoopTestEngineListener = object : TestEngineListener {
   override suspend fun executionStarted(node: Node) {}
   override suspend fun executionFinished(node: Node, result: TestResult) {}
   override suspend fun executionIgnored(node: Node, reason: String?) {}
}
