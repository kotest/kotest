package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.ProjectConfigResolverContextElement
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor
import kotlinx.coroutines.withContext

/**
 * A [SpecInterceptor] that injects the [ProjectConfigResolver] into the coroutine context
 * so it can be extracted in specs and tests.
 */
internal class ProjectConfigResolverSpecInterceptor(
   private val projectConfigResolver: ProjectConfigResolver,
) : SpecInterceptor {

   override suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor,
   ): Result<Map<TestCase, TestResult>> {
      return withContext(ProjectConfigResolverContextElement(projectConfigResolver)) {
         next.invoke(spec)
      }
   }
}

