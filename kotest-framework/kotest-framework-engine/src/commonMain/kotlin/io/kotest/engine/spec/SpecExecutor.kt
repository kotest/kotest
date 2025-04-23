package io.kotest.engine.spec

import io.kotest.common.KotestInternal
import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
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
import io.kotest.engine.spec.interceptor.NextSpecRefInterceptor
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.spec.interceptor.SpecRefInterceptorPipeline
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
import kotlin.reflect.KClass

/**
 * Executes a [SpecRef].
 *
 * First invokes the [SpecRef] against a [SpecRefInterceptorPipeline], then creates an instance
 * of the reference, then executes the spec.
 */
internal class SpecExecutor(
   private val context: EngineContext,
) {

   private val logger = Logger(SpecExecutor::class)
   private val pipeline = SpecRefInterceptorPipeline(context)
   private val extensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)

   suspend fun execute(kclass: KClass<out Spec>) {
      execute(SpecRef.Reference(kclass))
   }

   suspend fun execute(ref: SpecRef) {
      logger.log { Pair(ref.kclass.bestName(), "Received $ref") }
      val innerExecute = object : NextSpecRefInterceptor {
         override suspend fun invoke(ref: SpecRef): Result<Map<TestCase, TestResult>> {
            return createInstance(ref).flatMap { executeInDelegate(it) }
         }
      }
      pipeline.execute(ref, innerExecute)
   }

   private suspend fun executeInDelegate(spec: Spec): Result<Map<TestCase, TestResult>> {
      return try {
         execute(spec)
      } catch (t: Throwable) {
         logger.log { Pair(spec::class.bestName(), "Error executing spec $t") }
         Result.failure(t)
      }
   }

   /**
    * Creates an instance of the given [SpecRef], notifies users of the instantiation event
    * or instantiation failure, and returns a Result with the error or spec.
    *
    * After this method is called the spec is sealed so no further configuration or root tests can be added.
    */
   private suspend fun createInstance(ref: SpecRef): Result<Spec> =
      ref.instance(context.registry, context.projectConfigResolver)
         .onFailure { extensions.specInstantiationError(ref.kclass, it) }
         .flatMap { spec -> extensions.specInstantiated(spec).map { spec } }
         .onSuccess { if (it is DslDrivenSpec) it.seal() }

   // we need a structure to store the results of each test case as they complete and it must be thread safe
   private val results = TestResults()

   /**
    * The entry point to execute a [Spec] instance.
    */
   private suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> {
      logger.log { Pair(spec::class.bestName(), "Materializing tests") }

      // for the primary spec that is passed in, we need to run the instance pipeline,
      // then register all the root tests. These root tests will either execute in the
      // same instance (SingleInstance mode), or in a fresh instance (InstancePerRoot mode).

      // we switch to a new coroutine for each spec instance
      return withContext(CoroutineName("spec-scope-" + spec.hashCode())) {
         val specContext = SpecContext.create()
         runInstancePipeline(spec, specContext) {
            val tests = Materializer(context.specConfigResolver).materialize(spec).withIndex().toList()
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
                  when (context.specConfigResolver.isolationMode(spec)) {
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
                  Materializer(context.specConfigResolver).materialize(spec).first { it.descriptor == target }
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
      val concurrency = context.specConfigResolver.testExecutionMode(spec).concurrency
      logger.log { "Launching tests with $concurrency max concurrency" }
      return concurrency
   }

   private suspend fun runInstancePipeline(
      spec: Spec,
      specContext: SpecContext,
      inner: suspend (Spec) -> Result<Map<TestCase, TestResult>>,
   ): Result<Map<TestCase, TestResult>> {
      val pipeline = SpecInterceptorPipeline(context)
      val innerExecute = object : NextSpecInterceptor {
         override suspend fun invoke(spec: Spec): Result<Map<TestCase, TestResult>> {
            return inner(spec)
         }
      }
      return pipeline.execute(spec, specContext, innerExecute)
   }

   /**
    * Executes the given [TestCase] using a [io.kotest.engine.test.TestCaseExecutor].
    * Logs the results in the results tree.
    *
    * @return the result of this single test.
    */
   private suspend fun executeTest(testCase: TestCase, specContext: SpecContext): TestResult {

      val testExecutor = TestCaseExecutor(
         TestCaseExecutionListenerToTestEngineListenerAdapter(context.listener),
         context,
      )

      val scope = DuplicateNameHandlingTestScope(
         context.specConfigResolver.duplicateTestNameMode(testCase.spec),
         SpecExecutorTestScope(testCase, specContext, coroutineContext),
      )

      val result = testExecutor.execute(testCase, scope, specContext)
      results.completed(testCase, result)
      return result
   }

   /**
    * A [io.kotest.core.test.TestScope] that runs discovered tests as soon as they are registered in the same spec instance.
    *
    * This implementation tracks fail fast if configured via spec config or globally.
    */
   inner class SpecExecutorTestScope(
      override val testCase: TestCase,
      private val specContext: SpecContext,
      override val coroutineContext: CoroutineContext,
   ) : TestScope {

      private val logger = Logger(SpecExecutorTestScope::class)

      private val failFastReason = "Skipping test due to fail fast"

      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.name, "Registering nested test '${nested}") }

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

/**
 * Used to test a [SpecExecutor] from another module.
 * Should not be used by user's code and is subject to change.
 */
@KotestInternal
suspend fun testSpecExecutor(
   context: EngineContext,
   ref: SpecRef.Reference
) {
   SpecExecutor(context).execute(ref)
}
