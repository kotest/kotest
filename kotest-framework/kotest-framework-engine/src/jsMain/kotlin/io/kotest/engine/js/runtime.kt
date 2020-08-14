package io.kotest.engine.js

import io.kotest.engine.config.Project
import io.kotest.engine.spec.AbstractSpec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.format
import io.kotest.engine.CallingThreadExecutionContext
import io.kotest.engine.TestCaseExecutor
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.isActive
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext

/**
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
actual fun executeSpec(spec: AbstractSpec) {
   spec.rootTests()
      .filter { it.testCase.isActive() }
      .forEach { root ->
      // we have to always start the test so that the framework doesn't exit before we return
      // also it gives us a handle to the done callback
      it(root.testCase.description.name.format(Project.testNameCase(), Project.includeTestScopePrefixes())) { done ->

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
            TestCaseExecutor(listener, CallingThreadExecutionContext).execute(root.testCase, context)
         }

         // we don't want to return the promise as the js frameworks will use that for test resolution
         // instead of the done callback and we prefer the callback as it allows for custom timeouts
         // without the need for the user to configure them on the js side.
         Unit
      }
   }
}

/**
 * Sets the [KotestAdapter] used to intercept javascript test calls so that we can re-route them to kotest.
 */
actual fun configureRuntime() {
   setAdapter(KotestAdapter)
}
