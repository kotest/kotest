@file:Suppress("DEPRECATION")

package io.kotest.engine.spec

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext

internal actual fun createSpecExecutorDelegate(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   context: EngineContext,
): SpecExecutorDelegate = JvmSpecExecutorDelegate(defaultCoroutineDispatcherFactory, context)

@Deprecated("This will be merged into the SpecExecutor itself in a future pr")
internal class JvmSpecExecutorDelegate(
   private val dispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
) : SpecExecutorDelegate {

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      val executor = SpecExecutor2(context)
      return executor.execute(spec).getOrThrow()
   }
}
