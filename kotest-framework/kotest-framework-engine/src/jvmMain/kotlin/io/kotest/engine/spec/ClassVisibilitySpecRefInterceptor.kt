package io.kotest.engine.spec

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import kotlin.reflect.KVisibility

/**
 * A [SpecRefInterceptor] which will ignore private specs when the configuration values are set.
 */
internal class ClassVisibilitySpecRefInterceptor(private val context: EngineContext) : SpecRefInterceptor {

   override suspend fun intercept(ref: SpecRef, next: NextSpecRefInterceptor): Result<Map<TestCase, TestResult>> {
      return when {
         ref.kclass.visibility == KVisibility.PRIVATE && ignorePrivate() -> Result.success(emptyMap())
         else -> next.invoke(ref)
      }
   }

   /**
    * We ignore private classes if the configuration flag or system property to ignore is set to true.
    */
   private fun ignorePrivate(): Boolean {
      return context.configuration.ignorePrivateClasses ||
         System.getProperty(KotestEngineProperties.ignorePrivateClasses) == "true"
   }
}
