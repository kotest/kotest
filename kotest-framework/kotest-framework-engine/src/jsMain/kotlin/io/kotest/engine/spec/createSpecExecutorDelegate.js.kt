package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext

@Suppress("DEPRECATION")
internal actual fun createSpecExecutorDelegate(
   context: EngineContext
): SpecExecutorDelegate = JsSpecExecutorDelegate(context)

@Deprecated("This will be merged into the SpecExecutor itself in a future pr")
internal class JsSpecExecutorDelegate(
   private val context: EngineContext,
) : SpecExecutorDelegate {

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      val executor = SpecExecutor2(context)
      return executor.execute(spec).getOrThrow()
   }
}
