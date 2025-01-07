package io.kotest.engine.spec

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import kotlin.reflect.KVisibility

/**
 * A [SpecRefInterceptor] which will ignore private specs when the configuration values are set.
 */
internal class ClassVisibilitySpecRefInterceptor(
   private val projectConfigResolver: ProjectConfigResolver,
) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return when {
         ref.kclass.visibility == KVisibility.PRIVATE &&
            projectConfigResolver.ignorePrivateClasses() -> Result.success(emptyMap())

         else -> next.invoke(ref)
      }
   }
}
