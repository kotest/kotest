package io.kotest.engine.interceptors

import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.engine.EngineResult
import io.kotest.engine.LifecycleEventManager
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.log

class ProjectListenerEngineInterceptor(private val extensions: List<Extension>) : EngineInterceptor {

   private val lifecycle = LifecycleEventManager()

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      val before = extensions.filterIsInstance<BeforeProjectListener>()
      log { "ProjectListenerEngineInterceptor: Invoking ${before.size} BeforeProjectListeners" }
      lifecycle.beforeProject(before)

      // todo add logic here
      // if we have errors in the before project listeners, we'll not even execute tests, but
      // instead immediately exit.

      val result = execute(suite, listener)

      // todo capture errors and add to result
      val after = extensions.filterIsInstance<AfterProjectListener>()
      log { "ProjectListenerEngineInterceptor: Invoking ${after.size} AfterProjectListeners" }
      lifecycle.afterProject(after)

      return result
   }
}
