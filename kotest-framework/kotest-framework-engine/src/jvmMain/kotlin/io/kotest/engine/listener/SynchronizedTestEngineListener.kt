package io.kotest.engine.listener

import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass
import kotlinx.coroutines.runBlocking

/**
 * Wraps a [TestEngineListener]s methods in synchronized calls to ensure no race conditions.
 */
class SynchronizedTestEngineListener(private val listener: TestEngineListener) : TestEngineListener {

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      synchronized(listener) {
         listener.engineStarted(classes)
      }
   }

   override fun engineFinished(t: List<Throwable>) {
      synchronized(listener) {
         listener.engineFinished(t)
      }
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      synchronized(listener) {
         listener.specStarted(kclass)
      }
   }

   override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
      synchronized(listener) {
         listener.specFinished(kclass, t, results)
      }
   }

   override fun testStarted(testCase: TestCase) {
      synchronized(listener) {
         listener.testStarted(testCase)
      }
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      synchronized(listener) {
         listener.testIgnored(testCase, reason)
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      synchronized(listener) {
         listener.testFinished(testCase, result)
      }
   }

   override fun specInstantiated(spec: Spec) {
      synchronized(listener) {
         listener.specInstantiated(spec)
      }
   }

   override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
      synchronized(listener) {
         listener.specInstantiationError(kclass, t)
      }
   }

   override suspend fun specFinished(
      spec: Descriptor.SpecDescriptor,
      t: Throwable?,
      results: Map<Descriptor.TestDescriptor, TestResult>
   ) {
      synchronized(listener) {
         runBlocking {
            listener.specFinished(spec, t, results)
         }
      }
   }

   override suspend fun specStarted(spec: Descriptor.SpecDescriptor) {
      synchronized(listener) {
         runBlocking {
            listener.specStarted(spec)
         }
      }
   }

   override fun testFinished(descriptor: Descriptor.TestDescriptor, result: TestResult) {
      synchronized(listener) {
         listener.testFinished(descriptor, result)
      }
   }

   override fun testIgnored(descriptor: Descriptor.TestDescriptor, reason: String?) {
      synchronized(listener) {
         listener.testIgnored(descriptor, reason)
      }
   }

   override fun testStarted(descriptor: Descriptor.TestDescriptor) {
      synchronized(listener) {
         listener.testStarted(descriptor)
      }
   }
}
