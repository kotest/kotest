@file:Suppress("LocalVariableName")

package io.kotest.engine.reporter

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

/**
 * Wraps a [Reporter] methods to ensure that only test notifications
 * are passed to the underlying listener for one spec at at time. Notifications that
 * are not for the current spec are delayed until the current spec completes.
 */
class IsolatedReporter(val reporter: Reporter) : Reporter {

   private val runningSpec = AtomicReference<Descriptor?>(null)
   private val callbacks = mutableListOf<() -> Unit>()

   private fun queue(fn: () -> Unit) {
      callbacks.add { fn() }
   }

   private fun replay() {
      synchronized(reporter) {
         val _callbacks = callbacks.toList()
         callbacks.clear()
         _callbacks.forEach { it.invoke() }
      }
   }

   override fun hasErrors(): Boolean {
      return reporter.hasErrors()
   }

   override fun engineStarted(classes: List<KClass<*>>) {
      reporter.engineStarted(classes)
   }

   override fun engineFinished(t: List<Throwable>) {
      reporter.engineFinished(t)
   }

   override fun specStarted(kclass: KClass<*>) {
      synchronized(reporter) {
         if (runningSpec.compareAndSet(null, kclass.toDescriptor())) {
            reporter.specStarted(kclass)
         } else {
            queue {
               specStarted(kclass)
            }
         }
      }
   }

   override fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {
      synchronized(reporter) {
         if (runningSpec.get() == kclass.toDescriptor()) {
            reporter.specFinished(kclass, t, results)
            runningSpec.set(null)
            replay()
         } else {
            queue {
               specFinished(kclass, t, results)
            }
         }
      }
   }

   override fun testStarted(testCase: TestCase) {
      synchronized(reporter) {
         if (runningSpec.get() == testCase.spec::class.toDescriptor()) {
            reporter.testStarted(testCase)
         } else {
            queue {
               testStarted(testCase)
            }
         }
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      synchronized(reporter) {
         if (runningSpec.get() == testCase.spec::class.toDescriptor()) {
            reporter.testFinished(testCase, result)
         } else {
            queue {
               testFinished(testCase, result)
            }
         }
      }
   }
}
