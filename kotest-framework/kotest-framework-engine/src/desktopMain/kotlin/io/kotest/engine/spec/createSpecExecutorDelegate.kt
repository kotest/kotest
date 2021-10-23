package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.contexts.DuplicateNameHandlingTestContext
import io.kotest.engine.test.contexts.InOrderTestContext
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.mpp.log
import kotlin.coroutines.coroutineContext

@ExperimentalKotest
internal actual fun createSpecExecutorDelegate(
   listener: TestEngineListener,
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   configuration: Configuration,
): SpecExecutorDelegate =
   DefaultSpecExecutorDelegate(listener, defaultCoroutineDispatcherFactory, configuration)

/**
 * A [SpecExecutorDelegate] that executes tests sequentially, using the calling thread
 * as the execution context for timeouts.
 */
@ExperimentalKotest
internal class DefaultSpecExecutorDelegate(
   private val listener: TestEngineListener,
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: Configuration
) : SpecExecutorDelegate {

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      log { "DefaultSpecExecutorDelegate: Executing spec $spec" }
      spec.materializeAndOrderRootTests(configuration.testCaseOrder)
         .forEach { (testCase, _) ->
            log { "DefaultSpecExecutorDelegate: Executing testCase $testCase" }
            val context = DuplicateNameHandlingTestContext(
               configuration.duplicateTestNameMode,
               InOrderTestContext(
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
