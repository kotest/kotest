package io.kotest.core.config

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.KotestEngineProperties
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.config.DefaultExtensionRegistry
import io.kotest.engine.config.Defaults
import io.kotest.engine.config.ExtensionRegistry
import io.kotest.engine.coroutines.CoroutineDispatcherFactory
import io.kotest.mpp.sysprop
import kotlin.time.Duration

/**
 * This class defines project wide settings that are used when executing tests.
 *
 * Some settings here are fallback values. That is, a setting specified in a Spec or Test
 * will override the value here.
 */
class ProjectConfiguration {

   /**
    * Returns the [io.kotest.engine.config.ExtensionRegistry] that contains all extensions registered through
    * this configuration instance.
    */
   val registry: ExtensionRegistry = DefaultExtensionRegistry()

   /**
    * If enabled, then all failing spec names will be written to a "failure file".
    * This file can then be used by [SpecExecutionOrder.FailureFirst].
    *
    * Defaults to [io.kotest.engine.config.Defaults.writeSpecFailureFile].
    *
    * Note: Only has an effect on JVM.
    */
   var writeSpecFailureFile: Boolean = Defaults.writeSpecFailureFile

   /**
    * The path to write the failed spec list to, if enabled.
    *
    * Defaults to [Defaults.specFailureFilePath].
    *
    * Note: Only has an effect on JVM.
    */
   var specFailureFilePath: String = Defaults.specFailureFilePath

   /**
    * Specifies the minimum severity level for test cases to be executed.
    * If a test has a severity level lower than this value, it will not be executed.
    *
    * Eg, if the minimum runtime level is NORMAL and a test is defined with TRIVIAL, then that TRIVIAL
    * test case would not be executed.
    */
   var minimumRuntimeTestCaseSeverityLevel: TestCaseSeverityLevel? = null

   /**
    * If true, then all test cases are implicitly wrapped in an [io.kotest.assertions.assertSoftly] call.
    *
    * Defaults to [Defaults.GLOBAL_ASSERT_SOFTLY].
    */
   var globalAssertSoftly: Boolean = Defaults.GLOBAL_ASSERT_SOFTLY

   /**
    * The casing of test names can be adjusted using different strategies. It affects test
    * prefixes (I.e.: Given, When, Then) and test titles.
    *
    * This setting's options are defined in [TestNameCase].
    *
    * Defaults to [Defaults.TEST_NAME_CASE]
    */
   var testNameCase: TestNameCase = Defaults.TEST_NAME_CASE

   /**
    * If true, then the test execution will fail if any test is set to ignore.
    * If false, then ignored tests are outputted as normal.
    *
    * Defaults to [Defaults.FAIL_ON_IGNORED_TESTS].
    */
   var failOnIgnoredTests: Boolean = Defaults.FAIL_ON_IGNORED_TESTS

   /**
    * Returns the default assertion mode.
    */
   var assertionMode: AssertionMode = Defaults.ASSERTION_MODE

   /**
    * Returns the timeout for the execution of a test case in milliseconds.
    *
    * Note: This timeout includes the time required to executed nested tests.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.DEFAULT_TIMEOUT_MILLIS].
    */
   var timeout: Long = Defaults.DEFAULT_TIMEOUT_MILLIS

   /**
    * Returns the timeout for any single invocation of a test.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.DEFAULT_INVOCATION_TIMEOUT_MILLIS].
    */
   var invocationTimeout: Long = Defaults.DEFAULT_INVOCATION_TIMEOUT_MILLIS

   /**
    * Default number of invocations when not specified in any other place.
    */
   var invocations: Int = Defaults.INVOCATIONS

   /**
    * Each test is launched into its own coroutine. By default, the test engine waits for that
    * test to finish before launching the next test. By setting [testExecutionMode]
    * to [TestExecutionMode.Concurrent] all root tests will be launched at the same time.
    *
    * Setting this value to [TestExecutionMode.LimitedConcurrency] allows you to specify how
    * many root tests should be launched concurrently.
    *
    * Specs themselves will continue to be launched sequentially. To change that
    * see [specExecutionMode].
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. If you are using blocking calls in a test, setting [blockingTest] on that test's config
    * allows the test engine to spool up a new thread just for that test.
    */
   var testExecutionMode: TestExecutionMode = Defaults.TEST_EXECUTION_MODE

   /**
    * Each spec is launched into its own coroutine. By default, the test engine waits for all
    * tests in that spec to finish before launching the next spec. By setting [specExecutionMode]
    * to [SpecExecutionMode.Concurrent] all specs will be launched at the same time.
    *
    * Setting this value to [SpecExecutionMode.LimitedConcurrency] allows you to specify how
    * many specs should be launched concurrently.
    *
    * Tests inside each spec will continue to be launched sequentially. To change that
    * see [testExecutionMode].
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. If you are using blocking calls in a test, set [blockingTest] to true on that test's config.
    *
    * Note: Concurrency can be enabled and individual specs can still run in isolation by using the
    * [io.kotest.core.annotation.Isolate] annotation on that class. This annotation ensures that a spec
    * never runs concurrently regardless of any settings here.
    */
   var specExecutionMode: SpecExecutionMode = Defaults.SPEC_EXECUTION_MODE

