@file:Suppress("unused")

package io.kotest.engine.execution

import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
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

            TestCaseExecutor(listener, CallingThreadExecutionContext).execute(root.testCase, context)

         }
      }
}
