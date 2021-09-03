package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.log

/**
 * Notifies the test listener that the engine is fully booted and ready to rock.
 * Comes last in the engine interceptor chain.
 */
object TestEngineListenerStartedFinishedInterceptor : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      listener.engineStarted(suite.classes)
      val result = execute(suite, listener)

      result.errors.forEach {
         log(it) { "TestEngineListenerStartedFinishedInterceptor: Error during test engine run" }
         it.printStackTrace()
      }

      listener.engineFinished(result.errors)
      return result
   }
}
