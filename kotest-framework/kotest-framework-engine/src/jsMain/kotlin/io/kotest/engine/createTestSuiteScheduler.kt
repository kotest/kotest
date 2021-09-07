package io.kotest.engine

import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.toDescription
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.log

/**
 * Returns the appropriate TestSuiteScheduler for the platform.
 */
actual fun createTestSuiteScheduler(): TestSuiteScheduler = JsTestSuiteScheduler

/**
 * A [TestSuiteScheduler] that launches specs sequentially.
 */
object JsTestSuiteScheduler : TestSuiteScheduler {

   override suspend fun schedule(suite: TestSuite, listener: TestEngineListener): EngineResult {
      log { "JsTestSuiteScheduler: Executing ${suite.specs} specs" }
      execute(suite.specs)
      return EngineResult(emptyList())
   }

   private suspend fun execute(specs: List<SpecRef>) {
      specs.forEach {
         describe(it.instance().getOrThrow()::class.toDescription().displayName() + "_qqqq") {
            it("gggggg") { done -> done(null) }
         }
      }

//      specs.forEach {
//         val executor = SpecExecutor(NoopTestEngineListener)
//         executor.execute(it)
//      }
   }
}
