package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.project.ProjectContext
import io.kotest.core.project.ProjectContextElement
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.SpecInterceptor
import kotlinx.coroutines.withContext
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

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

internal data class EngineContextElement(val context: EngineContext) :
   AbstractCoroutineContextElement(ProjectContextElement) {
   companion object Key : CoroutineContext.Key<ProjectContextElement>
}
