package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.isRootTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.lifecycle.TestLifecycleAware
import java.util.Optional

@Deprecated("use ContainerExtension")
class TestContainerExtension<T : GenericContainer<out T>>(
   private val container: GenericContainer<out T>,
   private val lifecycleMode: LifecycleMode = LifecycleMode.Spec,
) : MountableExtension<T, T>, TestListener, AfterSpecListener {

   companion object {
      operator fun invoke(name: String): TestContainerExtension<GenericContainer<Nothing>> =
         TestContainerExtension(GenericContainer<Nothing>(name))

      operator fun invoke(name: String, lifecycleMode: LifecycleMode): TestContainerExtension<GenericContainer<Nothing>> =
         TestContainerExtension(GenericContainer<Nothing>(name), lifecycleMode)
   }

   override fun mount(configure: T.() -> Unit): T {
      (container as T).configure()
      if (lifecycleMode == LifecycleMode.Spec) {
         container.start()
      }
      return container
   }

   override suspend fun afterSpec(spec: Spec) {
      if (container.isRunning) {
         withContext(Dispatchers.IO) {
            stop()
         }
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
