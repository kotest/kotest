package io.kotest.engine.spec.interceptors

import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.SpecRefInterceptor
import kotlin.reflect.KVisibility

/**
 * A [SpecRefInterceptor] which will ignore private specs unless the include private flag
 * is true in project config.
 */
class ClassVisibilitySpecRefInterceptor(private val context: EngineContext) : SpecRefInterceptor {

   override suspend fun intercept(
      ref: SpecRef,
      fn: suspend (SpecRef) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return when {
         ref.kclass.visibility == KVisibility.PRIVATE && !allowPrivate() -> Result.success(emptyMap())
         else -> fn(ref)
      }
   }

   /**
    * We allow private classes if the JVM system property is set, if the configuration flag is set, or if
    * the test suite contains only a single class.
    */
   private fun allowPrivate(): Boolean {
      return context.configuration.includePrivateClasses ||
         System.getProperty(KotestEngineProperties.includePrivateClasses) == "true" ||
         context.suite.specs.size < 2
   }
}
