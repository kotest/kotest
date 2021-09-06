package io.kotest.engine.spec

import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.CallingThreadTestContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseListenerToTestEngineListenerAdapter
import io.kotest.mpp.log
import kotlin.coroutines.coroutineContext

actual fun createSpecExecutorDelegate(listener: TestEngineListener): SpecExecutorDelegate =
   DefaultSpecExecutorDelegate(listener)

/**
 * A [SpecExecutorDelegate] that executes tests sequentially, using the calling thread
 * as the execution context for timeouts.
 */
class DefaultSpecExecutorDelegate(private val listener: TestEngineListener) : SpecExecutorDelegate {

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      log { "DefaultSpecExecutorDelegate: Executing spec $spec" }
      spec.materializeAndOrderRootTests()
         .forEach { (testCase, _) ->
            log { "DefaultSpecExecutorDelegate: Executing testCase $testCase" }
            val context = CallingThreadTestContext(
               testCase,
               coroutineContext,
               configuration.duplicateTestNameMode,
               listener,
               CallingThreadExecutionContext
            )
            TestCaseExecutor(
               TestCaseListenerToTestEngineListenerAdapter(listener),
               CallingThreadExecutionContext
            ).execute(testCase, context)
         }
      return emptyMap()
   }
}
