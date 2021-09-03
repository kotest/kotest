package io.kotest.engine.interceptors

import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.events.AfterProjectListenerException
import io.kotest.engine.events.BeforeProjectListenerException
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.log

class ProjectListenerEngineInterceptor(private val extensions: List<Extension>) : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult,
   ): EngineResult {

      val before = extensions.filterIsInstance<BeforeProjectListener>()
      log { "ProjectListenerEngineInterceptor: Invoking ${before.size} BeforeProjectListeners" }
      val beforeErrors = ProjectLifecycleManager.beforeProject(before)

      // if we have errors in the before project listeners, we'll not even execute tests, but
      // instead immediately exit.
      if (beforeErrors.isNotEmpty()) return EngineResult(beforeErrors)

      val result = execute(suite, listener)

      // todo capture errors and add to result
      val after = extensions.filterIsInstance<AfterProjectListener>()
      log { "ProjectListenerEngineInterceptor: Invoking ${after.size} AfterProjectListeners" }
      val afterErrors = ProjectLifecycleManager.afterProject(after)

      return result.copy(errors = result.errors + afterErrors)
   }
}

object ProjectLifecycleManager {

   suspend fun beforeProject(before: List<BeforeProjectListener>): List<BeforeProjectListenerException> {
      return before.mapNotNull {
         try {
            it.beforeProject()
            null
         } catch (t: Throwable) {
            BeforeProjectListenerException(it.name, t)
         }
      }
   }

   suspend fun afterProject(after: List<AfterProjectListener>): List<AfterProjectListenerException> {
      return after.mapNotNull {
         try {
            it.afterProject()
            null
         } catch (t: Throwable) {
            AfterProjectListenerException(it.name, t)
         }
      }
   }
}
