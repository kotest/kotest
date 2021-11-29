@file:Suppress("LocalVariableName")

package io.kotest.engine.listener

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener] methods to ensure that only test notifications
 * are passed to the delegated listener for one spec at a time. Notifications that
 * are not for the current spec are delayed until the current spec completes.
 *
 * Note: This class is not thread safe. It is up to the caller to ensure that calls
 * to the methods of this listener are strictly sequential, for example by using
 * an instance of [ThreadSafeTestEngineListener].
 */
class PinnedSpecTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

   private var runningSpec: String? = null
   private val callbacks = mutableListOf<suspend () -> Unit>()

   private suspend fun queue(fn: suspend () -> Unit) {
      callbacks.add { fn() }
   }

   private suspend fun replay() {
      val _callbacks = callbacks.toList()
      callbacks.clear()
      _callbacks.forEach { it.invoke() }
   }

   override suspend fun executionStarted(node: Node) {
      when (node) {
         is Node.Engine -> listener.executionStarted(node)
         is Node.Spec -> if (runningSpec == null) {
            runningSpec = node.kclass.toDescriptor().path().value
            listener.executionStarted(node)
         } else {
            queue {
               executionStarted(node)
            }
         }
         is Node.Test ->  if (runningSpec == node.testCase.spec::class.toDescriptor().path().value) {
            listener.executionStarted(node)
         } else {
            queue {
               executionStarted(node)
            }
         }
      }
   }

   override suspend fun executionFinished(node: Node, result: TestResult) {
      when (node) {
         is Node.Engine -> listener.executionFinished(node, result)
         is Node.Spec -> if (runningSpec == node.kclass.toDescriptor().path().value) {
            listener.executionFinished(node, result)
            runningSpec = null
            replay()
         } else {
            queue {
               executionFinished(node, result)
            }
         }
         is Node.Test -> if (runningSpec == node.testCase.spec::class.toDescriptor().path().value) {
            listener.executionFinished(node, result)
         } else {
            queue {
               executionFinished(node, result)
            }
         }
      }
   }

   override suspend fun executionIgnored(node: Node, reason: String?) {
      when (node) {
         is Node.Engine -> listener.executionIgnored(node, reason)
         is Node.Spec ->  if (runningSpec == node.kclass.toDescriptor().path().value) {
            listener.executionIgnored(node, reason)
         } else {
            queue {
               executionIgnored(node, reason)
            }
         }
         is Node.Test -> if (runningSpec == node.testCase.spec::class.toDescriptor().path().value) {
            listener.executionIgnored(node, reason)
         } else {
            queue {
               executionIgnored(node, reason)
            }
         }
      }
   }
}
