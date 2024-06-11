@file:Suppress("MemberVisibilityCanBePrivate")

package io.kotest.core.config

import io.kotest.common.ExperimentalKotest
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.Listener
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
 * This class defines project wide settings that are used when executing tests.
 *
 * Some settings here are fallback values. That is, a setting specified in a Spec or Test
 * will override the value here.
 */
class ProjectConfiguration {

   companion object {
      @ExperimentalKotest
      const val Sequential = 1

      @ExperimentalKotest
      const val MaxConcurrency = Int.MAX_VALUE
   }

   /**
    * Returns the [ExtensionRegistry] that contains all extensions registered through
    * this configuration instance.
    */
   val registry: ExtensionRegistry = DefaultExtensionRegistry()

   /**
    * If enabled, then all failing spec names will be written to a "failure file".
    * This file can then be used by [SpecExecutionOrder.FailureFirst].
    *
    * Defaults to [Defaults.writeSpecFailureFile].
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
    * If true, then all test cases are implicitly wrapped in an [io.kotest.assertions.assertSoftly] call.
    *
    * Defaults to [Defaults.globalAssertSoftly].
    */
   var globalAssertSoftly: Boolean = Defaults.globalAssertSoftly

   /**
    * The casing of test names can be adjusted using different strategies. It affects test
    * prefixes (I.e.: Given, When, Then) and test titles.
    *
    * This setting's options are defined in [TestNameCase].
    *
    * Defaults to [Defaults.defaultTestNameCase]
    */
   var testNameCase: TestNameCase = Defaults.defaultTestNameCase

   /**
    * If true, then the test execution will fail if any test is set to ignore.
    * If false, then ignored tests are outputted as normal.
    *
    * Defaults to [Defaults.failOnIgnoredTests].
    */
   var failOnIgnoredTests: Boolean = Defaults.failOnIgnoredTests

   /**
    * Returns the default assertion mode.
    */
   var assertionMode: AssertionMode = Defaults.assertionMode

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
    * [ProjectConfiguration.concurrentSpecs] to the k unless that value has also been set.
    *
    * Defaults to [Defaults.parallelism].
    */
   var parallelism: Int = Defaults.parallelism

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
   @ExperimentalKotest
   var dispatcherAffinity: Boolean = Defaults.dispatcherAffinity

   /**
    * Each spec is launched into its own coroutine. By default, the test engine waits for that
    * coroutine to finish before launching the next spec. By setting [ProjectConfiguration.concurrentSpecs]
    * to a value higher than 1, multiple specs will be launched at the same time.
    *
    * For example, setting this value to 5 will result in 5 specs running concurrently. Once
    * one of those specs completes, another will be launched (if any are remaining), and so on.
    *
    * Setting this value to [ProjectConfiguration.MaxConcurrency] will result in all specs being launched together.
    *
    * The maximum number of coroutines that can be launched at any time is given
    * by [ProjectConfiguration.concurrentSpecs] * [ProjectConfiguration.concurrentTests].
    *
    * Tests inside each spec will continue to be launched sequentially. To change that
    * see [ProjectConfiguration.concurrentTests].
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. See [ProjectConfiguration.parallelism].
    *
    * Note: This setting can be > 1 and specs can still choose to "opt out" by using the
    * [io.kotest.core.annotation.Isolate] annotation. That annotation ensures that a spec never runs concurrently
    * with any other regardless of the setting here.
    */
   @ExperimentalKotest
   var concurrentSpecs: Int? = null

   /**
    * Each root test is launched into its own coroutine. By default, the test engine waits
    * for that coroutine to finish before launching the next test of the same spec. By setting
    * [ProjectConfiguration.concurrentTests] to a value higher than 1, multiple tests will be launched
    * at the same time.
    *
    * For example, setting this value to 5 will result in at most 5 tests running concurrently per spec.
    * Once one of those tests completes, another will be launched (if there are further tests in the spec),
    * and so on.
    *
    * Setting this value to [ProjectConfiguration.MaxConcurrency] will result in all tests of a spec being
    * launched together when the spec is instantiated
    *
    * Setting this value will result in tests inside a spec executing concurrently, but will not change
    * how many specs are launched concurrently. For that, see [ProjectConfiguration.concurrentSpecs].
    *
    * The maximum number of coroutines that can be launched at any time is given
    * by [ProjectConfiguration.concurrentSpecs] * [ProjectConfiguration.concurrentTests].
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. See [ProjectConfiguration.parallelism].
    */
   @ExperimentalKotest
   var concurrentTests: Int = Defaults.concurrentTests

   /**
    * Returns the timeout for the execution of a test case in milliseconds.
    *
    * Note: This timeout includes the time required to executed nested tests.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.defaultTimeoutMillis].
    */
   var timeout: Long? = null

