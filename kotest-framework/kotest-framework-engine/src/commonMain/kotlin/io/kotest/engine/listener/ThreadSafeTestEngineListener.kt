package io.kotest.engine.listener

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener]s methods with a mutex to ensure only one method is called at a time.
 */
class ThreadSafeTestEngineListener(private val listener: TestEngineListener) : TestEngineListener {

   private val mutex = Mutex()

   override suspend fun engineInitialized(context: EngineContext) {
      mutex.withLock {
         listener.engineInitialized(context)
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

   override suspend fun engineStarted() {
      mutex.withLock {
         listener.engineStarted()
      }
   }

   override suspend fun specExit(kclass: KClass<*>, t: Throwable?) {
      mutex.withLock {
         listener.specExit(kclass, t)
      }
   }

   override suspend fun specIgnored(kclass: KClass<*>) {
      mutex.withLock {
         listener.specIgnored(kclass)
      }
   }

   override suspend fun specInactive(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      mutex.withLock {
         listener.specInactive(kclass, results)
      }
   }

   override suspend fun specAborted(kclass: KClass<*>, t: Throwable) {
      mutex.withLock {
         listener.specAborted(kclass, t)
      }
   }

   override suspend fun specEnter(kclass: KClass<*>) {
      mutex.withLock {
         listener.specEnter(kclass)
      }
   }
}
