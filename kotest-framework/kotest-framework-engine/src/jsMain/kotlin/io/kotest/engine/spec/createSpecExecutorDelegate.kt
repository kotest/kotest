package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType.Container
import io.kotest.core.test.TestType.Dynamic
import io.kotest.core.test.TestType.Test
import io.kotest.engine.JsTestDoneCallback
import io.kotest.engine.JsTestHandle
import io.kotest.engine.PromiseTestCaseExecutionListener
import io.kotest.engine.describe
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.invoke
import io.kotest.engine.it
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.interceptors.testNameEscape
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import io.kotest.engine.test.scopes.DuplicateNameHandlingTestScope
import io.kotest.engine.test.scopes.InOrderTestScope
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.engine.xdescribe
import io.kotest.engine.xit
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asPromise
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.js.Promise

internal actual fun createSpecExecutorDelegate(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   context: EngineContext,
): SpecExecutorDelegate =
   JavascriptSpecExecutorDelegate(
      defaultCoroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
      context = context,
   )

/**
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
@ExperimentalKotest
internal class JavascriptSpecExecutorDelegate(
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
) : SpecExecutorDelegate {

   private val formatter: FallbackDisplayNameFormatter = getFallbackDisplayNameFormatter(
      context.configuration.registry,
      context.configuration,
   )

   private val materializer = Materializer(context.configuration)

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      val executeCoroutineContext = coroutineContext

      // we use the spec itself as an outer/parent test.
//      describe(testNameEscape(spec::class.bestName())) {

      val runner = SingleInstanceSpecJsRunner(
         specName = testNameEscape(spec::class.bestName()),
//            scheduler = SequentialTestScheduler,
         defaultCoroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
         context = context,
         formatter = formatter,
         executeCoroutineContext = executeCoroutineContext,
      )

//         globalScopeLaunch {
      runner.execute(spec)
//         }

//         materializer.materialize(spec).forEach { root ->
//
//            val testRunner = JsTestRunner(
//               context = context,
//               defaultCoroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
//               formatter = formatter,
//               executeCoroutineContext = executeCoroutineContext,
////               materializer = materializer,
//            )
//
//            testRunner.run(root)
//         }
//      }
      return emptyMap()
   }
}


private class SingleInstanceSpecJsRunner(
   private val specName: String,
//   private val scheduler: TestScheduler,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
   private val formatter: FallbackDisplayNameFormatter,
   private val executeCoroutineContext: CoroutineContext,
) {

   //   private val results = mutableMapOf<TestCase, TestResult>()
   private val logger = Logger(SingleInstanceSpecJsRunner::class)
   private val pipeline = SpecInterceptorPipeline(context)
   private val materializer = Materializer(context.configuration)
   private val testEngineListener: TestEngineListener = context.listener

   private fun TestCase.displayName(): String = testNameEscape(formatter.format(this))

   suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> {
      logger.log { Pair(spec::class.bestName(), "executing spec $spec") }
      try {
//         globalScopeLaunch {

         pipeline.execute(spec) {
            val rootTests = materializer.materialize(spec)
//               logger.log { Pair(spec::class.bestName(), "Launching ${rootTests.size} root tests on $scheduler") }
//               scheduler.schedule({ runTest(it, coroutineContext, null) }, rootTests)
//               Result.success(results)

            rootTests.forEach { tc ->
               runTest(tc, null)
            }

            Result.success(emptyMap())
         }
//         }

         return Result.success(emptyMap())
      } catch (e: Exception) {
         e.printStackTrace()
         throw e
      }
   }

   /**
    * A [TestScope] that runs discovered tests as soon as they are registered in the same spec instance.
    *
    * This implementation tracks fail fast if configured via TestCase config or globally.
    */
   inner class SingleInstanceTestScope(
      override val testCase: TestCase,
      val parentScope: SingleInstanceTestScope?,
   ) : TestScope {
      override val coroutineContext: CoroutineContext = executeCoroutineContext

      // set to true if we failed fast and should ignore further tests
//      private var skipRemaining = false

      // in the single instance runner we execute each nested test as soon as they are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.testName, "Registering nested test '${nested}") }

//         describe(testCase.displayName()) {
//            globalScopeLaunch {
         val nestedTestCase = Materializer(context.configuration).materialize(nested, testCase)

         runTest(nestedTestCase, this@SingleInstanceTestScope)
//            }
//         }
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      parentScope: SingleInstanceTestScope?,
   ) {

//      val executor = TestCaseExecutor(
//         listener = TestCaseExecutionListenerToTestEngineListenerAdapter(testEngineListener),
//         defaultCoroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
//         context = context,
//      )
//      val scope = InOrderTestScope(
//         testCase = testCase,
//         coroutineContext = executeCoroutineContext,
//         mode = context.configuration.duplicateTestNameMode,
//         coroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
//         context = context,
//      )
      val scope = DuplicateNameHandlingTestScope(
         mode = context.configuration.duplicateTestNameMode,
         SingleInstanceTestScope(testCase, parentScope,)
      )

      fun scopes(names: ArrayDeque<String>, inner: () -> Unit) {
         val name = names.removeFirstOrNull() ?: return inner()
         describe(name) {
            scopes(names, inner)
         }
      }

      val parents = generateSequence(parentScope) { it.parentScope }
         .map { it.testCase.displayName() }
         .toList()
         .let(::ArrayDeque)

      parents.addFirst(specName)

      scopes(parents) {
         val testHandle = it(testCase.displayName()) { done ->

            val listener = JsTestCaseExecutionListenerAdapter(listener = testEngineListener, done = done)
            val executor = TestCaseExecutor(
               listener = listener,
               defaultCoroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
               context = context,
            )

//         val listener = PromiseTestCaseExecutionListener(done = done)

            globalScopeLaunch {
               val result = executor.execute(testCase, scope)
//            results[testCase] = result
               listener.testFinished(testCase, result)
            }
         }

         // some frameworks default to a 2000 timeout,
         // here we set to a high number and use the timeout support kotest provides via coroutines
         testHandle.timeout(Int.MAX_VALUE)
      }
   }
}


