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
import io.kotest.engine.spec.interceptor.SpecContext
import kotlin.coroutines.coroutineContext

/**
 * A [SpecExecutorDelegate] that executes tests sequentially, using the calling thread
 * as the execution context for timeouts.
 */
@ExperimentalKotest
@Deprecated("Will be replaced by subsuming delegates into the spec executor directly")
internal class DefaultSpecExecutorDelegate(
   private val coroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val engineContext: EngineContext
) : SpecExecutorDelegate {

   private val materializer = Materializer(engineContext.configuration)

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      log { "DefaultSpecExecutorDelegate: Executing spec $spec" }
      val specContext = SpecContext.create()
      materializer.roots(spec)
         .forEach { testCase ->
            log { "DefaultSpecExecutorDelegate: Executing testCase $testCase" }
            val scope = DuplicateNameHandlingTestScope(
               engineContext.configuration.duplicateTestNameMode,
               InOrderTestScope(
                  specContext,
                  testCase,
                  coroutineContext,
                  engineContext.configuration.duplicateTestNameMode,
                  coroutineDispatcherFactory,
                  engineContext,
               )
            )
            TestCaseExecutor(
               TestCaseExecutionListenerToTestEngineListenerAdapter(engineContext.listener),
               coroutineDispatcherFactory,
               engineContext,
            ).execute(testCase, scope, specContext)
         }
      return emptyMap()
   }
}
