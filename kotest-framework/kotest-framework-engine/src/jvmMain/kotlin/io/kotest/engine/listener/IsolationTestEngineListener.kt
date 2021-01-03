@file:Suppress("LocalVariableName")

package io.kotest.engine.listener

import io.kotest.core.spec.Spec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.spec.toDescription
import io.kotest.framework.discovery.log
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener] methods to ensure that only test notifications
 * are passed to the underlying listener for one spec at at time. Notifications that
 * are not for the current spec are delayed until the current spec completes.
 */
class IsolationTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

   private val runningSpec = AtomicReference<Description?>(null)
   private val callbacks = mutableListOf<() -> Unit>()

   private fun queue(fn: () -> Unit) {
      callbacks.add { fn() }
   }

   private fun replay() {
      synchronized(listener) {
         val _callbacks = callbacks.toList()
         callbacks.clear()
         _callbacks.forEach { it.invoke() }
      }
   }

   override fun engineFinished(t: List<Throwable>) {
      synchronized(listener) {
         listener.engineFinished(t)
      }
   }

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      synchronized(listener) {
         listener.engineStarted(classes)
      }
   }

   override fun specInstantiated(spec: Spec) {
      synchronized(listener) {
         if (runningSpec.get() == spec::class.toDescription()) {
            listener.specInstantiated(spec)
         } else {
            queue {
               specInstantiated(spec)
            }
         }
      }
   }

   override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
      synchronized(listener) {
         if (runningSpec.get() == kclass.toDescription()) {
            listener.specInstantiationError(kclass, t)
         } else {
            queue {
               specInstantiationError(kclass, t)
            }
         }
      }
   }

   override fun testStarted(testCase: TestCase) {
      synchronized(listener) {
         if (runningSpec.get() == testCase.spec::class.toDescription()) {
            listener.testStarted(testCase)
         } else {
            queue {
               testStarted(testCase)
            }
         }
      }
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      synchronized(listener) {
         if (runningSpec.get() == testCase.spec::class.toDescription()) {
            listener.testIgnored(testCase, reason)
         } else {
            queue {
               testIgnored(testCase, reason)
            }
         }
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      synchronized(listener) {
         if (runningSpec.get() == testCase.spec::class.toDescription()) {
            listener.testFinished(testCase, result)
         } else {
            queue {
               testFinished(testCase, result)
            }
         }
      }
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      synchronized(listener) {
         log("IsolationTestEngineListener: specStarted $kclass")
         if (runningSpec.compareAndSet(null, kclass.toDescription())) {
            listener.specStarted(kclass)
         } else {
            queue {
               specStarted(kclass)
            }
         }
      }
   }

   override fun specFinished(
      kclass: KClass<out Spec>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) {
      synchronized(listener) {
         log("IsolationTestEngineListener: specFinished $kclass")
         if (runningSpec.get() == kclass.toDescription()) {
            listener.specFinished(kclass, t, results)
            runningSpec.set(null)
            replay()
         } else {
            queue {
               specFinished(kclass, t, results)
            }
         }
      }
   }
}
