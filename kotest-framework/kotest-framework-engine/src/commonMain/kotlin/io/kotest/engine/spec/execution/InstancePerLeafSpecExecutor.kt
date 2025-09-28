package io.kotest.engine.spec.execution

import io.kotest.core.Logger
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
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
import io.kotest.engine.test.TestResult
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
@Deprecated("The semantics of instance per leaf are confusing and this mode should be avoided")
internal class InstancePerLeafSpecExecutor(
   private val context: EngineContext,
) : SpecExecutor {

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
    *
    * The seed spec will be used for the first leaf discovered.
    */
   override suspend fun execute(ref: SpecRef, seed: Spec): Result<Map<TestCase, TestResult>> {
      launchRootTests(seed, ref)
      return Result.success(results.toMap())
   }

   private suspend fun launchRootTests(seed: Spec, ref: SpecRef) {

      val roots = materializer.materialize(seed)

      // controls how many tests to execute concurrently
      val concurrency = context.specConfigResolver.testExecutionMode(seed).concurrency
      val semaphore = Semaphore(concurrency)

      // all root test coroutines are launched immediately,
      // the semaphore will control how many can actually run concurrently

      coroutineScope { // will wait for all tests to complete
         roots.forEach { root ->
            launch {
               semaphore.withPermit {
                  RootTestExecutor().launchRootTest(root, ref)
               }
            }
         }
      }
   }

   inner class RootTestExecutor {
      private val testCasesToBeExecutedInFreshSpec = ArrayDeque<Pair<TestCase, SpecRef>>()
      private val finishedTestCases = mutableListOf<Pair<TestCase, TestResult>>()

      suspend fun launchRootTest(root: TestCase, ref: SpecRef) {
         val spec = inflator.inflate(ref).getOrThrow()

         executeInNewSpec(newSpec = spec) {
            val result = executeTest(root, null, it, ref)
            Result.success(mapOf(root to result))
         }

         while (testCasesToBeExecutedInFreshSpec.isNotEmpty()) {
            val (testCase, ref) = testCasesToBeExecutedInFreshSpec.removeFirst()
            executeInFreshSpec(testCase, ref)
         }

         finishedTestCases.sortedByDescending { (testCase, _) -> testCase.descriptor.depth() }
            .forEach { (testCase, testResult) -> context.listener.testFinished(testCase, testResult) }
      }

      private suspend fun executeInNewSpec(
         newSpec: Spec,
         executor: suspend (SpecContext) -> Result<Map<TestCase, TestResult>>
      ) {
         val specContext = SpecContext.create()

         withContext(CoroutineName("spec-scope-" + newSpec.hashCode())) {
            pipeline.execute(newSpec, specContext) {
               executor(specContext)
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
      private suspend fun executeInFreshSpec(testCase: TestCase, ref: SpecRef) {
         logger.log { "Enqueuing in a fresh spec ${testCase.descriptor}" }

         val spec = inflator.inflate(ref).getOrThrow()

         // we need to find the same root test but in the newly created spec
         val root = materializer.materialize(spec).first { it.descriptor.isPrefixOf(testCase.descriptor) }
         logger.log { "Located root for target $root" }

         executeInNewSpec(newSpec = spec) {
            val result = executeTest(root, testCase.descriptor, it, ref)
            Result.success(mapOf(testCase to result))
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
         val testScope = LeafLaunchingScope(
            testCase = testCase,
            target = target,
            specContext = specContext,
            coroutineContext = currentCoroutineContext(),
            ref = ref
         )
         val result = executor.execute(
            testCase = testCase,
            testScope = testScope,
            specContext = specContext
         )
         results.completed(testCase, result)
         testCasesToBeExecutedInFreshSpec.addAll(0, testScope.internalTestQueue)
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
         private var hasVisitedFirstNode = false

         private val logger = Logger(LeafLaunchingScope::class)

         val internalTestQueue = ArrayDeque<Pair<TestCase, SpecRef>>()

         override suspend fun registerTestCase(nested: NestedTest) {
            logger.log { Pair(testCase.name.name, "Discovered nested test '${nested.name.name}'") }
            val nestedTestCase = materializer.materialize(nested, testCase)

            if (target != null && !nestedTestCase.descriptor.isPrefixOf(target)) {
               // Should execute the given test case described by target but traversing an irrelevant test case now
               // Just return to abort from this logic
               return
            }
            if (hasVisitedFirstNode) {
               logger.log { Pair(testCase.name.name, "Executing in fresh spec") }
               internalTestQueue.add(nestedTestCase to ref)
               return
            }
            hasVisitedFirstNode = true

            if (target == null) {
               logger.log { Pair(testCase.name.name, "Launching discovered test in discovery mode") }
               when (nestedTestCase.type) {
                  TestType.Container -> {
                     logger.log { Pair(testCase.name.name, "Executing CONTAINER type in existing spec") }
                  }

                  TestType.Test -> {
                     logger.log { Pair(testCase.name.name, "Executing TEST type in existing spec") }
                  }
               }
               executeTest(nestedTestCase, null, specContext, ref)
            } else if (nestedTestCase.descriptor == target) {
               logger.log { Pair(testCase.name.name, "Start discovering tests from children nodes") }
               executeTest(nestedTestCase, null, specContext, ref)
            } else if (nestedTestCase.descriptor.isPrefixOf(target)) {
               logger.log { Pair(testCase.name.name, "Proceed discovery phase to the next node of target") }
               executeTest(nestedTestCase, target, specContext, ref)
            }
         }
      }

      inner class TargetListeningListener(
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
            if (testCase.type == TestType.Test) {
               delegate.testFinished(testCase, result)
            } else if (target == null) {
               finishedTestCases += testCase to result
            }
         }
      }
   }
}


