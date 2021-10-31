package io.kotest.engine.spec.interceptor

import io.kotest.core.ProjectContext
import io.kotest.core.ProjectContextElement
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
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->
      withContext(ProjectContextElement(context)) {
         fn(spec)
      }
   }
}
