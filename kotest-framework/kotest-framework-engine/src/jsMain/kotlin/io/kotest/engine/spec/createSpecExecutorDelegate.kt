package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.PromiseTestCaseExecutionListener
import io.kotest.engine.describe
import io.kotest.engine.it
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.NoInterruptionExecutionContext
import io.kotest.engine.test.TerminalTestContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.engine.xit
import io.kotest.mpp.bestName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.coroutineContext

actual fun createSpecExecutorDelegate(listener: TestEngineListener): SpecExecutorDelegate =
   JavascriptSpecExecutorDelegate

/**
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
object JavascriptSpecExecutorDelegate : SpecExecutorDelegate {

   @DelicateCoroutinesApi
   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      val cc = coroutineContext
      // we use the spec itself as an outer/parent test.
      describe(spec::class.bestName()) {
         spec.materializeAndOrderRootTests().forEach { root ->

            // todo find a way to delegate this to the test case executor
            val enabled = root.testCase.isEnabledInternal()
            if (enabled.isEnabled) {
               // we have to always invoke `it` to start the test so that the js test framework doesn't exit
               // before we invoke our callback. This also gives us the handle to the done callback.
               val test = it(root.testCase.description.name.displayName) { done ->
                  // ideally we'd just launch the executor and have the listener setup the test
                  // but we can't launch a promise inside the describe and have it resolve the "it"
                  // this means we must duplicate the isEnabled check outside of the executor
                  GlobalScope.promise {
                     TestCaseExecutor(
                        PromiseTestCaseExecutionListener(done),
                        NoInterruptionExecutionContext
                     ).execute(root.testCase, TerminalTestContext(root.testCase, cc))
                  }

                  // we don't want to return the promise as the js frameworks will use that for test resolution
                  // instead of the done callback, and we prefer the callback as it allows for custom timeouts
                  Unit
               }
               // some frameworks default to a 2000 timeout,
               // here we set to a high number and use the timeout support kotest provides via coroutines
               test.timeout(Int.MAX_VALUE)
               Unit
            } else {
               xit(root.testCase.displayName) {}
            }
         }
      }
      return emptyMap()
   }
}
