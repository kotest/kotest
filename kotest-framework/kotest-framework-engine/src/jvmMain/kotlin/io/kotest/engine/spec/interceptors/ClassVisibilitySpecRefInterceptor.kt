package io.kotest.engine.spec.interceptors

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import kotlin.reflect.KVisibility

/**
 * A [SpecRefInterceptor] which will ignore private specs unless the include private flag
 * is true in project config.
 */
class ClassVisibilitySpecRefInterceptor(private val config: ProjectConfiguration) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return when {
         ref.kclass.visibility == KVisibility.PRIVATE && !config.includePrivateClasses -> Result.success(emptyMap())
         else -> fn(ref)
      }
   }
}
