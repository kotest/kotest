@file:Suppress("LocalVariableName")

package io.kotest.engine.listener

import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener] methods to ensure that only test notifications
 * are passed to the delegated listener for one spec at at time. Notifications that
 * are not for the current spec are delayed until the current spec completes.
 *
 * Note: This class is not thread safe. It is up to the caller to ensure that calls
 * to the methods of this listener are strictly sequential.
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

   override suspend fun engineStarted(classes: List<KClass<*>>) {
      listener.engineStarted(classes)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      listener.engineFinished(t)
   }

   override suspend fun specInstantiated(spec: Spec) {
      if (runningSpec == spec::class.toDescription().path().value) {
         listener.specInstantiated(spec)
      } else {
         queue {
            specInstantiated(spec)
         }
      }
   }

   override suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {
      if (runningSpec == kclass.toDescription().path().value) {
         listener.specInstantiationError(kclass, t)
      } else {
         queue {
            specInstantiationError(kclass, t)
         }
      }
   }

   override suspend fun testStarted(testCase: TestCase) {
      if (runningSpec == testCase.spec::class.toDescription().path().value) {
         listener.testStarted(testCase)
      } else {
         queue {
            testStarted(testCase)
         }
      }
   }

   override suspend fun testStarted(descriptor: Descriptor.TestDescriptor) {
      if (runningSpec == descriptor.spec()?.classname) {
         listener.testStarted(descriptor)
      } else {
         queue {
            testStarted(descriptor)
         }
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      if (runningSpec == testCase.spec::class.toDescription().path().value) {
         listener.testIgnored(testCase, reason)
      } else {
         queue {
            testIgnored(testCase, reason)
         }
      }
   }

   override suspend fun testIgnored(descriptor: Descriptor.TestDescriptor, reason: String?) {
      if (runningSpec == descriptor.spec()?.classname) {
         listener.testIgnored(descriptor, reason)
      } else {
         queue {
            testIgnored(descriptor, reason)
         }
      }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      if (runningSpec == testCase.spec::class.toDescription().path().value) {
         listener.testFinished(testCase, result)
      } else {
         queue {
            testFinished(testCase, result)
         }
      }
   }

   override suspend fun testFinished(descriptor: Descriptor.TestDescriptor, result: TestResult) {
      if (runningSpec == descriptor.spec()?.classname) {
         listener.testFinished(descriptor, result)
      } else {
         queue {
            testFinished(descriptor, result)
         }
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      if (runningSpec == null) {
         runningSpec = kclass.toDescription().path().value
         listener.specStarted(kclass)
      } else {
         queue {
            specStarted(kclass)
         }
      }
   }

   override suspend fun specFinished(
      kclass: KClass<*>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) {
      if (runningSpec == kclass.toDescription().path().value) {
         listener.specFinished(kclass, t, results)
         runningSpec = null
         replay()
      } else {
         queue {
            specFinished(kclass, t, results)
         }
      }
   }

   override suspend fun specFinished(
      descriptor: Descriptor.SpecDescriptor,
      t: Throwable?,
      results: Map<Descriptor.TestDescriptor, TestResult>
   ) {
      if (runningSpec == descriptor.classname) {
         listener.specFinished(descriptor, t, results)
         runningSpec = null
         replay()
      } else {
         queue {
            specFinished(descriptor, t, results)
         }
      }
   }

   override suspend fun specStarted(descriptor: Descriptor.SpecDescriptor) {
      if (runningSpec == null) {
         runningSpec = descriptor.classname
         listener.specStarted(descriptor)
      } else {
         queue {
            specStarted(descriptor)
         }
      }
   }
}
