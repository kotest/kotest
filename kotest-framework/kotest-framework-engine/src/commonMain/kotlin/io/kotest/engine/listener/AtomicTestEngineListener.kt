package io.kotest.engine.listener

import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener]s methods in locks to ensure no race conditions.
 */
class AtomicTestEngineListener(private val listener: TestEngineListener) : TestEngineListener {

   private val mutex = Mutex()

   override suspend fun engineStarted(classes: List<KClass<*>>) {
      mutex.withLock {
         listener.engineStarted(classes)
      }
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      mutex.withLock {
         listener.engineFinished(t)
      }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      mutex.withLock {
         listener.specStarted(kclass)
      }
   }

   override suspend fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {
      mutex.withLock {
         listener.specFinished(kclass, t, results)
      }
   }

   override suspend fun testStarted(testCase: TestCase) {
      mutex.withLock {
         listener.testStarted(testCase)
      }
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      mutex.withLock {
         listener.testIgnored(testCase, reason)
      }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      mutex.withLock {
         listener.testFinished(testCase, result)
      }
   }

   override suspend fun specInstantiated(spec: Spec) {
      mutex.withLock {
         listener.specInstantiated(spec)
      }
   }

   override suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {
      mutex.withLock {
         listener.specInstantiationError(kclass, t)
      }
   }

   override suspend fun specFinished(
      spec: Descriptor.SpecDescriptor,
      t: Throwable?,
      results: Map<Descriptor.TestDescriptor, TestResult>
   ) {
      mutex.withLock {
         listener.specFinished(spec, t, results)
      }
   }

   override suspend fun specStarted(spec: Descriptor.SpecDescriptor) {
      mutex.withLock {
         listener.specStarted(spec)
      }
   }

   override suspend fun testFinished(descriptor: Descriptor.TestDescriptor, result: TestResult) {
      mutex.withLock {
         listener.testFinished(descriptor, result)
      }
   }

   override suspend fun testIgnored(descriptor: Descriptor.TestDescriptor, reason: String?) {
      mutex.withLock {
         listener.testIgnored(descriptor, reason)
      }
   }

   override suspend fun testStarted(descriptor: Descriptor.TestDescriptor) {
      mutex.withLock {
         listener.testStarted(descriptor)
      }
   }
}
