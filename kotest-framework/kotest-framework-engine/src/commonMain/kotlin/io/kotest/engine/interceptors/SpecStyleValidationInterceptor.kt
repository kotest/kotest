package io.kotest.engine.interceptors

import io.kotest.core.spec.Spec
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Validates that a [Spec] style is compatible for platforms that do not support nested tests.
 */
internal object SpecStyleValidationInterceptor : EngineInterceptor {

   override suspend fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: suspend (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      fun isValid(spec: KClass<out Spec>): Boolean =
         spec.simpleName == "FunSpec" || spec.simpleName == "ShouldSpec" || spec.simpleName == "StringSpec"

      val (valid, invalid) = suite.specs.partition { isValid(it.kclass) }

      if (invalid.isNotEmpty()) {
         println("WARN: kotest-js and kotest-native only support top level tests due to underlying platform limitations. The following specs are ignored:")
         invalid.forEach {
            println("WARN: " + it::class.bestName())
         }
      }

      return execute(suite.copy(specs = valid), listener)
   }
}
