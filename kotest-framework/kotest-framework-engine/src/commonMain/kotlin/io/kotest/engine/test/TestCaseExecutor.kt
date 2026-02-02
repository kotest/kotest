package io.kotest.engine.test

import io.kotest.common.ExperimentalKotest
import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestScope
import io.kotest.engine.TestEngineContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.interceptors.AssertionModeInterceptor
import io.kotest.engine.test.interceptors.CoroutineDebugProbeInterceptor
import io.kotest.engine.test.interceptors.CoroutineDispatcherFactoryTestInterceptor
import io.kotest.engine.test.interceptors.CoroutineLoggingInterceptor
import io.kotest.engine.test.interceptors.DescriptorPathContextInterceptor
import io.kotest.engine.test.interceptors.HandleSkippedExceptionsTestInterceptor
import io.kotest.engine.test.interceptors.InvocationCountCheckInterceptor
import io.kotest.engine.test.interceptors.InvocationTimeoutInterceptor
import io.kotest.engine.test.interceptors.KotlinTestRunTest
import io.kotest.engine.test.interceptors.LifecycleInterceptor
import io.kotest.engine.test.interceptors.NextTestExecutionInterceptor
import io.kotest.engine.test.interceptors.SoftAssertInterceptor
import io.kotest.engine.test.interceptors.SupervisorScopeInterceptor
import io.kotest.engine.test.interceptors.TestCaseExtensionInterceptor
import io.kotest.engine.test.interceptors.TestCoroutineInterceptor
import io.kotest.engine.test.interceptors.TestEnabledCheckInterceptor
import io.kotest.engine.test.interceptors.TestFinishedInterceptor
import io.kotest.engine.test.interceptors.TestNameContextInterceptor
import io.kotest.engine.test.interceptors.TimeoutInterceptor
import io.kotest.engine.test.interceptors.blockedThreadTimeoutInterceptor
import io.kotest.engine.test.interceptors.coroutineErrorCollectorInterceptor
import io.kotest.engine.test.interceptors.testInterceptorsForPlatform
import io.kotest.engine.test.listener.TestCaseExecutionListenerToTestEngineListenerAdapter
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.time.TimeSource

/**
 * Executes a single [TestCase].
 *
 * Uses a [TestCaseExecutionListener] to notify callers of events in the test lifecycle.
 *
 */
@OptIn(ExperimentalKotest::class)
internal class TestCaseExecutor(
   private val listener: TestCaseExecutionListener,
   private val context: TestEngineContext,
) {

   /**
    * Creates a [TestCaseExecutor] that delegates test events to the [TestEngineListener] provided
    * by the [TestEngineContext].
    */
   constructor(context: TestEngineContext) : this(
      TestCaseExecutionListenerToTestEngineListenerAdapter(context.listener),
      context
   )

   private val logger = Logger(TestCaseExecutor::class)

   suspend fun execute(testCase: TestCase, testScope: TestScope, specContext: SpecContext): TestResult {
      logger.log { Pair(testCase.name.name, "Executing test with scope $testScope") }

      val timeMark = TimeSource.Monotonic.markNow()

      val isKotlinTestRunTest: Boolean = coroutineContext[KotlinTestRunTest] != null
      val useCoroutineTestScope = context.testConfigResolver.coroutineTestScope(testCase)

      val interceptors = listOfNotNull(
         DescriptorPathContextInterceptor,
         TestNameContextInterceptor,
         FailFastInterceptor(context, specContext),
         TestFinishedInterceptor(listener, context.testExtensions()),
         InvocationCountCheckInterceptor(context.testConfigResolver),
         SupervisorScopeInterceptor,
         // the dispatcher factory should run before the before/after callbacks, so they are executed in the right context
         CoroutineDispatcherFactoryTestInterceptor(context.specConfigResolver),
         if (platform == Platform.JVM) coroutineErrorCollectorInterceptor() else null,
         TestEnabledCheckInterceptor(
            context.projectConfigResolver,
            context.specConfigResolver,
            context.testConfigResolver
         ),
         TestCaseExtensionInterceptor(context.testExtensions()),
         LifecycleInterceptor(listener, timeMark, context.testExtensions()),
         AssertionModeInterceptor(context.testConfigResolver),
         SoftAssertInterceptor(context.testConfigResolver),
         CoroutineLoggingInterceptor(context.projectConfigResolver, context.testExtensions()),
         if (platform == Platform.JVM)
            blockedThreadTimeoutInterceptor(timeMark, context.testConfigResolver)
         else null,
         TimeoutInterceptor(timeMark, context.testConfigResolver),
         HandleSkippedExceptionsTestInterceptor,
         *testInterceptorsForPlatform().toTypedArray(),
         TestInvocationInterceptor(
            timeMark = timeMark,
            invocationInterceptors = listOfNotNull(
               // Timeout is handled inside TestCoroutineInterceptor if it is enabled
               if (useCoroutineTestScope && !isKotlinTestRunTest)
                  TestCoroutineInterceptor(context.testConfigResolver)
               else
                  InvocationTimeoutInterceptor(context.testConfigResolver),
            ),
            testConfigResolver = context.testConfigResolver,
            testExtensions = context.testExtensions()
         ),
         CoroutineDebugProbeInterceptor(context.testConfigResolver),
      )

      val base = NextTestExecutionInterceptor { tc, scope ->
         logger.log { Pair(testCase.name.name, "Executing test") }

         // workaround for anyone using delay in a test on wasm
         // https://github.com/Kotlin/kotlinx.coroutines/issues/4239
         withContext(scope.coroutineContext) { }

         tc.test(scope)
         TestResultBuilder.builder().withDuration(timeMark.elapsedNow()).build()
      }

      return interceptors.foldRight(base) { ext, fn ->
         NextTestExecutionInterceptor { tc, tscope -> ext.intercept(tc, tscope, fn) }
      }.invoke(testCase, testScope)
   }
}

