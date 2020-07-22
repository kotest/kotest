package io.kotest.extensions.testcontainers

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.lifecycle.Startable
import org.testcontainers.lifecycle.TestLifecycleAware


/**
 * [StartablePerSpecListener] starts the given [startable] before execution of any test in the spec
 * and stops after execution of all tests. If the [startable] also inherit from [TestLifecycleAware]
 * then its [beforeTest] and [afterTest] method are also called by the listener.
 *
 * [startable] can any of [GenericContainer] [DockerComposeContainer] [LocalStackContainer] etc.
 *
 * This listener should be used when you want to use a single container for all tests in a single spec class.
 *
 * @see
 * [StartablePerTestListener]
 * */

class StartablePerSpecListener<T : Startable>(val startable: T) : TestListener {
   private val testLifecycleAwareListener = TestLifecycleAwareListener(startable)

   override suspend fun beforeSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         startable.start()
      }
   }

   override suspend fun beforeTest(testCase: TestCase) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
      }
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      if (testCase.type != TestType.Container) return

      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
      }
   }

   override suspend fun beforeEach(testCase: TestCase) {
      if (testCase.type != TestType.Test) return

      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         startable.stop()
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
      }
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      if (testCase.type != TestType.Container) return

      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
      }
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (testCase.type != TestType.Container) return

      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
      }
   }
}
