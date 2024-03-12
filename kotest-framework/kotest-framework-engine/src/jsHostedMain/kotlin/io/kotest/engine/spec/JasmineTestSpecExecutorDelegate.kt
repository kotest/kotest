package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.JsTestDoneCallback
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.jasmineTestDescribe
import io.kotest.engine.jasmineTestIt
import io.kotest.engine.jasmineTestXit
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.interceptors.escapedJsTestName
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.names.FallbackDisplayNameFormatter
import io.kotest.engine.test.names.getFallbackDisplayNameFormatter
import io.kotest.engine.test.scopes.DuplicateNameHandlingTestScope
import io.kotest.engine.test.status.isEnabledInternal
import io.kotest.mpp.Logger
import io.kotest.mpp.bestName
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A [SpecExecutorDelegate] running tests via a Jasmine-like JavaScript test framework (Jasmine/Mocha/Jest).
 *
 * Note: we need to use this: https://youtrack.jetbrains.com/issue/KT-22228
 */
@ExperimentalKotest
internal class JasmineTestSpecExecutorDelegate(
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
) : SpecExecutorDelegate {

   private val formatter: FallbackDisplayNameFormatter = getFallbackDisplayNameFormatter(
      context.configuration.registry,
      context.configuration,
   )

   override suspend fun execute(spec: Spec): Map<TestCase, TestResult> {
      val runner = SingleInstanceSpecJsRunner(
         specName = spec.describeName,
         defaultCoroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
         context = context,
         formatter = formatter,
         coroutineContext = coroutineContext,
      )

      runner.execute(spec)

      return emptyMap()
   }

   companion object {
      private val Spec.describeName: String
         get() = this::class.bestName().escapedJsTestName()
   }
}


private class SingleInstanceSpecJsRunner(
   private val specName: String,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val context: EngineContext,
   private val formatter: FallbackDisplayNameFormatter,
   val coroutineContext: CoroutineContext,
) {

   private val logger = Logger(SingleInstanceSpecJsRunner::class)
   private val pipeline = SpecInterceptorPipeline(context)
   private val materializer = Materializer(context.configuration)
   private val testEngineListener: TestEngineListener = context.listener

   private val TestCase.enabled: Boolean get() = isEnabledInternal(conf = context.configuration).isEnabled
   private fun TestCase.displayName(): String = formatter.format(this).escapedJsTestName()

   suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> {
      logger.log { Pair(spec::class.bestName(), "executing spec $spec") }
      try {
         pipeline.execute(spec) { _ ->
            val rootTests = materializer.materialize(spec)

            logger.log { "discovered ${rootTests.size} rootTests: ${rootTests.joinToString { it.displayName() }}" }

            rootTests.forEach { tc ->
               runTest(tc, null)
            }

            Result.success(emptyMap())
         }

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
   private inner class SingleInstanceTestScope(
      override val testCase: TestCase,
      val parentScope: SingleInstanceTestScope?,
   ) : TestScope {
      override val coroutineContext: CoroutineContext = this@SingleInstanceSpecJsRunner.coroutineContext

      // in the single instance runner we execute each nested test as soon as they are registered
      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.testName, "Registering nested test '${nested}") }

         val nestedTestCase = Materializer(context.configuration).materialize(nested, testCase)

         runTest(
            testCase = nestedTestCase,
            parentScope = this@SingleInstanceTestScope,
         )
      }
   }

   private suspend fun runTest(
      testCase: TestCase,
      parentScope: SingleInstanceTestScope?,
   ) {
      val scope = DuplicateNameHandlingTestScope(
         mode = context.configuration.duplicateTestNameMode,
         delegate = SingleInstanceTestScope(testCase, parentScope)
      )

      val parents = buildList {
         val scopeNames = generateSequence(parentScope) { it.parentScope }
            .map { it.testCase.displayName() }
         addAll(scopeNames)
         add(specName)
      }.reversed()

// this approach seems more accurate, but it doesn't work :( It overrides any parent describes?
//      fun scopes(names: ArrayDeque<String>, inner: () -> Unit) {
//         val name = names.removeFirstOrNull() ?: return inner()
//         describe(name) {
//            scopes(names, inner)
//         }
//      }
//      val parentsQueue = ArrayDeque(parents)
//      parentsQueue.addFirst(  specName.escapedJsTestName())
//      scopes(parentsQueue) {

      val describeName = parents.joinToString("⟶")

      jasmineTestDescribe(describeName) {

         if (testCase.enabled) {
            jasmineTestIt(testCase.displayName()) { done: JsTestDoneCallback ->

               val listener = JsTestCaseExecutionListenerAdapter(listener = testEngineListener, done = done)
               val executor = TestCaseExecutor(
                  listener = listener,
                  defaultCoroutineDispatcherFactory = defaultCoroutineDispatcherFactory,
                  context = context,
               )

               globalScopeLaunch {
                  val result = executor.execute(testCase, scope)
                  listener.testFinished(testCase, result)
               }
            }
         } else {
            jasmineTestXit(testCase.displayName()) {

            }
         }


//         handle.timeout(Int.MAX_VALUE)
      }
   }
}


// use GlobalScope, because Kotlin/JS doesn't have runBlocking {}
private fun globalScopeLaunch(
   block: suspend CoroutineScope.() -> Unit
) {
   @OptIn(DelicateCoroutinesApi::class)
   GlobalScope.launch { block() }
}


/**
 * Converts events fired to a [TestCaseExecutionListener] into events fired to a [TestEngineListener]
 */
private class JsTestCaseExecutionListenerAdapter(
   private val listener: TestEngineListener,
   private val done: JsTestDoneCallback?,
) : TestCaseExecutionListener {

   private val logger = Logger(TestCaseExecutionListenerToTestEngineListenerAdapter::class)

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "Adapting testFinished to engine event $result $testCase") }
      listener.testFinished(testCase, result)
      done?.invoke(result.errorOrNull)
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
