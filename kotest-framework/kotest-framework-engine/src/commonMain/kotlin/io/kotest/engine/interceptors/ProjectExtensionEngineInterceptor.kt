package io.kotest.engine.interceptors

import io.kotest.common.KotestInternal
import io.kotest.core.extensions.ProjectExtension
import io.kotest.engine.EngineResult

@OptIn(KotestInternal::class)
internal class ProjectExtensionEngineInterceptor(private val extensions: List<ProjectExtension>) : EngineInterceptor {

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {

      var result: EngineResult = EngineResult.empty
      val initial: suspend () -> Unit = { result = execute(context) }
      val chain = extensions.foldRight(initial) { extension, acc: suspend () -> Unit ->
         {
            extension.interceptProject { acc() }
         }
      }
      return try {
         chain.invoke()
         result
      } catch (t: Throwable) {
         result.addError(t)
      }
   }
}
