package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.ProjectContext
import io.kotest.core.extensions.ProjectExtension
import io.kotest.engine.EngineResult

/**
 * An [EngineInterceptor] that invokes any [ProjectExtension]s allowing them the chance
 * to change the [EngineContext] before the engine begins execution.
 */
@KotestInternal
internal object ProjectExtensionEngineInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {

      var result: EngineResult = EngineResult.empty
      val initial: suspend (ProjectContext) -> Unit = { result = execute(it.toEngineContext(context)) }
      val chain = context
         .configuration
         .extensions
         .all()
         .filterIsInstance<ProjectExtension>()
         .foldRight(initial) { extension, acc: suspend (ProjectContext) -> Unit ->
            {
               extension.interceptProject(context.toProjectContext()) { acc(it) }
            }
         }
      return try {
         chain.invoke(context.toProjectContext())
         result
      } catch (t: Throwable) {
         result.addError(t)
      }
   }
}
