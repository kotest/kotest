package io.kotest.engine.interceptors

import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.project.ProjectContext
import io.kotest.engine.EngineResult

/**
 * An [EngineInterceptor] that invokes any [ProjectExtension]s before the engine begins execution.
 *
 * Project extensions can adapt the [ProjectContext] which is the public API version of
 * the [EngineContext]. Any changes to the project context are reflected downstream in
 * the engine context passed to the execute function.
 */
internal object ProjectExtensionEngineInterceptor : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {

      var result: EngineResult = EngineResult.empty
      val initial: suspend (ProjectContext) -> Unit = { result = execute(it.toEngineContext(context, context.platform, context.state)) }
      val chain = context
         .configuration
         .registry
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