/**
 * The [TestCoroutineScheduler] acts as the "virtual clock" for your tests, while the [TestDispatcher] is the mechanism
 * that uses that clock to execute code.

 * The scheduler is the central source of truth for virtual time in a test. It performs two critical roles:
 * - Skips Delays: It automatically fast-forwards through delay() calls so that a test requiring a 10-second wait finishes instantly.
 * - Orchestrates Execution: It maintains a queue of tasks and determines their execution order based on their scheduled virtual time.

 * A [TestDispatcher] (like [StandardTestDispatcher]) is always linked to exactly one TestCoroutineScheduler.
 *
 * Task Queuing: When you launch a coroutine on a TestDispatcher, the dispatcher doesn't run the code itself.
 * Instead, it sends the task to its linked scheduler.
 *
 * Clock Management: The scheduler waits for you to manually move the clock. When you call methods
 * like `advanceUntilIdle` or `advanceTimeBy(ms)` on the scheduler, it tells the linked dispatchers to execute the
 * tasks whose time has come.
 *
 * Synchronization: You can link multiple TestDispatchers to the same scheduler. This ensures that even
 * if different parts of your code use different dispatchers (e.g., one for Main, one for IO), they all share
 * the same virtual clock and stay in sync.
 *
 * Summary of Key Methods:
 *
 * You typically interact with the scheduler through a TestScope provided by runTest, which exposes these controls:
 * `testScheduler.runCurrent()`: Runs tasks scheduled at the current virtual time.
 * `testScheduler.advanceTimeBy(delay)`: Moves the clock forward and executes tasks in that window.
 * `testScheduler.advanceUntilIdle()`: Fast-forwards until there are no more tasks left to run.
 *
 * Sharing a single [TestCoroutineScheduler] ensures that all your dispatchers (e.g., Main and IO)
 * operate on the same virtual timeline. If you advance time on the scheduler, it affects every dispatcher linked to it.
 *
 * Manual Sharing (Passing the Scheduler):
 * You can create a TestCoroutineScheduler explicitly and pass it to multiple dispatchers during initialization.
 *
 * Automatic Sharing (via Dispatchers.setMain):
 * If you set the Main dispatcher to a [TestDispatcher] at the start of your test, any new [TestDispatcher] created
 * afterward will automatically inherit the same scheduler.
 *
 * eg
 *
 * @Before
 * fun setup() {
 *     // Setting Main first establishes the shared scheduler
 *     Dispatchers.setMain(StandardTestDispatcher())
 * }
 *
 * @Test
 * fun autoSharingTest() = runTest {
 *     // This dispatcher will automatically use the same scheduler as `Dispatchers.Main`
 *     val ioDispatcher = StandardTestDispatcher()
 *     // Now both Dispatchers.Main and ioDispatcher are perfectly in sync
 * }
 */
class Executor {

}
