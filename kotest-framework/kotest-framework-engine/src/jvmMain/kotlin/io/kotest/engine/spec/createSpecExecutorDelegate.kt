package io.kotest.engine.spec

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.concurrency.isIsolate
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.runners.InstancePerLeafSpecRunner
import io.kotest.engine.spec.runners.InstancePerTestSpecRunner
import io.kotest.engine.spec.runners.SingleInstanceSpecRunner
import io.kotest.engine.test.scheduler.ConcurrentTestScheduler
import io.kotest.engine.test.scheduler.SequentialTestScheduler
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlin.math.max

internal actual fun createSpecExecutorDelegate(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   context: EngineContext,
): SpecExecutorDelegate = JvmSpecExecutorDelegate(defaultCoroutineDispatcherFactory, context)

internal class JvmSpecExecutorDelegate(
   private val dispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
) : SpecExecutorDelegate {

   private val logger = Logger(JvmSpecExecutorDelegate::class)

   private fun Spec.resolvedIsolationMode() =
      this.isolationMode() ?: this.isolationMode ?: context.configuration.isolationMode

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {

      val scheduler = when (val concurrentTests = spec.resolvedConcurrentTests(context.configuration.concurrentTests)) {
         ProjectConfiguration.Sequential -> SequentialTestScheduler
         else -> ConcurrentTestScheduler(max(1, concurrentTests))
      }

      val isolation = spec.resolvedIsolationMode()
      logger.log { Pair(spec::class.bestName(), "isolation=$isolation") }

      val runner = when (isolation) {
         IsolationMode.SingleInstance -> SingleInstanceSpecRunner(
            scheduler,
            dispatcherFactory,
            context
         )

         IsolationMode.InstancePerTest -> InstancePerTestSpecRunner(
            scheduler,
            dispatcherFactory,
            context,
         )

         IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(
            dispatcherFactory,
            context,
         )
      }

      return runner.execute(spec).getOrThrow()
   }
}

/**
 * Returns the concurrent tests count to use for tests in this spec.
 *
 * If threads is specified on the spec, then that will implicitly raise the
 * [concurrentTests][io.kotest.core.config.AbstractProjectConfig.concurrentTests]
 * count to the same value if
 * [concurrentTests][io.kotest.core.config.AbstractProjectConfig.concurrentTests]
 * is not specified.
 *
 * Note that if this spec is annotated with [@Isolate][io.kotest.core.annotation.Isolate] then
 * the value will be 1 regardless of the config setting.
 *
 * ```
 * spec.concurrency ?: configuration.concurrentTests
 * ```
 */
internal fun Spec.resolvedConcurrentTests(defaultConcurrentTests: Int): Int {
   val fromSpecConcurrency = this.concurrency ?: this.concurrency()
   return when {
      this::class.isIsolate() -> ProjectConfiguration.Sequential
      fromSpecConcurrency != null -> max(1, fromSpecConcurrency)
      else -> defaultConcurrentTests
   }
}
