package io.kotest.engine.spec.interceptor.instance

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExtensionsExecutor
import io.kotest.engine.spec.interceptor.SpecInterceptor

/**
 * A [SpecInterceptor] that executes all [SpecExtension]s, which allows for interception.
 */
internal class SpecExtensionInterceptor(registry: ExtensionRegistry) : SpecInterceptor {

   val extensions = SpecExtensionsExecutor(registry)

   override suspend fun intercept(
      spec: Spec,
      fn: suspend (Spec) -> Result<Map<TestCase, TestResult>>
   ): Result<Map<TestCase, TestResult>> {
      return extensions.intercept(spec) { fn(spec) } ?: Result.success(emptyMap())
   }
}

