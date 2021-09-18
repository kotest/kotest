package io.kotest.engine.spec

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.contexts.CallingThreadTestContext
import io.kotest.engine.test.contexts.DuplicateNameHandlingTestContext
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.mpp.log
import kotlin.coroutines.coroutineContext

internal actual fun createSpecExecutorDelegate(
   listener: TestEngineListener,
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
): SpecExecutorDelegate =
   DefaultSpecExecutorDelegate(listener, defaultCoroutineDispatcherFactory)

/**
 * A [SpecExecutorDelegate] that executes tests sequentially, using the calling thread
 * as the execution context for timeouts.
 */
internal class DefaultSpecExecutorDelegate(
   private val listener: TestEngineListener,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory
) : SpecExecutorDelegate {

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      log { "DefaultSpecExecutorDelegate: Executing spec $spec" }
      spec.materializeAndOrderRootTests()
         .forEach { (testCase, _) ->
            log { "DefaultSpecExecutorDelegate: Executing testCase $testCase" }
            val context = DuplicateNameHandlingTestContext(
               configuration.duplicateTestNameMode,
               CallingThreadTestContext(
                  testCase,
                  coroutineContext,
                  configuration.duplicateTestNameMode,
                  listener,
                  coroutineDispatcherFactory
               )
            )
            TestCaseExecutor(
               TestCaseExecutionListenerToTestEngineListenerAdapter(listener),
               coroutineDispatcherFactory,
            ).execute(testCase, context)
         }
      return emptyMap()
   }
}
