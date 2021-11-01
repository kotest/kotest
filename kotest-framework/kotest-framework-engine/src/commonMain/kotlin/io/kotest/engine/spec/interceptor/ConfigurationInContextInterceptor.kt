package io.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
import io.kotest.core.config.ConfigurationContextElement
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.withContext

/**
 * A [SpecInterceptor] that injects the [Configuration] into the coroutine context
 * so it can be extracted in specs and tests.
 */
class ConfigurationInContextInterceptor(private val conf: Configuration) : SpecInterceptor {

   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return withContext(ConfigurationContextElement(conf)) {
         fn(spec)
      }
   }
}

