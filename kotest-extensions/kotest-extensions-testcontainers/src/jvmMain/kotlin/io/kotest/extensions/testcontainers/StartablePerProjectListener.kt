package io.kotest.extensions.testcontainers

import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.lifecycle.Startable

/**
 * [StartablePerProjectListener] starts the given [startable] before execution of all specs
 * and stops after execution of all specs.
 *
 * [startable] can any of [GenericContainer] [DockerComposeContainer] [LocalStackContainer] etc.
 *
 * This listener should be used when you want to use a single container for all tests in project.
 *
 * @see
 * [StartablePerTestListener]
 * */

class StartablePerProjectListener<T : Startable>(val startable: T, val containerName: String) : TestListener, ProjectListener {
   override val name = containerName
   private val testLifecycleAwareListener = TestLifecycleAwareListener(startable)

   override suspend fun beforeProject() {
      withContext(Dispatchers.IO) {
         startable.start()
      }
   }

   override suspend fun beforeTest(testCase: TestCase) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.beforeTest(testCase)
      }
   }

   override suspend fun afterProject() {
      withContext(Dispatchers.IO) {
         startable.stop()
      }
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      withContext(Dispatchers.IO) {
         testLifecycleAwareListener.afterTest(testCase, result)
      }
   }
}
