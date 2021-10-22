package io.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.mpp.bestName
import io.kotest.mpp.log

/**
 * Filters [Spec]'s that are not compatible on platforms that disallow nested tests.
 */
internal class IgnoreNestedSpecStylesInterceptor(
   private val listener: TestEngineListener,
   private val configuration: Configuration,
) : SpecExecutionInterceptor {

   override suspend fun intercept(
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->

      fun isValid(spec: Spec) = when (spec) {
         is FunSpec, is ExpectSpec, is FeatureSpec, is ShouldSpec, is StringSpec -> true
         else -> false
      }

      if (isValid(spec)) {
         fn(spec)
      } else {
         log { "IgnoreNestedSpecStylesInterceptor: Marking ${spec::class.bestName()} as inactive due to platform limitations" }
         println("WARN: kotest-js only supports top level tests due to underlying platform limitations. '${spec::class.bestName()}' has been marked as ignored")
         listener.specIgnored(spec::class)
         SpecExtensions(configuration.extensions()).inactiveSpec(spec, emptyMap())
         emptyMap()
      }
   }
}
