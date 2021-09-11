package io.kotest.engine.interceptors

import io.kotest.core.extensions.ProjectExtension
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener

internal class ProjectExtensionEngineInterceptor(private val extensions: List<ProjectExtension>) : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      val initial: suspend () -> EngineResult = { execute(suite, listener) }
      val chain = extensions.foldRight(initial) { extension, acc: suspend () -> EngineResult ->
         {
            val errors = extension.aroundProject { acc().errors }
            EngineResult(errors)
         }
      }
      return chain.invoke()
   }
}