   /**
    * Returns the timeout for any single invocation of a test.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.defaultInvocationTimeoutMillis].
    */
   var invocationTimeout: Long? = null

   /**
    * A timeout that is applied to the overall project if not null,
    * if the sum duration of all the tests exceeds this the suite will fail.
    */
   var projectTimeout: Duration? = null

   /**
    * Controls which log functions on TestCase will be invoked or skipped
    */
   var logLevel: LogLevel = LogLevel.Off

   var failfast: Boolean = Defaults.failfast

   var projectWideFailFast: Boolean = Defaults.projectWideFailFast

   var blockingTest: Boolean = Defaults.blockingTest

   var severity: TestCaseSeverityLevel = Defaults.severity

   /**
    * If set to true then the test engine will install a
    * [`TestDispatcher`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-dispatcher/).
    *
    * This can be retrieved via `delayController` in your tests.
    *
    * See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/index.html
    */
   @ExperimentalKotest
   @Deprecated("Replaced with coroutineTestScope. Deprecated since 5.3")
   var testCoroutineDispatcher: Boolean = Defaults.testCoroutineDispatcher

   var coroutineTestScope: Boolean = Defaults.coroutineTestScope

   /**
    * Contains the default [ResolvedTestConfig] to be used by tests when not specified in either
    * the test, the spec, or the test factory.
    *
    * Defaults to [Defaults.testCaseConfig]
    */
   @Suppress("DEPRECATION") // Remove when removing legacy option
   @Deprecated("These settings can be specified individually to provide finer grain control. Deprecated since 5.0")
   var defaultTestConfig: TestCaseConfig = Defaults.testCaseConfig

   /**
    * If set to true, then will cause the test suite to fail if there were no executed tests.
    */
   var failOnEmptyTestSuite: Boolean = Defaults.failOnEmptyTestSuite

   /**
    * Set to true to enable enhanced tracing of coroutines when an error occurs.
    *
    * Defaults to [Defaults.coroutineDebugProbes]
    */
   var coroutineDebugProbes: Boolean = Defaults.coroutineDebugProbes

   /**
    * Some specs have DSLs that include prefix or suffix words in the test name.
    *
    * If this method returns true, then test names include those prefix and suffix names
    * in reports and the IDE.
    *
    * For example, when using ExpectSpec like this:
    *
    * ```
    * expect("this test 1") {
    *   feature("this test 2") {
    *   }
    * }
    * ```
    *
    * If prefixes are enabled, the output would be:
    *
    * ```text
    * Expect: this test 1
    *   Feature: this test 2
    * ```
    *
    * And if disabled, the output would be:
    *
    * ```text
    * this test 1
    *    test this 2
    * ```
    *
    * Defaults to `null`, which is to let the [Spec] style determine whether to include affixes.
    */
   var includeTestScopeAffixes: Boolean? = Defaults.defaultIncludeTestScopeAffixes

   /**
    * Set to false and if a spec has no active tests (all disabled due to config or tags say)
    * then the spec itself will not appear as a node in output.
    *
    * Defaults to [Defaults.displaySpecIfNoActiveTests]
    *
    * Note: This only works for JUnit and IntelliJ runners.
    */
   var displaySpecIfNoActiveTests: Boolean = Defaults.displaySpecIfNoActiveTests

   /**
    * Controls the default [IsolationMode] that each spec will execute in.
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Default to [Defaults.isolationMode]
    */
   var isolationMode: IsolationMode = Defaults.isolationMode

   /**
    * Controls the ordering of root test cases in each spec.
    *
    * Valid options are the enum values of [TestCaseOrder].
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Defaults to [Defaults.testCaseOrder]
    */
   var testCaseOrder: TestCaseOrder = Defaults.testCaseOrder

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

   var disableTestNestedJarScanning: Boolean = Defaults.disableTestNestedJarScanning

   /**
    * Controls what to do when a duplicated test name is discovered.
    * See possible settings in [DuplicateTestNameMode].
    *
    * Defaults to [Defaults.duplicateTestNameMode]
    */
   var duplicateTestNameMode: DuplicateTestNameMode = Defaults.duplicateTestNameMode

   var displayFullTestPath: Boolean = Defaults.displayFullTestPath

   var allowOutOfOrderCallbacks: Boolean = Defaults.allowOutOfOrderCallbacks

   @Suppress("DEPRECATION") // Remove when removing legacy option
   var threads: Int = defaultTestConfig.threads

   @Suppress("DEPRECATION") // Remove when removing legacy option
   var invocations: Int = defaultTestConfig.invocations

   /**
    * Returns all globally registered [Listener]s.
    */
   @Deprecated("Listeners have been subsumed into extensions", level = DeprecationLevel.ERROR)
   fun listeners(): Nothing = throw UnsupportedOperationException()

   @Deprecated("Use registry. Deprecated since 5.0")
   fun extensions(): List<Extension> = registry.all()
}
