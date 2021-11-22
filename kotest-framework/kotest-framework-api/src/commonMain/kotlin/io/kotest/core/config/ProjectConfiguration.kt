package io.kotest.core.config

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.core.test.config.TestCaseConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Duration

/**
 * An immutable configuration class used once the Engine has been initialized.
 */
data class ProjectConfiguration(

   val extensions: ExtensionRegistry,

   /**
    * If enabled, then all failing spec names will be written to a "failure file".
    * This file can then be used by [SpecExecutionOrder.FailureFirst].
    *
    * Defaults to [Defaults.writeSpecFailureFile].
    *
    * Note: Only has an effect on the JVM.
    */
   val writeSpecFailureFile: Boolean,

   /**
    * The path to write the failed spec list to, if enabled.
    *
    * Defaults to [Defaults.specFailureFilePath].
    *
    * Note: Only has an effect on the JVM.
    */
   val specFailureFilePath: String,

   /**
    * If true, then all test cases are implicitly wrapped in an assertSoftly call.
    *
    * Defaults to [Defaults.globalAssertSoftly].
    */
   val globalAssertSoftly: Boolean,

   /**
    * The casing of the tests' names can be adjusted using different strategies. It affects tests'
    * prefixes (I.e.: Given, When, Then) and tests' titles.
    *
    * This setting's options are defined in [TestNameCase].
    *
    * Defaults to [Defaults.defaultTestNameCase]
    */
   val testNameCase: TestNameCase,

   /**
    * If true, then the test execution will fail if any test is set to ignore.
    * If false, then ignored tests are outputted as normal.
    *
    * Defaults to [Defaults.failOnIgnoredTests].
    */
   val failOnIgnoredTests: Boolean,

   /**
    * Returns the default assertion mode.
    */
   val assertionMode: AssertionMode,

   /**
    * The parallelism factor determines how many threads are used to execute specs and tests.
    *
    * By default, a single threaded [CoroutineDispatcher] is used for all tests.
    *
    * Increasing this value to k > 1, means that k dispatchers are created, allowing different
    * specs to execute on different dispatchers (each backed by a separate thread).
    *
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky with regards
    * to the execution thread. To change that, specify [dispatcherAffinity] globally or per spec.
    *
    * An alternative way to set this value is via system property kotest.framework.parallelism
    * which will always (if defined) take priority over the value here.
    *
    * Note: For backwards compatibility, setting this value to k will implicitly set
    * [MutableConfiguration.concurrentSpecs] to the k unless that value has also been set.
    *
    * Defaults to [Defaults.parallelism].
    */
   val parallelism: Int,

   /**
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky in regard to
    * the execution thread. To change this, set this value to false.
    *
    * When this value is false, the framework is free to assign different dispatchers to different
    * root tests (nested tests always run in the same thread as their parent test).
    *
    * Note: This setting has no effect unless the number of threads is increasd; see [parallelism].
    *
    * Defaults to [Defaults.dispatcherAffinity].
    */
   val dispatcherAffinity: Boolean,

   /**
    * Each spec is launched into its own coroutine. By default, the test engine waits for that
    * coroutine to finish before launching the next spec. By setting [MutableConfiguration.concurrentSpecs]
    * to a value higher than 1, multiple specs will be launched at the same time.
    *
    * For example, setting this value to 5 will result in 5 specs running concurrently. Once
    * one of those specs completes, another will be launched (if any are remaining), and so on.
    *
    * Setting this value to [MutableConfiguration.MaxConcurrency] will result in all specs being launched together.
    *
    * The maximum number of coroutines that can be launched at any time is given
    * by [MutableConfiguration.concurrentSpecs] * [MutableConfiguration.concurrentTests].
    *
    * Tests inside each spec will continue to be launched sequentially. To change that
    * see [MutableConfiguration.concurrentTests].
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. See [MutableConfiguration.parallelism].
    *
    * Note: This setting can be > 1 and specs can still choose to "opt out" by using the
    * [io.kotest.core.annotation.Isolate] annotation. That annotation ensures that a spec never runs concurrently
    * with any other regardless of the setting here.
    */
   val concurrentSpecs: Int,

   /**
    * Each root test is launched into its own coroutine. By default, the test engine waits
    * for that coroutine to finish before launching the next test of the same spec. By setting
    * [MutableConfiguration.concurrentTests] to a value higher than 1, multiple tests will be launched
    * at the same time.
    *
    * For example, setting this value to 5 will result in at most 5 tests running concurrently per spec.
    * Once one of those tests completes, another will be launched (if there are further tests in the spec),
    * and so on.
    *
    * Setting this value to [MutableConfiguration.MaxConcurrency] will result in all tests of a spec being
    * launched together when the spec is instantiated
    *
    * Setting this value will result in tests inside a spec executing concurrently, but will not change
    * how many specs are launched concurrently. For that, see [MutableConfiguration.concurrentSpecs].
    *
    * The maximum number of coroutines that can be launched at any time is given
    * by [MutableConfiguration.concurrentSpecs] * [MutableConfiguration.concurrentTests].
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. See [MutableConfiguration.parallelism].
    */
   val concurrentTests: Int,

   /**
    * Returns the timeout for the execution of a test case in milliseconds.
    *
    * Note: This timeout includes the time required to executed nested tests.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.defaultTimeoutInMillis].
    */
   val timeout: Duration,

   /**
    * Returns the timeout for any single invocation of a test.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.defaultInvocationTimeoutInMillis].
    */
   val invocationTimeout: Duration,

   /**
    * A timeout that is applied to the overall project if not null.
    *
    * If the execution time of all tests exceeds this value then the test suite will fail.
    */
   val projectTimeout: Duration?,

   /**
    * Controls which log functions on TestCase will be invoked or skipped
    */
   val logLevel: LogLevel,

   /**
    * If set to true then the test engine will install a [TestCoroutineDispatcher].
    *
    * This can be retrieved via `delayController` in your tests.
    *
    * @see https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/index.html
    */
   val testCoroutineDispatcher: Boolean,

   /**
    * Contains the default [ResolvedTestConfig] to be used by tests when not specified in either
    * the test, the spec, or the test factory.
    *
    * Defaults to [Defaults.testCaseConfig]
    */
   val defaultTestConfig: TestCaseConfig,

   /**
    * If set to true, then will cause the test suite to fail if there were no executed tests.
    */
   val failOnEmptyTestSuite: Boolean,

   /**
    * Set to true to enable enhanced tracing of coroutines when an error occurs.
    *
    * Defaults to [Defaults.coroutineDebugProbes]
    */
   val coroutineDebugProbes: Boolean,

   /**
    * Some specs have DSLs that include prefix or suffix words in the test name.
    *
    * If this method returns true, then test names include those prefix and suffix names
    * in reports and the IDE.
    *
    * For example, when using ExpectSpec like this:
    *
    * expect("this test 1") {
    *   feature("this test 2") {
    *   }
    * }
    *
    * If prefixes are enabled, the output would be:
    *
    * Expect: this test 1
    *   Feature: this test 2
    *
    * And if disabled, the output would be:
    *
    * this test 1
    *    test this 2
    *
    * Defaults to null, which is to use the default.
    */
   val includeTestScopeAffixes: Boolean,

   /**
    * Set to false and if a spec has no active tests (all disabled due to config or tags say)
    * then the spec itself will not appear as a node in output.
    *
    * Defaults to [Defaults.displaySpecIfNoActiveTests]
    *
    * Note: This only works for JUnit and Intellij runners.
    */
   val displaySpecIfNoActiveTests: Boolean,

   /**
    * Controls the default [IsolationMode] that each spec will execute in.
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Default to [Defaults.isolationMode]
    */
   val isolationMode: IsolationMode,

   /**
    * Controls the ordering of root test cases in each spec.
    *
    * Valid options are the enum values of [TestCaseOrder].
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Defaults to [Defaults.testCaseOrder]
    */
   val testCaseOrder: TestCaseOrder,

   /**
    * Returns the sort order to use when executing specs.
    *
    * Defaults to [Defaults.specExecutionOrder]
    */
   val specExecutionOrder: SpecExecutionOrder,
   val removeTestNameWhitespace: Boolean,
   val testNameAppendTags: Boolean,

   /**
    * Controls what to do when a duplicated test name is discovered.
    * See possible settings in [DuplicateTestNameMode].
    *
    * Defaults to [Defaults.duplicateTestNameMode]
    */
   val duplicateTestNameMode: DuplicateTestNameMode,

   val displayFullTestPath: Boolean,

   var failfast: Boolean,

   var blockingTest: Boolean,

   var severity: TestCaseSeverityLevel,
)
