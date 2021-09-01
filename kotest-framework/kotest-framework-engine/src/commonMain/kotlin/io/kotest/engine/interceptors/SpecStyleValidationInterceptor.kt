package io.kotest.engine.interceptors

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.bestName

/**
 * Validates that a [Spec] style is compatible for platforms that do not support nested tests.
 */
internal object SpecStyleValidationInterceptor : EngineInterceptor {

   override fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      fun isValid(spec: Spec): Boolean = spec is FunSpec || spec is StringSpec || spec is ShouldSpec
      val (valid, invalid) = suite.specs.partition { isValid(it) }

      if (invalid.isNotEmpty()) {
         println("WARN: kotest-js and kotest-native only support top level tests due to underlying platform limitations. The following specs are ignored:")
         invalid.forEach {
            println("WARN: " + it::class.bestName())
         }
      }

      return execute(suite.copy(specs = valid), listener)
   }
}
