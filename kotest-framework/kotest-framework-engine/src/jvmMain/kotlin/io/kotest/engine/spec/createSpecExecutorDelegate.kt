package io.kotest.engine.spec

import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.CoroutineDispatcherController
import io.kotest.engine.concurrency.resolvedConcurrentTests
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.runners.InstancePerLeafSpecRunner
import io.kotest.engine.spec.runners.InstancePerTestSpecRunner
import io.kotest.engine.spec.runners.SingleInstanceSpecRunner
import io.kotest.engine.test.scheduler.ConcurrentTestScheduler
import io.kotest.engine.test.scheduler.SequentialTestScheduler
import kotlin.math.max

actual fun createSpecExecutorDelegate(
   listener: TestEngineListener,
   controller: CoroutineDispatcherController,
): SpecExecutorDelegate = object : SpecExecutorDelegate {

   private fun Spec.resolvedIsolationMode() =
      this.isolationMode() ?: this.isolationMode ?: configuration.isolationMode

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {

      val scheduler = when (val concurrentTests = spec.resolvedConcurrentTests()) {
         Configuration.Sequential -> SequentialTestScheduler
         else -> ConcurrentTestScheduler(max(1, concurrentTests))
      }

      val runner = when (spec.resolvedIsolationMode()) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener, scheduler, controller)
         IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(listener, scheduler, controller)
         IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(listener, scheduler, controller)
      }

      return runner.execute(spec).getOrThrow()
   }
}
