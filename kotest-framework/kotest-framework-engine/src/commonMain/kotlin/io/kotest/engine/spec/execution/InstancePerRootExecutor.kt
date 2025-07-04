package io.kotest.engine.spec.execution

import io.kotest.core.Logger
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.isRootTest
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRefInflator
import io.kotest.engine.spec.TestResults
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutor
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

internal class InstancePerRootExecutor(
   private val context: EngineContext,
) : SpecExecutor() {

   private val pipeline = SpecInterceptorPipeline(context)
   private val extensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)
   private val results = TestResults()

   private val inflator = SpecRefInflator(
      registry = context.registry,
      projectConfigRegistry = context.projectConfigResolver,
      extensions = extensions,
   )

   override suspend fun execute(ref: SpecRef, seed: Spec): Result<Map<TestCase, TestResult>> {
      // we switch to a new coroutine for each spec instance
      return withContext(CoroutineName("spec-scope-" + seed.hashCode())) {
         val specContext = SpecContext.create()

         // for the seed spec that is passed in, we need to run the instance pipeline,
         // then register all the root tests. These root tests will either execute in the
         // seed instance (for the first test), or in a fresh instance (for the rest).

         pipeline.execute(seed, specContext) {
            launchRootTests(seed, ref, specContext)
            Result.success(results.toMap())
         }

         Result.success(results.toMap())
      }
   }

   private suspend fun launchRootTests(seed: Spec, ref: SpecRef, specContext: SpecContext) {

      val rootTests = Materializer(context.specConfigResolver).materialize(seed)

      // controls how many tests to execute concurrently
      val concurrency = context.specConfigResolver.testExecutionMode(seed).concurrency
      val semaphore = Semaphore(concurrency)

      // all root test coroutines are launched immediately,
      // the semaphore will control how many can actually run concurrently

      coroutineScope { // will wait for all tests to complete
         rootTests.withIndex().toList().forEach { (index, root) ->
            launch {
               semaphore.withPermit {
                  if (index == 0) {
                     /**
                      * The first time we run a root test, we can use the already instantiated spec as the instance.
                      * This avoids creating specs that do nothing other than scheduling tests for other specs to run in.
                      * Eg, see https://github.com/kotest/kotest/issues/3490
                      */
                     executeTest(root, specContext)
                  } else {
                     // for subsequent tests, we create a new instance of the spec
                     // and will re-run the pipelines etc
                     executeInFreshSpec(root, ref)
                  }
               }
            }
         }
      }
   }

   /**
    * Executes the given [TestCase] that is a root test.
    *
    * It will create a new spec instance and run the pipeline on that, before
    * using that spec for the test execution.
    */
   private suspend fun executeInFreshSpec(testCase: TestCase, ref: SpecRef) {
      require(testCase.isRootTest())
      val spec = inflator.inflate(ref).getOrThrow()
      val specContext = SpecContext.create()
      // we switch to a new coroutine for each spec instance
      withContext(CoroutineName("spec-scope-" + spec.hashCode())) {
         pipeline.execute(spec, specContext) {
            val result = executeTest(testCase.copy(spec = spec), specContext)
            Result.success(mapOf(testCase to result))
         }
      }
   }

   /**
    * Executes the given [TestCase] using a [TestCaseExecutor].
    * Logs the results in the results tree.
    *
    * @return the result of this single test.
    */
   private suspend fun executeTest(testCase: TestCase, specContext: SpecContext): TestResult {
      val executor = TestCaseExecutor(context)
      val result = executor.execute(testCase, SameSpecTestScope(testCase, specContext, coroutineContext), specContext)
      results.completed(testCase, result)
      return result
   }

   /**
    * A [TestScope] that runs discovered tests as soon as they are registered in the same spec instance.
    *
    * This implementation tracks fail fast if configured via spec config or globally.
    */
   inner class SameSpecTestScope(
      override val testCase: TestCase,
      private val specContext: SpecContext,
      override val coroutineContext: CoroutineContext,
   ) : TestScope {

      private val logger = Logger(SameSpecTestScope::class)

      private val failFastReason = "Skipping test due to fail fast"

      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.name, "Registering nested test '${nested}'") }

         val nestedTestCase = Materializer(context.specConfigResolver)
            .materialize(nested, testCase)

         // if a previous test has failed and this test is marked as fail fast, it will be ignored
         val failFast = context.testConfigResolver.failfast(nestedTestCase)

         if (failFast && results.hasErrorOrFailure()) {

            logger.log { Pair(testCase.name.name, failFastReason) }
            context.listener.testIgnored(nestedTestCase, failFastReason)
            context.testExtensions().ignoredTestListenersInvocation(nestedTestCase, failFastReason)

         } else {
            executeTest(nestedTestCase, specContext)
         }
      }
   }
}


