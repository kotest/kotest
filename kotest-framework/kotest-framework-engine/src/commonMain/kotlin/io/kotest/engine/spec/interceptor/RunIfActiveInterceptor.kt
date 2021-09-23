package io.kotest.engine.spec.interceptor

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecInactiveExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.status.isEnabled
import io.kotest.mpp.log

/**
 * The root tests on a [Spec] are retrieved, and if none are enabled, then no
 * execution step takes place, the test is marked as ignored, and all [SpecInactiveExtension]s
 * are invoked.
 *
 * Otherwise, if at least one active, the downstream function is invoked.
 */
internal class RunIfActiveInterceptor(private val listener: TestEngineListener) : SpecExecutionInterceptor {

   override suspend fun intercept(
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->

      val roots = spec.materializeAndOrderRootTests()
      val enabled = roots.associate { it.testCase to it.testCase.isEnabled() }
      val active = enabled.any { it.value.isEnabled }

      log { "RunIfActiveInterceptor: active=$active from ${roots.size} root tests [$spec]" }

      if (active) {
         fn(spec)
      } else {
         val results = enabled.mapValues { TestResult.ignored(it.value.reason) }
         listener.specInactive(spec::class, results)
         SpecExtensions(configuration).inactiveSpec(spec, results)
         results
      }
   }
}
