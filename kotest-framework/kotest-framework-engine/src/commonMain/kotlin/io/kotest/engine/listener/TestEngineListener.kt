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
   suspend fun executionStarted(node: Node)

   /**
    * Must be called when the execution of a node of the test tree
    * has been skipped.
    *
    * The [Node] may be a test case or a spec. Once a node is marked
    * as ignored, then no events should be fired for sub nodes.
    *
    * <p>A skipped test or subtree of tests must not be reported as
    * {@linkplain #executionStarted started} or
    * {@linkplain #executionFinished finished}.
    *
    * @param testDescriptor the descriptor of the skipped test or container
    * @param reason a human-readable message describing why the execution
    * has been skipped
    */
   suspend fun executionIgnored(node: Node, reason: String?)
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
