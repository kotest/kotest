package io.kotest.engine.js

import io.kotest.common.reflection.bestName
import io.kotest.core.Logger
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.descriptor
import io.kotest.core.spec.name
import io.kotest.core.test.TestCase
import io.kotest.engine.listener.TestEngineInitializedContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.promise
import kotlin.reflect.KClass

/**
 * An implementation of [TestEngineListener] that will wait for a spec to be completed, before then emitting
 * all events through a [KotlinJsTestFramework].
 */
@OptIn(DelicateCoroutinesApi::class)
internal class JsTestFrameworkTestEngineListener(
   private val framework: KotlinJsTestFramework,
) : TestEngineListener {

   private val logger = Logger(JsTestFrameworkTestEngineListener::class)
   private val nodes = mutableMapOf<DescriptorId, NodeProxy>()

   // hold up mocha from exiting before our tests are registered
   private val channel = Channel<Unit>(100)

   override suspend fun engineStarted() {
      // we have to launch the engine inside a test and return a promise, so mocha will wait for the engine to finish
      // otherwise our engine is running in a coroutine and mocha will have exited before we start emitting tests
      // The downside is that we get an extra node in the output (todo, perhaps the IDE plugin can hide this?)
      kotlinJsTestFramework.suite("Kotest", false) {
         kotlinJsTestFramework.test("Executor", false) {
            GlobalScope.promise {
               channel.receive() // will suspend this placeholder test until the first real test releases us
            }
         }
      }
   }

   override suspend fun engineInitialized(context: TestEngineInitializedContext) {}

   override suspend fun engineFinished(t: List<Throwable>) {
      // close out the last test and exit
      channel.send(Unit)
   }

   override suspend fun specStarted(ref: SpecRef) {
      createNode(ref.descriptor().id, ref.name())
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      // for ignored specs we can just output immediately as nothing else will be happpening
      outputNode(NodeProxy(kclass.bestName(), TestResult.Ignored(reason), mutableListOf()))
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      val node = getNode(ref.descriptor().id)
      node.result = result
      outputNode(node)
   }

   override suspend fun testStarted(testCase: TestCase) {
      val node = createNode(testCase.descriptor.id, testCase.name.name)
      val parent = getNode(testCase.descriptor.parent.id)
      parent.children.add(node)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      val node = createNode(testCase.descriptor.id, testCase.name.name)
      node.result = TestResult.Ignored(reason)
      val parent = getNode(testCase.descriptor.parent.id)
      parent.children.add(node)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      val node = getNode(testCase.descriptor.id)
      node.result = result
   }

   private fun getNode(id: DescriptorId): NodeProxy = nodes[id] ?: error("Node for $id not found")

   private fun createNode(id: DescriptorId, name: String): NodeProxy {
      val node = NodeProxy(name, TestResultBuilder.builder().build(), mutableListOf())
      nodes[id] = node
      return node
   }

   private fun outputNode(node: NodeProxy) {
      if (node.children.isEmpty()) { // no children mean a leaf test
         logger.log { Pair(node.name, "Outputting test node ${node.result}") }
         framework.test(testNameEscape(node.name), node.result.isIgnored) {
            GlobalScope.promise {
               // this releases any previous test
               channel.send(Unit)
               // will suspend until the next test releases us
               channel.receive()
               node.result.errorOrNull?.let { throw it }
            }
         }
      } else {
         logger.log { Pair(node.name, "Outputting suite node ${node.result}") }
         framework.suite(testNameEscape(node.name), node.result.isIgnored) {
            node.children.forEach { outputNode(it) }
            // errors cannot be thrown in a suite, so we'll add a placeholder test and throw inside that
            node.result.errorOrNull?.let { t ->
               framework.test(t::class.bestName(), false) {
                  throw t
               }
            }
         }
      }
   }
}

private data class NodeProxy(
   val name: String,
   var result: TestResult,
   val children: MutableList<NodeProxy>,
)
