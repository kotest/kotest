@file:Suppress("unused")

package io.kotest.engine.execution

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.isEnabledInternal
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

/**
 * Entry point for native tests.
 * This method is invoked by the compiler plugin.
 */
@DelicateCoroutinesApi
fun executeSpec(spec: Spec) {
   spec.materializeAndOrderRootTests()
      .filter { it.testCase.isEnabledInternal().isEnabled }
      .forEach { root ->

         runBlocking {

            val listener = object : TestCaseExecutionListener {
               override fun testStarted(testCase: TestCase) {
                  TeamCityLogger().start(testCase.displayName)
               }

               override fun testIgnored(testCase: TestCase) {
               }

               override fun testFinished(testCase: TestCase, result: TestResult) {
                  TeamCityLogger().pass(testCase.displayName, 124)
               }
            }

            val context = object : TestContext {
               override val testCase: TestCase = root.testCase
               override val coroutineContext: CoroutineContext = this@runBlocking.coroutineContext
               override suspend fun registerTestCase(nested: NestedTest) {
                  throw IllegalStateException("Spec styles that support nested tests are disabled in kotest-native")
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
      }
}
