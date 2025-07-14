package io.kotest.engine.spec.execution

import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRefInflator
import io.kotest.engine.spec.TestResults
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.spec.interceptor.SpecInterceptorPipeline
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

@Deprecated("The semantics of instance per leaf are confusing and this mode should be avoided")
internal class InstancePerLeafSpecExecutor(
   private val context: EngineContext,
) : SpecExecutor() {

   private val logger = Logger(InstancePerLeafSpecExecutor::class)
   private val pipeline = SpecInterceptorPipeline(context)
   private val extensions = SpecExtensions(context.specConfigResolver, context.projectConfigResolver)
   private val materializer = Materializer(context.specConfigResolver)
   private val results = TestResults()

   private val inflator = SpecRefInflator(
      registry = context.registry,
      projectConfigRegistry = context.projectConfigResolver,
      extensions = extensions,
   )

   /**
    * The intention of this runner is that each **leaf** [TestCase] executes in its own instance
    * of the containing [Spec] class, but parent tests (containers) are executed in a single shared instance.
    */
   override suspend fun execute(ref: SpecRef, seed: Spec): Result<Map<TestCase, TestResult>> {
      // we switch to a new coroutine for each spec instance
      return withContext(CoroutineName("spec-scope-" + seed.hashCode())) {
         val specContext = SpecContext.create()

         // for the seed spec that is passed in, we need to run the instance pipeline,
         // then register all the root tests. Any root tests that are containers will execute in the seed instance,
         // but any leaf tests will execute in a fresh instance of the spec.

         pipeline.execute(seed, specContext) {
            launchRootTests(seed, ref, specContext)
            Result.success(results.toMap())
         }.map { results.toMap() }
      }
   }

   private suspend fun launchRootTests(seed: Spec, ref: SpecRef, specContext: SpecContext) {

      val roots = materializer.materialize(seed)

      // controls how many tests to execute concurrently
      val concurrency = context.specConfigResolver.testExecutionMode(seed).concurrency
      val semaphore = Semaphore(concurrency)

      // all root test coroutines are launched immediately,
      // the semaphore will control how many can actually run concurrently

      coroutineScope { // will wait for all tests to complete
         roots.forEach {
            launch {
               semaphore.withPermit {
                  executeTest(it, null, specContext, ref)
               }
            }
         }
      }
   }

   /**
    * Executes the given [TestCase] in a fresh spec instance.
    *
    * It will create a new spec instance and run the pipeline on that, before
    * using that spec for the test execution.
    *
    * It will locate the root that is the parent of the given [TestCase] and execute it in the new spec instance.
    */
   private suspend fun executeInFreshSpec(testCase: TestCase, ref: SpecRef, specContext: SpecContext) {
      require(testCase.type == TestType.Test) { "Only leaf tests should be executed in a fresh spec" }
      logger.log { "Enqueuing in a fresh spec ${testCase.descriptor}" }

      val spec = inflator.inflate(ref).getOrThrow()

      // we need to find the same root test but in the newly created spec
      val root = materializer.materialize(spec).first { it.descriptor.isPrefixOf(testCase.descriptor) }
      logger.log { "Located root for target $root" }

      // we switch to a new coroutine for each spec instance
      withContext(CoroutineName("spec-scope-" + spec.hashCode())) {
         pipeline.execute(spec, specContext) {
            val result = executeTest(root, testCase.descriptor, specContext, ref)
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
   private suspend fun executeTest(
      testCase: TestCase,
      target: Descriptor.TestDescriptor?,
      specContext: SpecContext,
      ref: SpecRef
   ): TestResult {
      val executor = TestCaseExecutor(
         // we need a special listener that only listens to the target test case events
         listener = TargetListeningListener(target, context.listener),
         context = context
      )
      val result = executor.execute(
         testCase = testCase,
         testScope = LeafLaunchingScope(
            testCase = testCase,
            target = target,
            specContext = specContext,
            coroutineContext = coroutineContext,
            ref = ref
         ),
         specContext = specContext
      )
      results.completed(testCase, result)
      return result
   }

   /**
    * A [TestScope] that runs leafs in fresh specs, otherwise continues in the same instance.
    */
   inner class LeafLaunchingScope(
      override val testCase: TestCase,
      private val target: Descriptor.TestDescriptor?,
      private val specContext: SpecContext,
      override val coroutineContext: CoroutineContext,
      private val ref: SpecRef,
   ) : TestScope {

      private val logger = Logger(LeafLaunchingScope::class)

      override suspend fun registerTestCase(nested: NestedTest) {
         logger.log { Pair(testCase.name.name, "Discovered nested test '${nested}'") }
         val nestedTestCase = materializer.materialize(nested, testCase)

         // we care about two scenarios:
         // - if the target is null, we are in discovery mode and nested tests will be executed if a container, or queued up if a leaf
         // - if the target is not null, we are trying to reach a specific test, and we will execute nested tests on the path to the target
         if (target == null) {
            logger.log { Pair(testCase.name.name, "Launching discovered test in discovery mode") }
            when (nestedTestCase.type) {
               TestType.Container -> executeTest(nestedTestCase, null, specContext, ref)
               TestType.Test -> executeInFreshSpec(nestedTestCase, ref, specContext)
            }
            return
         } else if (nestedTestCase.descriptor.isPrefixOf(target)) {
            executeTest(nestedTestCase, target, specContext, ref)
         }
      }
   }

   internal class TargetListeningListener(
      private val target: Descriptor.TestDescriptor?,
      private val delegate: TestEngineListener,
   ) : TestCaseExecutionListener {

      override suspend fun testStarted(testCase: TestCase) {
         if (target == null || testCase.type == TestType.Test) delegate.testStarted(testCase)
      }

      override suspend fun testIgnored(testCase: TestCase, reason: String?) {
         if (target == null || testCase.type == TestType.Test) delegate.testIgnored(testCase, reason)
      }

      override suspend fun testFinished(testCase: TestCase, result: TestResult) {
         if (target == null || testCase.type == TestType.Test) delegate.testFinished(testCase, result)
      }
   }
}


