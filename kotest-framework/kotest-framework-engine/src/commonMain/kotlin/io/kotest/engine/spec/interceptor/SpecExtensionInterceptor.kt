package io.kotest.engine.spec.interceptor

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExtensions
import io.kotest.mpp.log

/**
 * A [SpecInterceptor] that executes all [SpecExtension]s.
 */
internal class SpecExtensionInterceptor(
   private val registry: ExtensionRegistry
) : SpecInterceptor {

   override suspend fun intercept(
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->

      val extensions = SpecExtensions(registry)
      log { "SpecInterceptExtensionsInterceptor: Intercepting spec" }

      extensions.intercept(spec) { fn(spec) }
   }
}
