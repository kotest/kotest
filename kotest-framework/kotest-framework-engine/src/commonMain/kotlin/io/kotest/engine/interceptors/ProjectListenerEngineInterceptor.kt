package io.kotest.engine.interceptors

import io.kotest.engine.EngineResult

/**
 * An [EngineInterceptor] that invokes any [io.kotest.core.listeners.BeforeProjectListener]
 * and [io.kotest.core.listeners.AfterProjectListener] instances.
 */
internal object ProjectListenerEngineInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: NextEngineInterceptor
   ): EngineResult {

      val beforeErrors = context.projectExtensions().beforeProject()

      // if we have errors in the before project listeners, we'll not execute tests,
      // but instead immediately return those errors.
      if (beforeErrors.isNotEmpty()) return EngineResult(beforeErrors)

      val result = execute(context)

      val afterErrors = context.projectExtensions().afterProject()
      return result.copy(errors = result.errors + afterErrors)
   }
}
