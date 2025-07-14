package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.isRootTest
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.lifecycle.TestLifecycleAware
import java.util.Optional
import org.testcontainers.containers.DockerComposeContainer

@Deprecated("Use TestContainerProjectExtension or TestContainerSpeccExtension instead")
class DockerComposeContainersExtension<T : DockerComposeContainer<*>>(
   private val container: T,
   private val lifecycleMode: LifecycleMode = LifecycleMode.Spec,
) : MountableExtension<T, T>, TestListener, AfterSpecListener {

   companion object {
      operator fun invoke(composeFile: File): DockerComposeContainersExtension<*> =
         DockerComposeContainersExtension(DockerComposeContainer(composeFile))

      operator fun invoke(
         composeFile: File,
         lifecycleMode: LifecycleMode
      ): DockerComposeContainersExtension<*> =
         DockerComposeContainersExtension(DockerComposeContainer(composeFile), lifecycleMode)
   }

   override fun mount(configure: T.() -> Unit): T {
      @Suppress("UNCHECKED_CAST")
      container.configure()
      if (lifecycleMode == LifecycleMode.Spec) {
         container.start()
      }
      return container
   }

   override suspend fun afterSpec(spec: Spec) {
      withContext(Dispatchers.IO) {
         stop()
      }
   }

   override suspend fun beforeAny(testCase: TestCase) {
      val every = lifecycleMode == LifecycleMode.EveryTest
      val root = lifecycleMode == LifecycleMode.Root && testCase.isRootTest()
      val leaf = lifecycleMode == LifecycleMode.Leaf && testCase.type == TestType.Test
      if (every || root || leaf) {
         lifecycleBeforeTest(testCase)
         start()
      }
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      val every = lifecycleMode == LifecycleMode.EveryTest
      val root = lifecycleMode == LifecycleMode.Root && testCase.isRootTest()
      val leaf = lifecycleMode == LifecycleMode.Leaf && testCase.type == TestType.Test
      if (every || root || leaf) {
         lifecycleAfterTest(testCase, result)
         stop()
      }
   }

   private suspend fun start() {
      withContext(Dispatchers.IO) {
         container.start()
      }
   }

   private suspend fun stop() {
      withContext(Dispatchers.IO) {
         container.stop()
      }
   }

   private suspend fun lifecycleBeforeTest(testCase: TestCase) {
      when (container) {
         is TestLifecycleAware -> withContext(Dispatchers.IO) {
            container.beforeTest(testCase.toTestDescription())
         }
      }
   }

   private suspend fun lifecycleAfterTest(testCase: TestCase, result: TestResult) {
      when (container) {
         is TestLifecycleAware -> withContext(Dispatchers.IO) {
            container.afterTest(
               testCase.toTestDescription(), Optional.ofNullable(result.errorOrNull)
            )
         }
      }
   }
}
