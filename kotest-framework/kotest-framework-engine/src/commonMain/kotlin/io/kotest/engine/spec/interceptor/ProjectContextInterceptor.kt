package io.kotest.engine.spec.interceptor

import io.kotest.core.project.ProjectContext
import io.kotest.core.project.ProjectContextElement
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.withContext

/**
 * A [SpecInterceptor] that adds the [ProjectContext] to the coroutine context.
 */
internal class ProjectContextInterceptor(
   private val context: ProjectContext,
) : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return withContext(ProjectContextElement(context)) {
         fn(spec)
      }
   }
}
