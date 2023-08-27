package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.config.ConfigurationContextElement
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.SpecInterceptor
import kotlinx.coroutines.withContext

/**
 * A [SpecInterceptor] that injects the [ProjectConfiguration] into the coroutine context
 * so it can be extracted in specs and tests.
 */
internal class ConfigurationInContextSpecInterceptor(
   private val projectConfiguration: ProjectConfiguration
) : SpecInterceptor {

   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return withContext(ConfigurationContextElement(projectConfiguration)) {
         fn(spec)
      }
   }
}

