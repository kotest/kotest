@file:Suppress("LocalVariableName")

package io.kotest.engine.listener

import io.kotest.engine.descriptors.toDescriptor
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

   override suspend fun engineStarted() {
      listener.engineStarted()
   }

   override suspend fun engineInitialized(context: EngineContext) {
      listener.engineInitialized(context)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      listener.engineFinished(t)
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      if (runningSpec == null) {
         runningSpec = kclass.toDescriptor().path().value
         listener.specStarted(kclass)
      } else {
         queue {
            specStarted(kclass)
         }
      }
   }

   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
      if (runningSpec == kclass.toDescriptor().path().value) {
         listener.specFinished(kclass, result)
         runningSpec = null
         replay()
      } else {
         queue {
            specFinished(kclass, result)
         }
      }
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      listener.specIgnored(kclass, reason)
   }

   override suspend fun testStarted(testCase: TestCase) {
      if (runningSpec == testCase.spec::class.toDescriptor().path().value) {
         listener.testStarted(testCase)
      } else {
         queue {
            testStarted(testCase)
         }
      }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      if (runningSpec == testCase.spec::class.toDescriptor().path().value) {
         listener.testFinished(testCase, result)
      } else {
         queue {
            testFinished(testCase, result)
         }
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      if (runningSpec == testCase.spec::class.toDescriptor().path().value) {
         listener.testIgnored(testCase, reason)
      } else {
         queue {
            testIgnored(testCase, reason)
         }
      }
   }
}
