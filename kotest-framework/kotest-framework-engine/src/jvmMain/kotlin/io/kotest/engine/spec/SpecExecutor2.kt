package io.kotest.engine.spec

import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.log
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.flatMap
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.NextSpecInterceptor
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import io.kotest.engine.test.scopes.DuplicateNameHandlingTestScope
import io.kotest.mpp.bestName
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * A [SpecExecutor2] is responsible for executing all the tests inside a single [Spec],
 * and handling the lifecycle callbacks of each spec. The executor handles executing tests in relation
 * to the spec isolation mode, instantiating fresh specs when required. It also handles the
 * execution (concurrency) mode of tests inside a spec.
 *
 * This class has a strange name because it will be merged into the parent SpecExecutor class in a future PR.
 */
@Suppress("DEPRECATION")
@Deprecated("This will be merged into the SpecExecutor itself in a future pr")
internal class SpecExecutor2(
   private val engineContext: EngineContext,
) {

   private val logger = Logger(SpecExecutor2::class)

   // we need a structure to store the results of each test case as they complete and it must be thread safe
   private val results = TestResults()

   /**
    * The entry point to execute a spec instance.
    */
   suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> {
      logger.log { Pair(spec::class.bestName(), "Materializing tests") }

      // for the primary spec that is passed in, we need to run the instance pipeline,
      // then register all the root tests. These root tests will either execute in the
      // same instance (SingleInstance mode), or in a fresh instance (InstancePerRoot mode).

      // we switch to a new coroutine for each spec instance
      return withContext(CoroutineName("spec-scope-" + spec.hashCode())) {
         val specContext = SpecContext.create()
         runInstancePipeline(spec, specContext) {
            val tests = Materializer(engineContext.specConfigResolver).materialize(spec).withIndex().toList()
            enqueueRootTests(spec, tests, specContext)
         }
      }
   }

   private suspend fun enqueueRootTests(
      spec: Spec,
      rootTests: List<IndexedValue<TestCase>>,
      specContext: SpecContext, // the context for the primary spec
   ): Result<Map<TestCase, TestResult>> {
      val semaphore = Semaphore(concurrency(spec)) // controls how many tests to execute concurrently
      coroutineScope { // will wait for all tests to complete
         rootTests.forEach { (index, root) ->
            launch {
               semaphore.withPermit {
                  when (engineContext.specConfigResolver.isolationMode(spec)) {
                     // in SingleInstance mode, we can just launch the test directly on the primary spec instance
                     IsolationMode.SingleInstance -> {
                        executeTest(testCase = root, specContext = specContext)
                     }
                     // in InstancePerRoot mode, we will create a new instance of the spec for each test,
                     // except for the first test which will use the primary spec instance.
                     IsolationMode.InstancePerRoot -> {
                        if (index == 0) {
                           executeTest(testCase = root, specContext = specContext)
                        } else {
                           executeInFreshSpec(target = root.descriptor, ref = SpecRef.Reference(spec::class))
                        }
                     }
                  }
               }
            }
         }
      }

      return Result.success(results.toMap())
   }

   /**
    * Creates a new spec and executes only the target root test (and children)
    */
   private suspend fun executeInFreshSpec(
      target: Descriptor.TestDescriptor,
      ref: SpecRef,
   ): Result<Map<TestCase, TestResult>> {
      require(target.isRootTest()) { "Descriptor must be a root test" }
      return createInstance(ref).flatMap { spec ->
         val specContext = SpecContext.create()
         // we switch to a new coroutine for each spec instance
         withContext(CoroutineName("spec-scope-" + spec.hashCode())) {
            runInstancePipeline(spec, specContext) {
               val test =
                  Materializer(engineContext.specConfigResolver).materialize(spec).first { it.descriptor == target }
               val result = executeTest(testCase = test, specContext = specContext)
               Result.success(mapOf(test to result))
            }
         }
      }
   }

   /**
    * Returns how many root tests should be launched concurrently.
    */
   private fun concurrency(spec: Spec): Int {
      val concurrency = engineContext.specConfigResolver.testExecutionMode(spec).concurrency
      log { "Launching tests with $concurrency max concurrency" }
      return concurrency
   }

   private suspend fun runInstancePipeline(
      spec: Spec,
      specContext: SpecContext,
      inner: suspend (Spec) -> Result<Map<TestCase, TestResult>>,
   ): Result<Map<TestCase, TestResult>> {
      val pipeline = SpecInterceptorPipeline(engineContext)
      val innerExecute = object : NextSpecInterceptor {
         override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
            return inner(spec)
         }
      }
      return pipeline.execute(spec, specContext, innerExecute)
   }

   /**
    * Executes the given [TestCase] using a [TestCaseExecutor].
    * Logs the results in the results tree.
    *
    * @return the result of this single test.
    */
   private suspend fun executeTest(testCase: TestCase, specContext: SpecContext): TestResult {

      val testExecutor = TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(engineContext.listener),
         engineContext,
      )

      val scope = DuplicateNameHandlingTestScope(
         engineContext.specConfigResolver.duplicateTestNameMode(testCase.spec),
         SpecExecutor2TestScope(testCase, specContext, coroutineContext),
      )

      val result = testExecutor.execute(testCase, scope, specContext)
      results.completed(testCase, result)
      return result
   }

   /**
    * Creates an instance of the given spec class, invokes callbacks for the instantiation event
    * or instantiation failure, and returns a Result with the error or spec.
    *
    * After this method is called the spec is sealed so no further configuration or root tests can be added.
    */
   private suspend fun createInstance(ref: SpecRef): Result<Spec> {
      return ref.instance(engineContext.registry, engineContext.projectConfigResolver)
         .onFailure { engineContext.specExtensions().specInstantiationError(ref.kclass, it) }
         .flatMap { spec -> engineContext.specExtensions().specInstantiated(spec).map { spec } }
         .onSuccess { if (it is DslDrivenSpec) it.seal() }
   }

   /**
    * A [TestScope] that runs discovered tests as soon as they are registered in the same spec instance.
    *
    * This implementation tracks fail fast if configured via spec config or globally.
    */
   inner class SpecExecutor2TestScope(
      override val testCase: TestCase,
      private val specContext: SpecContext,
      override val coroutineContext: CoroutineContext,
   ) : TestScope {

      private val logger = Logger(SpecExecutor2TestScope::class)

      private val failFastReason = "Skipping test due to fail fast"

      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.name, "Registering nested test '${nested}") }

         val nestedTestCase = Materializer(engineContext.specConfigResolver)
            .materialize(nested, testCase)

         // if a previous test has failed and this test is marked as fail fast, it will be ignored
         val failFast = engineContext.testConfigResolver.failfast(nestedTestCase)

         if (failFast && results.hasErrorOrFailure()) {

            logger.log { Pair(testCase.name.name, failFastReason) }
            engineContext.listener.testIgnored(nestedTestCase, failFastReason)
            engineContext.testExtensions().ignoredTestListenersInvocation(nestedTestCase, failFastReason)

         } else {
            executeTest(nestedTestCase, specContext)
         }
      }
   }
}


