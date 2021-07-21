@file:Suppress("unused")

package io.kotest.engine.execution

import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.core.spec.Spec
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.test.resolvedTimeout
import io.kotest.mpp.bestName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext

/**
 * Entry point for JS tests.
 * This method is invoked by the compiler plugin.
 *
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
@DelicateCoroutinesApi
fun executeSpec(spec: Spec) {
   // we use the spec itself is an outer test like in JVM tests.
   describe(spec::class.bestName()) {
      spec.materializeAndOrderRootTests()
         .filter { it.testCase.isEnabledInternal().isEnabled }
         .forEach { root ->
            // we have to always start the test so that the framework doesn't exit before we return
            // also it gives us a handle to the done callback
            it(root.testCase.description.name.displayName) { done ->

               // done is the JS promise
               // some frameworks default to a 2000 timeout,
               // we can change this to the kotest test setting
               done.timeout(root.testCase.resolvedTimeout())

               val listener = object : TestCaseExecutionListener {
                  override fun testStarted(testCase: TestCase) {}
                  override fun testIgnored(testCase: TestCase) {}
                  override fun testFinished(testCase: TestCase, result: TestResult) {
                     done(result.error)
                  }
               }

               GlobalScope.promise {
                  val context = object : TestContext {
                     override val testCase: TestCase = root.testCase
                     override val coroutineContext: CoroutineContext = this@promise.coroutineContext
                     override suspend fun registerTestCase(nested: NestedTest) {
                        throw IllegalStateException("Spec styles that support nested tests are disabled in kotest-js because the underlying JS frameworks do not support promises for outer root scopes. Please use FunSpec, StringSpec, or ShouldSpec and ensure that only top level tests are used.")
                     }
                  }
                  TestCaseExecutor(
                     listener,
                     CallingThreadExecutionContext,
                     validateTestCase = {
                        it.spec is FunSpec || it.spec is StringSpec || it.spec is ShouldSpec
                     },
                     toTestResult = { t, duration ->
                        when (t) {
                           null -> TestResult.success(duration)
                           is AssertionError -> TestResult.failure(t, duration)
                           else -> TestResult.error(t, duration)
                        }
                     }
                  ).execute(root.testCase, context)
               }

               // we don't want to return a promise here as the js frameworks will use that for test resolution
               // instead of the done callback, and we prefer the callback as it allows for custom timeouts
               // without the need for the user to configure them on the js side.
               Unit
            }
         }
   }
}
