package io.kotest.extensions.testcontainers

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.lifecycle.Startable
import org.testcontainers.lifecycle.TestLifecycleAware

/**
 * [StartablePerTestListener] starts the given [startable] before execution of each test in the spec
 * and stops after execution of each test. If the [startable] also inherit from [TestLifecycleAware]
 * then its [beforeTest] and [afterTest] method are also called by the listener.
 *
 * [startable] can any of [GenericContainer] [DockerComposeContainer] [LocalStackContainer] etc.
 *
 * This should be used when you want a fresh container for each test.
 *
 * @see[StartablePerSpecListener]
 * */
class StartablePerTestListener<T : Startable>(val startable: T) : TestListener {
   private val testLifecycleAwareListener = TestLifecycleAwareListener(startable)

   override suspend fun beforeTest(testCase: TestCase) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
         startable.start()
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
         startable.stop()
      }
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      if (testCase.type != TestType.Container) return

      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
         startable.start()
      }
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      if (testCase.type != TestType.Container) return

      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
         startable.stop()
      }
   }

   override suspend fun beforeEach(testCase: TestCase) {
      if (testCase.type != TestType.Test) return

      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
         startable.start()
      }
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (testCase.type != TestType.Test) return

      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
         startable.stop()
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
         startable.start()
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
         startable.stop()
      }
   }
}
