package io.kotest.engine

import io.kotest.core.Tags
import io.kotest.core.filter.SpecFilter
import io.kotest.core.filter.TestFilter
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.preconditions.ValidateSpec
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.CallingThreadExecutionContext
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.mpp.bestName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

data class TestEngineConfig(
   val testFilters: List<TestFilter>,
   val specFilters: List<SpecFilter>,
   val listener: TestEngineListener,
   val explicitTags: Tags?,
   val dumpConfig: Boolean,
   val validations: List<ValidateSpec>,
)

interface TestEngineListener {

   /**
    * Is invoked when the [TestEngine] is starting execution.
    */
   fun engineStarted() {}

   /**
    * Is invoked when the [TestEngine] has finished execution.
    *
    * If an unrecoverable error was detected during execution then it will be passed
    * as the parameter to the engine.
    */
   fun engineFinished(t: List<Throwable>) {}
}

val NoopTestEngineListener = object : TestEngineListener {}

data class TestSuite(val specs: List<Spec>)

/**
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
@DelicateCoroutinesApi
class TestEngine(private val config: TestEngineConfig) {

   fun execute(suite: TestSuite) {
      require(suite.specs.isNotEmpty()) { "Cannot invoke the engine with no specs" }

      suite.specs.forEach { spec ->
         config.validations.forEach {
            it.invoke(spec::class)
         }
      }

      execute(suite.specs)
   }

   private fun execute(specs: List<Spec>) {
      if (specs.isNotEmpty()) {
         val runner = SpecRunner()
         runner.execute(specs.first()) { execute(specs.drop(1)) }
      }
   }
}

@OptIn(DelicateCoroutinesApi::class)
class SpecRunner {

   /**
    * Executes a single [spec].
    *
    * In Javascript the specs are instantiated in advance and passed to the engine.
    *
    * Once the inner test promise completes, the [onComplete] callback is invoked.
    */
   fun execute(spec: Spec, onComplete: () -> Unit) {
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

   private fun executeTest(testCase: TestCase, onComplete: (TestResult) -> Unit) {
      // done is the JS promise
      // some frameworks default to a 2000 timeout,
      // we can change this to the kotest test setting
//      done.timeout(testCase.resolvedTimeout())

      val listener = CallbackTestCaseExecutionListener(onComplete)

      GlobalScope.promise {
         val context = TerminalTestContext(testCase, this.coroutineContext)
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
