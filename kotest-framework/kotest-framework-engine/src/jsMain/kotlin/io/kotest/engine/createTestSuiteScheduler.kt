package io.kotest.engine

import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.core.spec.SpecRef
import io.kotest.mpp.log
import kotlin.reflect.KClass

/**
 * Returns the appropriate TestSuiteScheduler for the platform.
 */
actual fun createTestSuiteScheduler(): TestSuiteScheduler = JsTestSuiteScheduler

/**
 * A [TestSuiteScheduler] that launches specs sequentially, using the spec completion
 * callbacks to trigger to the next spec.
 */
object JsTestSuiteScheduler : TestSuiteScheduler {

   override suspend fun schedule(suite: TestSuite, listener: TestEngineListener): EngineResult {
      log { "JsTestSuiteScheduler: Executing ${suite.specs} specs" }
      execute(suite.specs, listener)
      return EngineResult(emptyList())
   }

   private suspend fun execute(specs: List<SpecRef>, listener: TestEngineListener) {
      if (specs.isNotEmpty()) {
         val callback = object : TestEngineListener {
            override suspend fun specExecutorAboutToReturn(kclass: KClass<*>) {
               execute(specs.drop(1), listener)
            }
         }
         val executor = SpecExecutor(CompositeTestEngineListener(listOf(listener, callback)))
         executor.execute(specs.first())
      }
   }
}
