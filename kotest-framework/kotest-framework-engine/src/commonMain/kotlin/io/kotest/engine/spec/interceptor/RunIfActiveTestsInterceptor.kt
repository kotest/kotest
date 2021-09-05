package io.kotest.engine.spec.interceptor

import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.status.isEnabled
import io.kotest.mpp.log

/**
 * The root tests on a [Spec] are retrieved, and if none are active, then no
 * execution step takes place. Otherwise, if at least one active, the downstream
 * function is invoked.
 */
class RunIfActiveTestsInterceptor(private val listener: TestEngineListener) : SpecExecutionInterceptor {

   override suspend fun intercept(fn: suspend (Spec) -> Unit): suspend (Spec) -> Unit = { spec ->

      log { "runTestsIfAtLeastOneActive [$spec]" }
      val roots = spec.materializeAndOrderRootTests()
      val active = roots.any { it.testCase.isEnabled().isEnabled }

      if (active) {
         fn(spec)
      } else {
         val extensions = SpecExtensions(configuration)
         val results = roots.associate { it.testCase to TestResult.ignored(it.testCase.isEnabled()) }
         extensions.specSkipped(spec, results)
         // extensions.specFinalize(spec)
      }
//      if (active) runTests(spec) else emptyMap<TestCase, TestResult>().success()
   }
}
