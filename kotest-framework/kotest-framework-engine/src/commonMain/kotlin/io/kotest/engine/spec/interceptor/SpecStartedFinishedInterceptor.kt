package io.kotest.engine.spec.interceptor

import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions

internal class SpecStartedFinishedInterceptor(private val listener: TestEngineListener) : SpecExecutionInterceptor {

   private val extensions = SpecExtensions(configuration)

   override suspend fun intercept(
      fn: suspend (Spec) -> Map<TestCase, TestResult>
   ): suspend (Spec) -> Map<TestCase, TestResult> = { spec ->
      listener.specStarted(spec::class)
      val results = fn(spec)
      listener.specFinished(spec::class, results)
      extensions.finishSpec(spec::class, results)
      results
   }
}
