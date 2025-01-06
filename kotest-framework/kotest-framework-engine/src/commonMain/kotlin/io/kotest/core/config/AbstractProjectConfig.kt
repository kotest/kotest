package io.kotest.core.config

import io.kotest.core.extensions.Extension
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.config.Defaults
import kotlin.time.Duration

/**
 * Project-wide configuration. Extensions returned by an instance of this class will be applied
 * to all [Spec]s and [TestCase][io.kotest.core.test.TestCase]s.
 *
 * Create a class that is derived from this class and place it in your source.
 * Note, on the JVM and JS, this config class can also be an object.
 *
 * It will be detected at runtime and used to configure the test engine.
 *
 * For example, you could create this object and place the source in `src/main/kotlin/my/test/package`.
 *
 * ```
 * class KotestProjectConfig : AbstractProjectConfig() {
 *    override val failOnEmptyTestSuite = true
 *    override val testCaseOrder = TestCaseOrder.Random
 * }
 * ```
 */
abstract class AbstractProjectConfig {

   /**
    * List of project wide [Extension] instances.
    */
   open fun extensions(): List<Extension> = emptyList()

   /**
    * Override this function and return an instance of [SpecExecutionOrder] which will
    * be used to sort specs before execution.
    *
    * Note: JVM ONLY
    */
   open val specExecutionOrder: SpecExecutionOrder? = null

   /**
    * The [IsolationMode] set here will be applied if the isolation mode in a spec is null.
    *
    * Note: JVM ONLY
    */
   open val isolationMode: IsolationMode? = null

   /**
    * A global timeout that is applied to all tests if not null.
    * Tests which define their own timeout will override this.
    */
   open val timeout: Duration? = null

   /**
    * A global invocation timeout that is applied to all tests if not null.
    * Tests which define their own timeout will override this.
    * The value here is in millis
    */
   open val invocationTimeout: Duration? = null

   /**
    * Set this to true and all specs will be set to fail fast, unless overriden in the spec itself.
    */
   open var projectWideFailFast: Boolean? = null

   /**
    * A timeout that is applied to the overall project if not null,
    * if the sum duration of all the tests exceeds this the suite will fail.
    */
   open val projectTimeout: Duration? = null

   /**
    * Controls which log functions on TestCase will be invoked or skipped
    */
   open val logLevel: LogLevel? = null

   open val coroutineTestScope: Boolean? = null

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
   open val testExecutionMode: TestExecutionMode? = null

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
   open val specExecutionMode: SpecExecutionMode? = null

   /**
    * When set to true, failed specs are written to a file called spec_failures.
    * This file is used on subsequent test runs to run the failed specs first.
    *
    * To enable this feature, set this to true, or set the system property
    *
    * ```properties
    * kotest.write.specfailures=true
    * ```
    *
    * Note: JVM ONLY
    */
   open val writeSpecFailureFile: Boolean? = null

   /**
    * Sets the order of top level tests in a spec.
    * The value set here will be used unless overridden in a [Spec].
    * The value in a [Spec] is always taken in preference to the value here.
    * Nested tests will always be executed in discovery order.
    *
    * If this function returns null then the default of Sequential
    * will be used.
    */
   open val testCaseOrder: TestCaseOrder? = null

   /**
    * Sets the seed that is used when randomizing specs and tests.
    * Default is null, which will use the default random instance.
    */
   open var randomOrderSeed: Long? = null

   /**
    * Override this value and set it to true if you want all tests to behave as if they
    * were operating in an [io.kotest.assertions.assertSoftly] block.
    */
   open val globalAssertSoftly: Boolean? = null

   /**
    * Override this value and set it to true if you want the build to be marked as failed
    * if there was one or more tests that were disabled/ignored.
    */
   open val failOnIgnoredTests: Boolean? = null

   /**
    * Override this value and set it to true if you want the build to be marked as failed
    * if no tests were executed.
    */
   open val failOnEmptyTestSuite: Boolean? = null

   /**
    * Override this value to set a global [AssertionMode].
    * If a spec sets an assertion mode, then the spec will override.
    */
   open val assertionMode: AssertionMode? = null

   /**
    * Some specs have DSLs that include "prefix" words in the test name.
    * For example, when using [io.kotest.core.spec.style.ExpectSpec] like this:
    *
    * ```
    * expect("this test 1") {
    *   feature("this test 2") {
    *   }
    * }
    * ```
    *
    * Will result in:
    * ```text
    * Expect: this test 1
    *   Feature: this test 2
    * ```
    * From 4.2, this feature can be disabled by setting this value to false.
    * Then the output of the previous test would be:
    * ```text
    * this test 1
    *   this test 2
    * ```
    */
   open val includeTestScopePrefixes: Boolean? = null

   /**
    * The casing of test names can be adjusted using different strategies. It affects test
    * prefixes (I.e.: Given, When, Then) and test titles.
    *
    * This setting's options are defined in [TestNameCase]. Check the previous enum for the
    * available options and examples.
    */
   open val testNameCase: TestNameCase? = null

   open val testNameRemoveWhitespace: Boolean? = null

   open val testNameAppendTags: Boolean? = null

   // Note: JVM ONLY
   open val tagInheritance: Boolean? = null

   /**
    * Controls what to do when a duplicated test name is discovered.
    * See possible settings in [DuplicateTestNameMode].
    */
   open val duplicateTestNameMode: DuplicateTestNameMode? = null

   /**
    * Set to true to enable enhanced tracing of coroutines when an error occurs.
    */
   open val coroutineDebugProbes: Boolean? = null

   /**
    * Set to false and if a spec has no active tests (all disabled due to config or tags say)
    * then the spec itself will not appear as a node in output.
    */
   open val displaySpecIfNoActiveTests: Boolean? = null

   open var displayFullTestPath: Boolean? = null

   open var allowOutOfOrderCallbacks: Boolean? = null

   /**
    * If set to false then private spec classes will be ignored by the test engine.
    */
   open var ignorePrivateClasses: Boolean? = null

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
    * If set, then this is the maximum number of times we will retry a test if it fails.
    */
   open var retries: Int? = Defaults.defaultRetries

   /**
    * If set, then this is the delay between retries.
    */
   open var retryDelay: Duration? = null

   open val minimumRuntimeTestCaseSeverityLevel: TestCaseSeverityLevel? = null

   open val severity: TestCaseSeverityLevel? = null

   open val enabledOrReasonIf: EnabledOrReasonIf? = null

   /**
    * Executed before the first test of the project, but after the
    * [ProjectListener.beforeProject] methods.
    */
   open suspend fun beforeProject() {}

   /**
    * Executed after the last test of the project, but before the
    * [ProjectListener.afterProject] methods.
    */
   open suspend fun afterProject() {}
}