// hack, because Kotlin/JS doesn't have runBlocking{}
private fun globalScopeLaunch(
   block: suspend CoroutineScope.() -> Unit
): Promise<Unit> {
   @OptIn(DelicateCoroutinesApi::class)
   return GlobalScope
      .async { block() }
      .asPromise()
}



/**
 * Converts events fired to a [TestCaseExecutionListener] into events fired to a [TestEngineListener]
 */
private class JsTestCaseExecutionListenerAdapter(
   private val listener: TestEngineListener,
   private val done: JsTestDoneCallback,
) : TestCaseExecutionListener {

   private val logger = Logger(TestCaseExecutionListenerToTestEngineListenerAdapter::class)

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "Adapting testFinished to engine event $result $testCase") }
      listener.testFinished(testCase, result)
      done(result.errorOrNull)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      logger.log { Pair(testCase.name.testName, "Adapting testIgnored to engine event $reason $testCase") }
      listener.testIgnored(testCase, reason)
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "Adapting testStarted to engine event $testCase") }
      listener.testStarted(testCase)
   }
}


//region JsTestRunner
private class JsTestRunner(
   private val formatter: FallbackDisplayNameFormatter,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
   private val executeCoroutineContext: CoroutineContext,
//   private val materializer: Materializer,
) {

   fun run(
      testCase: TestCase,
   ) {
      val testDisplayName = testCase.displayName()

      val enabled = testCase.isEnabledInternal(context.configuration)

      if (!enabled.isEnabled) {
         return when (testCase.type) {
            Container -> xdescribe(testDisplayName) {}
            Test      -> xit(testDisplayName) {}
            Dynamic   -> xit(testDisplayName) {}
         }
      }

      when (testCase.type) {
         Container -> {
            describe(testDisplayName) {
               runDescribe(testCase)
//
//               // some frameworks default to a 2000 timeout,
//               // here we set to a high number and use the timeout support kotest provides via coroutines
//               test.timeout(Int.MAX_VALUE)
            }
         }

         Test      -> {
            val test = runTest(testCase)

            // some frameworks default to a 2000 timeout,
            // here we set to a high number and use the timeout support kotest provides via coroutines
            test.timeout(Int.MAX_VALUE)
         }

         Dynamic   -> {
            describe(testDisplayName) {
               runDescribe(testCase)
            }
         }
      }
   }

   private fun runDescribe(testCase: TestCase) {
      describe(testCase.displayName()) {
//         execute(testCase, null)
      }
   }

   private fun runTest(testCase: TestCase): JsTestHandle {
      // we have to always invoke `it` to start the test so that the js test framework doesn't exit
      // before we invoke our callback. This also gives us the handle to the done callback.
      return it(testCase.displayName()) { done ->
//         execute(testCase, done)
         TODO()
      }
   }

   private fun execute(testCase: TestCase, done: JsTestDoneCallback) {
      val duplicateTestNameMode = context.configuration.duplicateTestNameMode

      val scope = InOrderTestScope(
         testCase = testCase,
         coroutineContext = executeCoroutineContext,
         mode = duplicateTestNameMode,
         coroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
         context = context,
      )

      val executor = TestCaseExecutor(
         listener = PromiseTestCaseExecutionListener(done = done),
         defaultCoroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
         context = context,
      )

      @OptIn(DelicateCoroutinesApi::class) // because Kotlin/JS doesn't have runBlocking {}
      GlobalScope.launch {
         executor.execute(
            testCase = testCase,
            testScope = DuplicateNameHandlingTestScope(duplicateTestNameMode, scope),
         )
      }
   }

   private fun TestCase.displayName(): String = testNameEscape(formatter.format(this))
}
//endregion
