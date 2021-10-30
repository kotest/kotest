package io.kotest.engine.spec.interceptor

import io.kotest.core.config.ExtensionRegistry
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.mpp.log

internal class SpecStartedFinishedInterceptor(
   private val listener: TestEngineListener,
   private val registry: ExtensionRegistry,
) : SpecInterceptor {

   override suspend fun intercept(
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->

      log { "SpecStartedFinishedInterceptor: listener.specStarted $spec" }
      listener.specStarted(spec::class)

      val results = try {
         fn(spec)
      } catch (t: Throwable) {
         log { "SpecStartedFinishedInterceptor: Error downstream $t" }
         throw t
      }

//      log { "SpecStartedFinishedInterceptor: listener.specFinished $spec" }
//      listener.specFinished(spec::class, results)

//      SpecExtensions(registry).finishSpec(spec::class, results)
      results
   }
}
