package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.project.ProjectContext
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * A [SpecInterceptor] that adds the [EngineContext] to the coroutine context.
 */
internal class EngineContextInterceptor(
   private val context: EngineContext,
) : SpecInterceptor {
   override suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      return withContext(EngineContextElement(context)) {
         next.invoke(spec)
      }
   }
}

internal val CoroutineContext.engineContext: ProjectContext
   get() = get(EngineContextElement)?.projectContext
      ?: error("engineContext is not injected into this CoroutineContext")
