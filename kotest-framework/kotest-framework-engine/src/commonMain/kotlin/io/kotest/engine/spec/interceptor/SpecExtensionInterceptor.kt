package io.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExtensions
import io.kotest.mpp.log

/**
 * A [SpecExecutionInterceptor] that executes all [SpecExtension]s.
 */
internal class SpecExtensionInterceptor(
   private val configuration: Configuration
) : SpecExecutionInterceptor {

   override suspend fun intercept(
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->

      val extensions = SpecExtensions(configuration.extensions())
      log { "SpecInterceptExtensionsInterceptor: Intercepting spec" }

      var results = emptyMap<TestCase, TestResult>()
      extensions.intercept(spec) { results = fn(spec) }
      results
   }
}
