package io.kotest.engine

import io.kotest.common.ExperimentalKotest
import io.kotest.core.project.TestSuite
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.core.Logger

/**
 * A [TestSuiteScheduler] is responsible for launching each spec from a [TestSuite] into a coroutine.
 */
@ExperimentalKotest
internal interface TestSuiteScheduler {
   suspend fun schedule(
      suite: TestSuite,
   ): EngineResult
}

/**
 * A [TestSuiteScheduler] that launches specs sequentially in a loop.
 */
@ExperimentalKotest
internal class SequentialTestSuiteScheduler(
   private val context: EngineContext
) : TestSuiteScheduler {

   private val logger = Logger(this::class)

   override suspend fun schedule(
      suite: TestSuite,
   ): EngineResult {

      logger.log { Pair(null, "Executing ${suite.specs} specs") }
      val collector = CollectingTestEngineListener()

      val errors = mutableListOf<Throwable>()

      suite.specs.forEach {
         try {
            if (context.configuration.projectWideFailFast && collector.errors) {
               context.listener.specIgnored(it.kclass, null)
            } else {
               val executor = SpecExecutor(NoopCoroutineDispatcherFactory, context.mergeListener(collector))
               executor.execute(it)
            }
         } catch (e:Throwable) {
            println(e)
            errors.add(e)
         }
      }

      return EngineResult(errors.toList())
   }
}

