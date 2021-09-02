package io.kotest.engine.interceptors

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.core.listeners.Listener
import io.kotest.engine.EngineResult
import io.kotest.engine.LifecycleEventManager
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.log

class ProjectLifecycleEngineInterceptor(private val listeners: List<Listener>) : EngineInterceptor {

   private val lifecycle = LifecycleEventManager()

   override fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      log { "ProjectLifecycleEngineInterceptor: Invoking BeforeProjectListeners" }
      lifecycle.beforeProject(listeners.filterIsInstance<BeforeProjectListener>())

      val result = execute(suite, listener)

      // todo capture errors and add to result
      log { "ProjectLifecycleEngineInterceptor: Invoking AfterProjectListeners" }
      lifecycle.afterProject(listeners.filterIsInstance<AfterProjectListener>())

      return result
   }
}
