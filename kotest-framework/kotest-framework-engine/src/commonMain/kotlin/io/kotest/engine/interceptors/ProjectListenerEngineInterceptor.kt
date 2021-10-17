package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.engine.EngineResult
import io.kotest.engine.events.AfterProjectListenerException
import io.kotest.engine.events.BeforeProjectListenerException
import io.kotest.mpp.log

/**
 * An [EngineInterceptor] that invokes the before and after project listeners.
 */
@KotestInternal
internal class ProjectListenerEngineInterceptor(private val extensions: List<Extension>) : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {

      val before = extensions.filterIsInstance<BeforeProjectListener>()
      log { "ProjectListenerEngineInterceptor: Invoking ${before.size} BeforeProjectListeners" }
      val beforeErrors = ProjectListenerEvents.beforeProject(before)

      // if we have errors in the before project listeners, we'll not execute tests,
      // but instead immediately return those errors.
      if (beforeErrors.isNotEmpty()) return EngineResult(beforeErrors)

      val result = execute(context)

      val after = extensions.filterIsInstance<AfterProjectListener>()
      log { "ProjectListenerEngineInterceptor: Invoking ${after.size} AfterProjectListeners" }
      val afterErrors = ProjectListenerEvents.afterProject(after)

      return result.copy(errors = result.errors + afterErrors)
   }
}

object ProjectListenerEvents {

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
