package io.kotest.engine.extensions

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.EngineResult
import io.kotest.engine.TestSuite
import io.kotest.engine.listener.TestEngineListener
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Validates that a [Spec] style is compatible for platforms that do not support nested tests.
 */
object SpecStyleValidationExtension : EngineExtension {

   override fun intercept(
      suite: TestSuite,
      listener: TestEngineListener,
      execute: (TestSuite, TestEngineListener) -> EngineResult
   ): EngineResult {

      val invalid = suite.specs.filterNot {
         it is FunSpec || it is StringSpec || it is ShouldSpec
      }.map { it::class }

      return if (invalid.isEmpty()) {
         execute(suite, listener)
      } else {
         EngineResult(listOf(InvalidSpecStyleException(invalid)))
      }
   }
}

class InvalidSpecStyleException(val specs: List<KClass<out Spec>>) :
   Exception("Unsupported spec styles used - kotest-js and kotest-native only support top level tests due to underlying platform limitations. Caused by: ${specs.map { it.bestName() }}")
