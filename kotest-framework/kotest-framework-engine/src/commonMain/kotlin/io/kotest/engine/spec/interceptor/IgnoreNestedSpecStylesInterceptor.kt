package io.kotest.engine.spec.interceptor

import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.mpp.bestName

/**
 * Filters [Spec]'s that are not compatible on platforms that disallow nested tests.
 */
internal class IgnoreNestedSpecStylesInterceptor(private val listener: TestEngineListener) : SpecExecutionInterceptor {

   override suspend fun intercept(
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->

      fun isValid(spec: Spec): Boolean = spec is FunSpec || spec is ShouldSpec || spec is StringSpec

      if (isValid(spec)) {
         fn(spec)
      } else {
         println("WARN: kotest-js only supports top level tests due to underlying platform limitations. '${spec::class.bestName()}' has been marked as ignored")
         listener.specInactive(spec::class)
         SpecExtensions(configuration).inactiveSpec(spec, emptyMap())
         emptyMap()
      }
   }
}
