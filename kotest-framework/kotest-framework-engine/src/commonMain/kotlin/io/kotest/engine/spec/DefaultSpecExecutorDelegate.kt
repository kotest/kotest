package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.log
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.scopes.DuplicateNameHandlingTestScope
import io.kotest.engine.test.scopes.InOrderTestScope
import kotlin.coroutines.coroutineContext

/**
 * A [SpecExecutorDelegate] that executes tests sequentially, using the calling thread
 * as the execution context for timeouts.
 */
@Suppress("DEPRECATION")
@ExperimentalKotest
@Deprecated("Will be replaced by subsuming delegates into the spec executor directly")
internal class DefaultSpecExecutorDelegate(
   private val engineContext: EngineContext,
) : SpecExecutorDelegate {

   private val materializer = Materializer(engineContext.specConfigResolver)

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      log { "DefaultSpecExecutorDelegate: Executing spec $spec" }
      val specContext = SpecContext.create()
      materializer.materialize(spec)
         .forEach { testCase ->
            log { "DefaultSpecExecutorDelegate: Executing testCase $testCase" }
            val scope = DuplicateNameHandlingTestScope(
               engineContext.specConfigResolver.duplicateTestNameMode(spec),
               InOrderTestScope(
                  specContext,
                  testCase,
                  coroutineContext,
                  engineContext.specConfigResolver.duplicateTestNameMode(spec),
                  engineContext,
               )
            )
            TestCaseExecutor(
               TestCaseExecutionListenerToTestEngineListenerAdapter(engineContext.listener),
               engineContext,
            ).execute(testCase, scope, specContext)
         }
      return emptyMap()
   }
}
