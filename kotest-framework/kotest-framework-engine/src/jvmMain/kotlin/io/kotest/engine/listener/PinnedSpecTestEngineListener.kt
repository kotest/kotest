@file:Suppress("LocalVariableName")

package io.kotest.engine.listener

import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.spec.toDescription
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener] methods to ensure that only test notifications
 * are passed to the delegated listener for one spec at at time. Notifications that
 * are not for the current spec are delayed until the current spec completes.
 */
class PinnedSpecTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

   private val runningSpec = AtomicReference<String?>(null)
   private val callbacks = mutableListOf<suspend () -> Unit>()
   private val mutex = Mutex()

   private suspend fun queue(fn: suspend () -> Unit) {
      callbacks.add { fn() }
   }

   private suspend fun replay() {
      mutex.withLock {
         val _callbacks = callbacks.toList()
         callbacks.clear()
         _callbacks.forEach { it.invoke() }
      }
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      mutex.withLock {
         listener.engineFinished(t)
      }
   }

   override suspend fun engineStarted(classes: List<KClass<*>>) {
      mutex.withLock {
         listener.engineStarted(classes)
      }
   }

   override suspend fun specInstantiated(spec: Spec) {
      mutex.withLock {
         if (runningSpec.get() == spec::class.toDescription().path().value) {
            listener.specInstantiated(spec)
         } else {
            queue {
               specInstantiated(spec)
            }
         }
      }
   }

   override suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {
      mutex.withLock {
         if (runningSpec.get() == kclass.toDescription().path().value) {
            listener.specInstantiationError(kclass, t)
         } else {
            queue {
               specInstantiationError(kclass, t)
            }
         }
      }
   }

   override suspend fun testStarted(testCase: TestCase) {
      mutex.withLock {
         if (runningSpec.get() == testCase.spec::class.toDescription().path().value) {
            listener.testStarted(testCase)
         } else {
            queue {
               testStarted(testCase)
            }
         }
      }
   }

   override suspend fun testStarted(descriptor: Descriptor.TestDescriptor) {
      mutex.withLock {
         if (runningSpec.get() == descriptor.spec()?.classname) {
            listener.testStarted(descriptor)
         } else {
            queue {
               testStarted(descriptor)
            }
         }
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      mutex.withLock {
         if (runningSpec.get() == testCase.spec::class.toDescription().path().value) {
            listener.testIgnored(testCase, reason)
         } else {
            queue {
               testIgnored(testCase, reason)
            }
         }
      }
   }

   override suspend fun testIgnored(descriptor: Descriptor.TestDescriptor, reason: String?) {
      mutex.withLock {
         if (runningSpec.get() == descriptor.spec()?.classname) {
            listener.testIgnored(descriptor, reason)
         } else {
            queue {
               testIgnored(descriptor, reason)
            }
         }
      }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      mutex.withLock {
         if (runningSpec.get() == testCase.spec::class.toDescription().path().value) {
            listener.testFinished(testCase, result)
         } else {
            queue {
               testFinished(testCase, result)
            }
         }
      }
   }

   override suspend fun testFinished(descriptor: Descriptor.TestDescriptor, result: TestResult) {
      mutex.withLock {
         if (runningSpec.get() == descriptor.spec()?.classname) {
            listener.testFinished(descriptor, result)
         } else {
            queue {
               testFinished(descriptor, result)
            }
         }
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      mutex.withLock {
         if (runningSpec.compareAndSet(null, kclass.toDescription().path().value)) {
            listener.specStarted(kclass)
         } else {
            queue {
               specStarted(kclass)
            }
         }
      }
   }

   override suspend fun specFinished(
      kclass: KClass<*>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) {
      mutex.withLock {
         if (runningSpec.get() == kclass.toDescription().path().value) {
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

   override suspend fun specFinished(
      descriptor: Descriptor.SpecDescriptor,
      t: Throwable?,
      results: Map<Descriptor.TestDescriptor, TestResult>
   ) {
      mutex.withLock {
         if (runningSpec.get() == descriptor.classname) {
            runBlocking {
               listener.specFinished(descriptor, t, results)
            }
            runningSpec.set(null)
            replay()
         } else {
            queue {
               runBlocking {
                  listener.specFinished(descriptor, t, results)
               }
            }
         }
      }
   }

   override suspend fun specStarted(descriptor: Descriptor.SpecDescriptor) {
      mutex.withLock {
         if (runningSpec.compareAndSet(null, descriptor.classname)) {
            runBlocking {
               listener.specStarted(descriptor)
            }
         } else {
            queue {
               runBlocking {
                  listener.specStarted(descriptor)
               }
            }
         }
      }
   }
}
