package io.kotest.engine.js

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.name
import io.kotest.core.test.TestCase
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.runPromise
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.TestResults
import io.kotest.engine.spec.execution.SpecExecutor
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import io.kotest.engine.test.scopes.TerminalTestScope
import io.kotest.engine.test.status.isEnabledInternal
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

internal class KotlinJsSpecExecutor(private val context: EngineContext) : SpecExecutor {

   private val formatter = getFallbackDisplayNameFormatter(
      context.projectConfigResolver,
      context.testConfigResolver,
   )

   private val pipeline = SpecInterceptorPipeline(context)
   private val materializer = Materializer(context.specConfigResolver)
   private val results = TestResults()

   override suspend fun execute(ref: SpecRef, seed: Spec): Result<Map<TestCase, TestResult>> {
      // we switch to a new coroutine for each spec instance, which in this case is always the same provided instance
      return withContext(CoroutineName("spec-scope-" + seed.hashCode())) {
         val specContext = SpecContext.create()
         pipeline.execute(seed, specContext) { spec ->
            // This implementation supports a two-level test hierarchy with the spec itself as the test `suite`,
            // which declares a single level of `test`s.
            kotlinJsTestFramework.suite(testNameEscape(ref.name()), ignored = false) {
               materializer.materialize(seed).forEach { testCase ->
                  executeTest(testCase, specContext)
               }
            }
            Result.success(results.toMap())
         }.map { results.toMap() } // we only use the test results if the pipeline completes successfully
      }
   }

   /**
    * Executes the given [TestCase] using a [io.kotest.engine.test.TestCaseExecutor].
    * Logs the results in the results tree.
    *
    * @return the result of this single test.
    */
   private fun executeTest(testCase: TestCase, specContext: SpecContext) {
      val ignored = testCase.isEnabledInternal(
         context.projectConfigResolver,
         context.testConfigResolver,
      ).isDisabled

      kotlinJsTestFramework.test(
         testNameEscape(formatter.format(testCase)),
         ignored = ignored,
      ) {
         // We rely on JS Promise to interact with the JS test framework. We cannot use callbacks here
         // because we pass our function through the Kotlin/JS test infra via its interface `FrameworkAdapter`,
         // which does not support callbacks. It does, however, allow the test function to return a Promise-like
         // type for asynchronous invocations. See `KotlinJsTestFramework` for details.
         @OptIn(DelicateCoroutinesApi::class)
         runPromise {
            val cc = coroutineContext
            val testExecutor = TestCaseExecutor(context)
            //  we use the `TerminalTestScope` because we don't support nested test suites on javascript
            testExecutor.execute(testCase, TerminalTestScope(testCase, cc), specContext)
               .errorOrNull?.let { throw it }
         }
      }

      results.completed(testCase, TestResultBuilder.builder().build())
   }
}
