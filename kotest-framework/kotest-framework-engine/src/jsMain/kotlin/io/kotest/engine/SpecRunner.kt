package io.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EmptyTestSuiteInterceptor
import io.kotest.engine.interceptors.EngineInterceptor
import io.kotest.engine.interceptors.ProjectExtensionEngineInterceptor
import io.kotest.engine.interceptors.ProjectListenerEngineInterceptor
import io.kotest.engine.interceptors.SpecSortEngineInterceptor
import io.kotest.engine.interceptors.SpecStyleValidationInterceptor
import io.kotest.engine.interceptors.TestDslStateInterceptor
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
               val test = it(root.testCase.description.name.displayName) { done ->
                  executeTest(root.testCase) { result ->
                     done(result.error)
                     onComplete()
                  }

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
   }

   private fun executeTest(testCase: TestCase, onComplete: suspend (TestResult) -> Unit) {
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
      Unit
   }
}

actual fun testEngineInterceptors(): List<EngineInterceptor> {
   return listOfNotNull(
      TestDslStateInterceptor,
      SpecStyleValidationInterceptor,
      SpecSortEngineInterceptor,
      ProjectExtensionEngineInterceptor(configuration.extensions().filterIsInstance<ProjectExtension>()),
      ProjectListenerEngineInterceptor(configuration.extensions()),
      if (configuration.failOnEmptyTestSuite) EmptyTestSuiteInterceptor else null,
   )
}
