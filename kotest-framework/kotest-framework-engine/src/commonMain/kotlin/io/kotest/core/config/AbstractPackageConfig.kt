package io.kotest.core.config

import io.kotest.core.extensions.Extension
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.concurrency.TestExecutionMode
import kotlin.time.Duration

/**
 * Concrete implementations of this class with the name `KotestPackageConfig` will be detected at runtime and
 * used to configure the test engine for specs running in the same or a child package of that spec.
 *
 * For example, creating an instance `com.sksamuel.foo.KotestPackageConfig` will
 * mean any tests residing in packages `com.sksamuel.foo`, `com.sksamuel.foo.bar`, `com.sksamuel.foo.baz` and
 * so on will have this config applied.
 *
 * Configuration in these classes will override any global configuration, but spec and test level configuration
 * will continue to override values here. If multiple package level configurations are detected, the value in a
 * package closest to the spec package will take precedence. In other words, the order of resolutions is always:
 * test > spec > package > parent package -> parent parent package -> ... -> project.
 *
 * Only configuration values that can change after the test engine has been initialized can
 * be placed in this class. For configuration that must be set before the test engine starts,
 * use [AbstractProjectConfig].
 */
abstract class AbstractPackageConfig {

   /**
    * Returns the [IsolationMode] to be used by the test engine when running tests in this spec.
    * If null, then the project default is used.
    */
   open val isolationMode: IsolationMode? = null

   /**
    * Sets the [AssertionMode] to be used by test cases in this spec. This value is overridden
    * by a value specified on a [TestCase] itself or in a spec.
    *
    * If this value returns null, and the test case does not define a value, then the project
    * default is used.
    */
   open val assertionMode: AssertionMode? = null

   /**
    * Sets the order of root [TestCase]s in this spec.
    * If this function returns a null value, then the project default will be used.
    */
   open val testCaseOrder: TestCaseOrder? = null

   /**
    * Returns the timeout to be used by each test case. This value is overridden by a timeout
    * specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define a timeout, then the project
    * default is used.
    */
   open val timeout: Duration? = null

   /**
    * Returns the invocation timeout to be used by each test case. This value is overridden by a
    * value specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define an invocation timeout, then
    * the project default is used.
    */
   open val invocationTimeout: Duration? = null

   open val failfast: Boolean? = null

   // if set to > 0, then the test will be retried this many times in the event of a failure
   // if left to null, then the default provided by a spec or the project config will be used
   open val retries: Int? = null

   /**
    * Set to true to enable enhanced tracing of coroutines when an error occurs.
    *
    * This value overrides the global configuration value.
    */
   open val coroutineDebugProbes: Boolean? = null

   open val coroutineTestScope: Boolean? = null

   /**
    * Controls what to do when a duplicated test name is discovered.
    * See possible settings in [DuplicateTestNameMode].
    *
    * If not specified, then defaults to the global setting.
    */
   open val duplicateTestNameMode: DuplicateTestNameMode? = null

   /**
    * Whether soft assertion mode should be applied for all tests in the specs of this package.
    */
   open val assertSoftly: Boolean? = null

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
    * blocked. If you are using blocking calls in a test, setting [io.kotest.core.test.config.TestConfig.blockingTest]
    * on that test's config allows the test engine to spool up a new thread just for that test.
    */
   open val testExecutionMode: TestExecutionMode? = null

   /**
    * List of package wide [Extension] instances.
    */
   open val extensions: List<Extension> = emptyList()
}
