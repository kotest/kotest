package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.log

/**
 * Notifies the test listener that the engine is ready to execute tests, and the final [TestSuite]
 * is ready to be used, and that all tests have completed, with any unexpected errors.
 */
object TestEngineListenerStartedFinishedInterceptor : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      listener.engineStarted(suite.specs.map { it.kclass })
      val result = execute(suite, listener)

      result.errors.forEach {
         log(it) { "TestEngineListenerStartedFinishedInterceptor: Error during test engine run" }
         it.printStackTrace()
      }

      listener.engineFinished(result.errors)
      return result
   }
}
