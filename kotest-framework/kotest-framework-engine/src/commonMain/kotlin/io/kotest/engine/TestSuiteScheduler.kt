package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.core.project.TestSuite
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.log

/**
 * A [TestSuiteScheduler] is responsible for launching each spec from a [TestSuite] into a coroutine.
 */
@ExperimentalKotest
internal interface TestSuiteScheduler {
   suspend fun schedule(
      suite: TestSuite,
      listener: TestEngineListener,
   ): EngineResult
}

/**
 * A [TestSuiteScheduler] that launches specs sequentially in a loop.
 */
@ExperimentalKotest
internal class SequentialTestSuiteScheduler(
   private val context: EngineContext
) : TestSuiteScheduler {

   override suspend fun schedule(
      suite: TestSuite,
      listener: TestEngineListener,
   ): EngineResult {
      log { "LoopingTestSuiteScheduler: Executing ${suite.specs} specs" }
      suite.specs.forEach {
         val executor = SpecExecutor(context.listener, NoopCoroutineDispatcherFactory, context)
         executor.execute(it)
      }
      return EngineResult(emptyList())
   }
}

