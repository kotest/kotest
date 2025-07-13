package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecInterceptor

/**
 * A [SpecInterceptor] that executes all [SpecExtension]s, which allows for interception.
 */
internal class SpecExtensionInterceptor(private val specExtensions: SpecExtensions) : SpecInterceptor {

   override suspend fun intercept(
      spec: Spec,
      next: NextSpecInterceptor
   ): Result<Map<TestCase, TestResult>> {
      return specExtensions.intercept(spec) { next.invoke(spec) } ?: Result.success(emptyMap())
   }
}

