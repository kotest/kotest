package io.kotest.engine

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.RootRestrictedTestContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.mpp.bestName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

/**
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
@OptIn(DelicateCoroutinesApi::class)
actual class SpecRunner {

   /**
    * Executes a single [spec].
    *
    * In Javascript the specs are instantiated in advance and passed to the engine.
    *
    * Once the inner test promise completes, the [onComplete] callback is invoked.
    */
   actual fun execute(spec: Spec, onComplete: suspend () -> Unit) {
      // we use the spec itself as an outer/parent test.
      describe(spec::class.bestName()) {
         spec.materializeAndOrderRootTests().forEach { root ->

            val enabled = root.testCase.isEnabledInternal()
            if (enabled.isEnabled) {
               // we have to always invoke `it` to start the test so that the js test framework doesn't exit
               // before we invoke our callback. This also gives us the handle to the done callback.
               it(root.testCase.description.name.displayName) { done ->
                  executeTest(root.testCase) { result ->
                     done(result.error)
                     onComplete()
                  }

               }
            } else {
               xit(root.testCase.displayName) {}
            }
         }
      }
   }

   private fun executeTest(testCase: TestCase, onComplete: suspend (TestResult) -> Unit) {
      // done is the JS promise
      // some frameworks default to a 2000 timeout,
      // we can change this to the kotest test setting
//      done.timeout(testCase.resolvedTimeout())

      val listener = CallbackTestCaseExecutionListener(onComplete)

      GlobalScope.promise {
         val context = RootRestrictedTestContext(testCase, this.coroutineContext)
         val executor = TestCaseExecutor(
            listener,
            CallingThreadExecutionContext
         )
         executor.execute(testCase, context)
      }

      // we don't want to return a promise here as the js frameworks will use that for test resolution
      // instead of the done callback, and we prefer the callback as it allows for custom timeouts
      // without the need for the user to configure them on the js side.
      Unit
   }
}
