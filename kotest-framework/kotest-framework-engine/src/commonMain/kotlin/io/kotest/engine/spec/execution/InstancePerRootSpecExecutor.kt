package io.kotest.engine.spec.execution

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.DefaultTestScope
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.isRootTest
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRefInflator
import io.kotest.engine.spec.TestResults
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.names.DuplicateTestNameHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

internal class InstancePerRootSpecExecutor(
   private val context: EngineContext,
) : SpecExecutor() {

   private val pipeline = SpecInterceptorPipeline(context)
   private val extensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)
   private val materializer = Materializer(context.specConfigResolver)
   private val results = TestResults()

   private val inflator = SpecRefInflator(
      registry = context.registry,
      projectConfigRegistry = context.projectConfigResolver,
      extensions = extensions,
   )

   override suspend fun execute(ref: SpecRef, seed: Spec): Result<Map<TestCase, TestResult>> {
      val specContext = SpecContext.create()

      // we switch to a new coroutine for each spec instance
      return withContext(CoroutineName("spec-scope-" + seed.hashCode())) {

         // for the seed spec that is passed in, we need to run the instance pipeline,
         // then materialize the root tests. These root tests will either execute in the
         // seed instance (for the first test), or in a fresh instance (for the rest).

         pipeline.execute(seed, specContext) {
            materializeAndInvokeRootTests(seed, ref, specContext)
            Result.success(results.toMap())
         }.map { results.toMap() } // we only use the test results if the pipeline completes successfully
      }
   }

   private suspend fun materializeAndInvokeRootTests(seed: Spec, ref: SpecRef, specContext: SpecContext) {

      val rootTests = materializer.materialize(seed)
      val duplicateTestNameHandler = DuplicateTestNameHandler()

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
                     executeTest(testCase = root, specContext = specContext)
                  } else {
                     // for subsequent tests, we create a new instance of the spec
                     // and will re-run the pipelines etc
                     executeRootInFreshSpec(
                        root = root,
                        ref = ref,
                     )
                  }
               }
            }
         }
      }
   }

   /**
    * Executes the given root [TestCase] in a new spec instance.
    * It will create the instance and run the pipeline on that, before
    * using that spec for the test execution.
    */
   private suspend fun executeRootInFreshSpec(
      root: TestCase,
      ref: SpecRef,
   ) {
      require(root.isRootTest())

      val spec = inflator.inflate(ref).getOrThrow()

      // map all the names again so they are unique, and then find the matching root test in the new spec instance
      val freshRoot = materializer.materialize(spec)
         .first { it.descriptor == root.descriptor }

      val specContext = SpecContext.create()

      // we switch to a new coroutine for each spec instance
      withContext(CoroutineName("spec-scope-" + spec.hashCode())) {
         pipeline.execute(spec, specContext) {
            val result = executeTest(freshRoot, specContext)
            Result.success(mapOf(freshRoot to result))
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
      val duplicateTestNameHandler = DuplicateTestNameHandler()
      val duplicateTestNameMode = context.specConfigResolver.duplicateTestNameMode(testCase.spec)
      val executor = TestCaseExecutor(context)
      val result = executor.execute(
         testCase = testCase,
         testScope = DefaultTestScope(testCase) {
            val unique = duplicateTestNameHandler.unique(duplicateTestNameMode, it.name)
            val uniqueName = it.name.copy(name = unique)
            val nestedTestCase = materializer.materialize(it.copy(name = uniqueName), testCase)
            executeTest(nestedTestCase, specContext)
         },
         specContext = specContext
      )
      results.completed(testCase, result)
      return result
   }
}


