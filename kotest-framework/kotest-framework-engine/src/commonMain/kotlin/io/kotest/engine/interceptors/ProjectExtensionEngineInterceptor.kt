package io.kotest.engine.interceptors

import io.kotest.core.extensions.ProjectExtension
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener

class ProjectExtensionEngineInterceptor(private val extensions: List<ProjectExtension>) : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      val initial: suspend () -> List<Throwable> = { execute(suite, listener).errors }
      val errors = extensions.foldRight(initial) { extension, acc -> { extension.aroundProject(acc) } }.invoke()
      return EngineResult(errors)
   }
}