   var coroutineDispatcherFactory: CoroutineDispatcherFactory? = null

   /**
    * A timeout that is applied to the overall project if not null,
    * if the sum duration of all the tests exceeds this the suite will fail.
    */
   var projectTimeout: Duration? = null

   /**
    * Controls which log functions on TestCase will be invoked or skipped
    */
   var logLevel: LogLevel = LogLevel.Off

   /**
    * Sets the default [failfast] for any test which doesn't override.
    */
   var failfast: Boolean = Defaults.FAILFAST

   var projectWideFailFast: Boolean = Defaults.projectWideFailFast

   var blockingTest: Boolean = Defaults.BLOCKING_TEST

   /**
    * Sets the default [TestCaseSeverityLevel] for any test which doesn't override.
    */
   var severity: TestCaseSeverityLevel = Defaults.TEST_CASE_SEVERITY_LEVEL

   var coroutineTestScope: Boolean = Defaults.COROUTINE_TEST_SCOPE

   /**
    * If set to true, then will cause the test suite to fail if there were no executed tests.
    */
   var failOnEmptyTestSuite: Boolean = Defaults.FAIL_ON_EMPTY_TEST_SUITE

   /**
    * Set to true to enable enhanced tracing of coroutines when an error occurs.
    *
    * Defaults to [Defaults.COROUTINE_DEBUG_PROBES]
    */
   var coroutineDebugProbes: Boolean = Defaults.COROUTINE_DEBUG_PROBES

   /**
    * Set to false and if a spec has no active tests (all disabled due to config or tags say)
    * then the spec itself will not appear as a node in output.
    *
    * Defaults to [Defaults.DISPLAY_SPEC_IF_NO_ACTIVE_TESTS]
    *
    * Note: This only works for JUnit and IntelliJ runners.
    */
   var displaySpecIfNoActiveTests: Boolean = Defaults.DISPLAY_SPEC_IF_NO_ACTIVE_TESTS

   /**
    * Controls the default [IsolationMode] that each spec will execute in.
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Default to [Defaults.ISOLATION_MODE]
    */
   var isolationMode: IsolationMode = Defaults.ISOLATION_MODE

   /**
    * Controls the ordering of root test cases in each spec.
    *
    * Valid options are the enum values of [TestCaseOrder].
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Defaults to [Defaults.TEST_CASE_ORDER]
    */
   var testCaseOrder: TestCaseOrder = Defaults.TEST_CASE_ORDER

   /**
    * Returns the sort order to use when executing specs.
    *
    * Defaults to [Defaults.specExecutionOrder]
    */
   var specExecutionOrder: SpecExecutionOrder = Defaults.specExecutionOrder

   /**
    * Sets the seed that is used when randomizing specs and tests.
    * Default is null, which will use the default random instance.
    */
   var randomOrderSeed: Long? = null

   var removeTestNameWhitespace: Boolean = false

   var testNameAppendTags: Boolean = false

   /**
    * Determines whether tags can be inherited from super types
    * Default is false
    */
   var tagInheritance: Boolean = false

   var discoveryClasspathFallbackEnabled: Boolean = Defaults.discoveryClasspathFallbackEnabled

   var disableTestNestedJarScanning: Boolean = Defaults.disableTestNestedJarScanning

   /**
    * Controls what to do when a duplicated test name is discovered.
    * See possible settings in [DuplicateTestNameMode].
    *
    * Defaults to [Defaults.DUPLICATE_TEST_NAME_MODE]
    */
   var duplicateTestNameMode: DuplicateTestNameMode = Defaults.DUPLICATE_TEST_NAME_MODE

   var displayFullTestPath: Boolean = sysprop(KotestEngineProperties.displayFullTestPath, Defaults.displayFullTestPath)
   /**
    * true if the test name should be the full name including parent names
    */
   var displayFullTestPath: Boolean = Defaults.DISPLAY_FULL_TEST_PATH

   var allowOutOfOrderCallbacks: Boolean = Defaults.allowOutOfOrderCallbacks

   /**
    * If set to false then private spec classes will be ignored by the test engine.
    * Defaults to false.
    */
   var ignorePrivateClasses: Boolean = Defaults.ignorePrivateClasses

   var enabledIf: EnabledIf? = null

   var enabledOrReasonIf: EnabledOrReasonIf? = null

   var retries: Int? = Defaults.defaultRetries

   var retryDelay: Duration? = Defaults.defaultRetryDelay
}

