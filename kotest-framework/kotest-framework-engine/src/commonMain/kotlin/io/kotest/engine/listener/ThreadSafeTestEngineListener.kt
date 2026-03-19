package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

/**
 * Wraps a [TestEngineListener]s methods with a mutex to ensure only one method is called at a time.
 */
@KotestInternal
class ThreadSafeTestEngineListener(private val listener: TestEngineListener) : TestEngineListener {

   private val mutex = Mutex()

   override suspend fun engineInitialized(context: TestEngineInitializedContext) {
      mutex.withLock {
         listener.engineInitialized(context)
      }
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      mutex.withLock {
         listener.engineFinished(t)
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

   override suspend fun engineStarted() {
      mutex.withLock {
         listener.engineStarted()
      }
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      mutex.withLock {
         listener.specFinished(ref, result)
      }
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      mutex.withLock {
         listener.specIgnored(kclass, reason)
      }
   }

   override suspend fun specStarted(ref: SpecRef) {
      mutex.withLock {
         listener.specStarted(ref)
      }
   }
}
