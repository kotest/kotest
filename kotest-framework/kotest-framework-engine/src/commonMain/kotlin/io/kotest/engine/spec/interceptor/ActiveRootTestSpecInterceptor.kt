package io.kotest.engine.spec.interceptor

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.status.isEnabled

class ActiveRootTestSpecInterceptor(val configuration: ProjectConfiguration) : SpecInterceptor {
   private val materializer = Materializer(configuration)

   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      val isEnabled = materializer.materialize(spec).any { it.isEnabled(configuration).isEnabled }
      return if (isEnabled) fn(spec) else Result.success(emptyMap())
   }
}
