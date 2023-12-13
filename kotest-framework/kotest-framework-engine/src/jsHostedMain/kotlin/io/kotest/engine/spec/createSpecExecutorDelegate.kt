package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.PromiseTestCaseExecutionListener
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.describe
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.jasmineIt
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.interceptors.testNameEscape
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import io.kotest.engine.test.scopes.TerminalTestScope
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.engine.jasmineXit
import io.kotest.mpp.bestName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.coroutineContext

internal actual fun createSpecExecutorDelegate(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   context: EngineContext,
): SpecExecutorDelegate = JavascriptSpecExecutorDelegate(context)

/**
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
@ExperimentalKotest
internal class JavascriptSpecExecutorDelegate(private val context: EngineContext) : SpecExecutorDelegate {

   private val formatter = getFallbackDisplayNameFormatter(
      context.configuration.registry,
      context.configuration,
   )

   private val materializer = Materializer(context.configuration)

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      val cc = coroutineContext
      // we use the spec itself as an outer/parent test.
      describe(testNameEscape(spec::class.bestName())) {
         materializer.materialize(spec).forEach { root ->

            val testDisplayName = testNameEscape(formatter.format(root))

            // todo find a way to delegate this to the test case executor
            val enabled = root.isEnabledInternal(context.configuration)
            if (enabled.isEnabled) {
               // we have to always invoke `it` to start the test so that the js test framework doesn't exit
               // before we invoke our callback. This also gives us the handle to the done callback.
               jasmineIt(
                  testDisplayName,
                  fn = { done ->
                     // ideally we'd just launch the executor and have the listener set up the test,
                     // but we can't launch a promise inside the describe and have it resolve the "it"
                     // this means we must duplicate the isEnabled check outside the executor
                     GlobalScope.promise {
                        TestCaseExecutor(
                           PromiseTestCaseExecutionListener(done),
                           NoopCoroutineDispatcherFactory,
                           context
                        ).execute(root, TerminalTestScope(root, cc))
                     }
                     // we don't want to return the promise as the js frameworks will use that for test resolution
                     // instead of the done callback, and we prefer the callback as it allows for custom timeouts
                  },
                  // some frameworks default to a 2000 timeout,
                  // here we set to a high number and use the timeout support kotest provides via coroutines
                  timeout = Int.MAX_VALUE
               )
            } else {
               jasmineXit(testDisplayName) {}
            }
         }
      }
      return emptyMap()
   }
}
