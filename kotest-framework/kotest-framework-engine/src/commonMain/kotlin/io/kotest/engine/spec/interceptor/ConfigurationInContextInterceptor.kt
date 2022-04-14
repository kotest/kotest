package io.kotest.engine.spec.interceptor

import io.kotest.core.config.ConfigurationContextElement
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.withContext

/**
 * A [SpecInterceptor] that injects the [ProjectConfiguration] into the coroutine context
 * so it can be extracted in specs and tests.
 */
class ConfigurationInContextInterceptor(
   private val projectConfiguration: ProjectConfiguration
) : SpecInterceptor {

   override suspend fun intercept(
      spec: SpecContainer,
      fn: suspend (SpecContainer) -> Result<Pair<SpecContainer, Map<TestCase, TestResult>>>
   ): Result<Pair<SpecContainer, Map<TestCase, TestResult>>> {
      return withContext(ConfigurationContextElement(projectConfiguration)) {
         fn(spec)
      }
   }
}

