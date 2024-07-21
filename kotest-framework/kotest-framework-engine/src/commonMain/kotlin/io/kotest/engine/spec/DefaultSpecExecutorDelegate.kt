package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.scopes.DuplicateNameHandlingTestScope
import io.kotest.engine.test.scopes.InOrderTestScope
import io.kotest.core.log
import kotlin.coroutines.coroutineContext

/**
 * A [SpecExecutorDelegate] that executes tests sequentially, using the calling thread
 * as the execution context for timeouts.
 */
@ExperimentalKotest
internal class DefaultSpecExecutorDelegate(
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext
) : SpecExecutorDelegate {

   private val materializer = Materializer(context.configuration)

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      log { "DefaultSpecExecutorDelegate: Executing spec $spec" }
      materializer.materialize(spec)
         .forEach { testCase ->
            log { "DefaultSpecExecutorDelegate: Executing testCase $testCase" }
            val scope = DuplicateNameHandlingTestScope(
               context.configuration.duplicateTestNameMode,
               InOrderTestScope(
                  testCase,
                  coroutineContext,
                  context.configuration.duplicateTestNameMode,
                  coroutineDispatcherFactory,
                  context
               )
            )
            TestCaseExecutor(
               TestCaseExecutionListenerToTestEngineListenerAdapter(context.listener),
               coroutineDispatcherFactory,
               context,
            ).execute(testCase, scope)
         }
      return emptyMap()
   }
}
