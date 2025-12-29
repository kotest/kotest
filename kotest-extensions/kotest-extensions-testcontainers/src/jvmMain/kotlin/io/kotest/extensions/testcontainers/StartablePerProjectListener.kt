package io.kotest.extensions.testcontainers

import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
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
@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
class StartablePerProjectListener<T : Startable>(private val startable: T) : TestListener, ProjectListener {

   @Deprecated("The containerName arg is no longer used")
   constructor(startable: T, containerName: String) : this(startable)

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
