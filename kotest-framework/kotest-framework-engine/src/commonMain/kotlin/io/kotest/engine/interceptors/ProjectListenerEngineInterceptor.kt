package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult
import io.kotest.engine.extensions.ProjectExtensions

/**
 * An [EngineInterceptor] that invokes any before and after project listeners.
 */
internal object ProjectListenerEngineInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor
   ): EngineResult {

      val extensions = ProjectExtensions(context.projectConfigResolver.extensions())
      val beforeErrors = extensions.beforeProject()

      // if we have errors in the before project listeners, we'll not execute tests,
      // but instead immediately return those errors.
      if (beforeErrors.isNotEmpty()) return EngineResult(beforeErrors)

      val result = execute(context)

      val afterErrors = extensions.afterProject()
      return result.copy(errors = result.errors + afterErrors)
   }
}
