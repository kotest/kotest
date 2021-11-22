package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.concurrency.isIsolate
import io.kotest.engine.config.MutableConfiguration
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.runners.InstancePerLeafSpecRunner
import io.kotest.engine.spec.runners.InstancePerTestSpecRunner
import io.kotest.engine.spec.runners.SingleInstanceSpecRunner
import io.kotest.engine.test.scheduler.ConcurrentTestScheduler
import io.kotest.engine.test.scheduler.SequentialTestScheduler
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlin.math.max

@ExperimentalKotest
internal actual fun createSpecExecutorDelegate(
   listener: TestEngineListener,
   coroutineDispatcherFactory: CoroutineDispatcherFactory,
   context: EngineContext,
): SpecExecutorDelegate = JvmSpecExecutorDelegate(listener, coroutineDispatcherFactory, context.configuration)

@ExperimentalKotest
class JvmSpecExecutorDelegate(
   private val listener: TestEngineListener,
   private val dispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: ProjectConfiguration,
) : SpecExecutorDelegate {

   private val logger = Logger(this::class)

   private fun Spec.resolvedIsolationMode() =
      this.isolationMode() ?: this.isolationMode ?: configuration.isolationMode

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {

      val scheduler = when (val concurrentTests = spec.resolvedConcurrentTests(configuration.concurrentTests)) {
         MutableConfiguration.Sequential -> SequentialTestScheduler
         else -> ConcurrentTestScheduler(max(1, concurrentTests))
      }

      val isolation = spec.resolvedIsolationMode()
      logger.log { Pair(spec::class.bestName(), "isolation=$isolation") }

      val runner = when (isolation) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(
            listener,
            scheduler,
            dispatcherFactory,
            configuration
         )
         IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(
            listener,
            scheduler,
            dispatcherFactory,
            configuration
         )
         IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(
            listener,
            scheduler,
            dispatcherFactory,
            configuration
         )
      }

      return runner.execute(spec).getOrThrow()
   }
}

/**
 * Returns the concurrent tests count to use for tests in this spec.
 *
 * If threads is specified on the spec, then that will implicitly raise the concurrentTests
 * count to the same value if concurrentTests is not specified.
 *
 * Note that if this spec is annotated with @Isolate then the value
 * will be 1 regardless of the config setting.
 *
 * spec.concurrency ?: configuration.concurrentTests
 */
@OptIn(ExperimentalKotest::class)
internal fun Spec.resolvedConcurrentTests(defaultConcurrentTests: Int): Int {
   val fromSpecConcurrency = this.concurrency ?: this.concurrency()
   return when {
      this::class.isIsolate() -> MutableConfiguration.Sequential
      fromSpecConcurrency != null -> max(1, fromSpecConcurrency)
      else -> defaultConcurrentTests
   }
}
