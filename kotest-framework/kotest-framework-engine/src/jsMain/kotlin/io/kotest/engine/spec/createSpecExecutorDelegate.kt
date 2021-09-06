package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.describe
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.TerminalTestContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseListenerToTestEngineListenerAdapter
import io.kotest.mpp.bestName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.coroutineContext

actual fun createSpecExecutorDelegate(listener: TestEngineListener): SpecExecutorDelegate {
   TODO("Not yet implemented")
}


/**
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
class JavascriptSpecExecutorDelegate(private val listener: TestEngineListener) : SpecExecutorDelegate {

   @DelicateCoroutinesApi
   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      val cc = coroutineContext
      // we use the spec itself as an outer/parent test.
      describe(spec::class.bestName()) {
         spec.materializeAndOrderRootTests().forEach { root ->
            GlobalScope.promise {
               TestCaseExecutor(
                  TestCaseListenerToTestEngineListenerAdapter(listener),
                  CallingThreadExecutionContext
               ).execute(root.testCase, TerminalTestContext(root.testCase, cc))
            }

            // we don't want to return the promise as the js frameworks will use that for test resolution
            // instead of the done callback, and we prefer the callback as it allows for custom timeouts
            Unit

//            val enabled = root.testCase.isEnabledInternal()
//            if (enabled.isEnabled) {
//               // we have to always invoke `it` to start the test so that the js test framework doesn't exit
//               // before we invoke our callback. This also gives us the handle to the done callback.
//               val test = it(root.testCase.description.name.displayName) { done ->
//                  executeTest(root.testCase) { result ->
//                     done(result.error)
//                     onComplete()
//                  }
//               }
//               // some frameworks default to a 2000 timeout,
//               // here we set to a high number and use the timeout support kotest provides via coroutines
//               test.timeout(Int.MAX_VALUE)
//               Unit
//            } else {
//               xit(root.testCase.displayName) {}
//            }
         }
      }
      return emptyMap()
   }
}
