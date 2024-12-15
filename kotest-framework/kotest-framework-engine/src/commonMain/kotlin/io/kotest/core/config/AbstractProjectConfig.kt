package io.kotest.core.config

import io.kotest.common.ExperimentalKotest
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
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
   open val invocationTimeout: Long? = null

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

   /**
    * The parallelism factor determines how many threads are used to launch tests.
    *
    * The tests inside the same spec are always executed using the same thread, to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky
    * in regard to the execution thread.
    *
    * Increasing this value to `k > 1`, means that `k` threads are created, allowing different
    * specs to execute on different threads. For `n` specs, if you set this value to `k`, then
    * on average, each thread will service `n/k` specs.
    *
    * An alternative way to enable this is the system property `kotest.framework.parallelism`
    * which will always (if defined) take priority over the value here.
    *
    * Note: For backwards compatibility, setting this value to > 1 will implicitly set
    * [concurrentSpecs] to [ProjectConfiguration.MaxConcurrency] unless that option has been explicitly
    * set to another value.
    *
    * Note: JVM ONLY
    */
   open val parallelism: Int? = null

   open val coroutineTestScope: Boolean? = null

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

   @ExperimentalKotest
   // Note: JVM ONLY
   open val concurrentSpecs: Int? = null

   @ExperimentalKotest
   // Note: JVM ONLY
   open val concurrentTests: Int? = null

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

   open var dispatcherAffinity: Boolean? = null

   open var displayFullTestPath: Boolean? = null

   open var allowOutOfOrderCallbacks: Boolean? = null

   /**
    * Set to true if you wish to enable classpath scanning for test discovery if no selectors are present.
    *
    * Note: JVM ONLY
    */
   open var discoveryClasspathFallbackEnabled: Boolean? = null

   /**
    * Set to false if you wish to allow nested jar scanning for tests.
    *
    * Note: JVM ONLY
    */
   open var disableTestNestedJarScanning: Boolean? = null

   /**
    * If set to false then private spec classes will be ignored by the test engine.
    */
   open var ignorePrivateClasses: Boolean? = null

   /**
    * If set, then this is the maximum number of times we will retry a test if it fails.
    */
   open var retries: Int? = Defaults.defaultRetries

   /**
    * Similar to [retries] but allows a function to determine if we should retry based on the test case and
    * attempted retries so far.
    */
   open var retryFn: ((TestCase) -> Int)? = Defaults.defaultRetriesFn

   /**
    * If set, then this is the delay between retries.
    */
   open var retryDelay: Duration? = null

   /**
    * Similar to [retryDelay] but allows a function to determine the delay based on the number of retries.
    */
   open var retryDelayFn: ((TestCase, Int) -> Duration)? = null

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
