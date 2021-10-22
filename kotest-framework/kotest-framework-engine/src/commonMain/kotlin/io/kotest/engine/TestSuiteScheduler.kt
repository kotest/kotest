package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.mpp.log

/**
 * A [TestSuiteScheduler] is responsible for launching each spec from a [TestSuite] into a coroutine.
 */
@ExperimentalKotest
internal interface TestSuiteScheduler {
   suspend fun schedule(suite: TestSuite, listener: TestEngineListener): EngineResult
}

/**
 * A [TestSuiteScheduler] that launches specs sequentially in a loop.
 */
internal object SequentialTestSuiteScheduler : TestSuiteScheduler {

   override suspend fun schedule(suite: TestSuite, listener: TestEngineListener): EngineResult {
      log { "LoopingTestSuiteScheduler: Executing ${suite.specs} specs" }
      suite.specs.forEach {
         val executor = SpecExecutor(NoopTestEngineListener, NoopCoroutineDispatcherFactory, configuration)
         executor.execute(it)
      }
      return EngineResult(emptyList())
   }
}

