package io.kotest.engine.listener

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener]s methods with a mutex to ensure only one method is called at a time.
 */
class ThreadSafeTestEngineListener(private val listener: TestEngineListener) : TestEngineListener {

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

   override suspend fun specFinished(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      mutex.withLock {
         listener.specFinished(kclass, results)
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

   override suspend fun engineShutdown() {
      mutex.withLock {
         listener.engineShutdown()
      }
   }

   override suspend fun engineStartup() {
      mutex.withLock {
         listener.engineStartup()
      }
   }

   override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {
      mutex.withLock {
         listener.specExit(kclass, t)
      }
   }

   override suspend fun specIgnored(kclass: KClass<out Spec>) {
      mutex.withLock {
         listener.specIgnored(kclass)
      }
   }

   override suspend fun specInactive(kclass: KClass<*>) {
      mutex.withLock {
         listener.specInactive(kclass)
      }
   }

   override suspend fun specEnter(kclass: KClass<out Spec>) {
      mutex.withLock {
         listener.specEnter(kclass)
      }
   }
}
