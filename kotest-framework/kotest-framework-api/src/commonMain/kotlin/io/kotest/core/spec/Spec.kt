package io.kotest.core.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.common.SoftDeprecated
import io.kotest.core.SourceRef
import io.kotest.core.Tag
import io.kotest.core.TestConfiguration
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestName
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ConfigurableTestConfig
import io.kotest.core.test.config.TestCaseConfig
import kotlin.js.JsName

/**
 * A [Spec] is the top most container of tests.
 *
 * It allows tests to be defined, either through DSL-methods or via declaring functions, or
 * through a user defined way by subclassing this class.
 *
 * A Spec allows test defaults to be specified - either inline by assignment to var's, or
 * by function overrides.
 *
 * Lifecycle callbacks can be registered, either by lambda accepting functions or
 * by function overrides.
 *
 * Functions to register [AutoCloseable] instances can be found in [AutoClosing].
 */
abstract class Spec : TestConfiguration() {

   /**
    * Returns the [RootTest]s that are defined by this spec.
    */
   abstract fun rootTests(): List<RootTest>

   /**
    * Override this function to register instances of [TestCaseExtension]
    * which will be invoked during execution of this spec.
    *
    * If you wish to register an extension for all specs
    * then use [Configuration.registerExtension].
    */
   open fun extensions(): List<Extension> = listOf()

   /**
    * Override this function to register instances of
    * [TestListener] which will be notified of events during
    * execution of this spec.
    *
    * If you wish to register a listener for all specs
    * then use [Configuration.registerListener].
    */
   @SoftDeprecated("Use extensions")
   open fun listeners(): List<TestListener> = emptyList()

   /**
    * Override this function to set default [TestCaseConfig] which will be applied to each
    * test case. If null, then will use project defaults.
    *
    * Any test case config set a test itself will override any value here.
    */
   open fun defaultTestCaseConfig(): TestCaseConfig? = null

   /**
    * Returns the [IsolationMode] to be used by the test engine when running tests in this spec.
    * If null, then the project default is used.
    */
   open fun isolationMode(): IsolationMode? = null

   /**
    * Sets the order of root [TestCase]s in this spec.
    * If this function returns a null value, then the project default will be used.
    */
   open fun testCaseOrder(): TestCaseOrder? = null

   /**
    * Returns the timeout to be used by each test case. This value is overriden by a timeout
    * specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define a timeout, then the project
    * default is used.
    */
   open fun timeout(): Long? = null

   /**
    * Returns the invocation timeout to be used by each test case. This value is overriden by a
    * value specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define an invocation timeout, then
    * the project default is used.
    */
   open fun invocationTimeout(): Long? = null

   /**
    * Any tags added here will be in applied to all [TestCase]s defined in this spec
    * in additional to any defined on the individual tests themselves.
    *
    * Note: The spec instance will still need to be instantiated to retrieve these tags.
    * If you want to exclude a Spec without an instance being created, use @Tags
    * on the Spec class.
    */
   open fun tags(): Set<Tag> = emptySet()

   /**
    * Sets the [AssertionMode] to be used by test cases in this spec. This value is overriden
    * by a value specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define a value, then the project
    * default is used.
    */
   open fun assertionMode(): AssertionMode? = null

   /**
    * Sets the number of threads that will be used for executing root tests in this spec.
    *
    * By setting this a value, a [CoroutineDispatcherFactory] will be installed for this spec
    * that shares a fixed number of threads for this spec only. If the [coroutineDispatcherFactory]
    * is also set, then that will have precedence.
    *
    * This setting is JVM only.
    */
   open fun threads(): Int? = null

   /**
    * Sets the number of tests that will be launched concurrently.
    *
    * Each test is launched into its own coroutine. This parameter determines how many test
    * coroutines are launched concurrently inside of this spec.
    *
    * Setting this parameter to [Configuration.MaxConcurrency] will result in all tests of this spec
    * being launched concurrently.
    *
    * Note: This value does not change the number of threads used by the test engine. By default
    * the engine will create a single thread. To change that see [Configuration.parallelism]. In addition,
    * all tests inside a spec will use the same dispatcher to ensure callbacks operate on the same thread.
    * To change that behavior, see [dispatcherAffinity].
    */
   @ExperimentalKotest
   open fun concurrency(): Int? = null

   /**
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky in regard to
    * the execution thread. To change this, set this value to false. This value can also be
    * set globally in [Configuration.dispatcherAffinity].
    *
    * When this value is false, the framework is free to assign different dispatchers to different
    * root tests (nested tests always run in the same thread as their parent test).
    *
    * Note: Setting this value alone will not increase the number of threads used. For that,
    * see [Configuration.parallelism].
    */
   @ExperimentalKotest
   open fun dispatcherAffinity(): Boolean? = null

   open fun coroutineDispatcherFactory(): CoroutineDispatcherFactory? = null

   /**
    * Returns any extensions registered via this spec that should be added to the global scope.
    */
   abstract fun globalExtensions(): List<Extension>

   @JsName("isolation_mode_js")
   var isolationMode: IsolationMode? = null

   /**
    * Sets the number of tests that will be launched concurrently.
    *
    * Each test is launched into its own coroutine. This parameter determines how many test
    * coroutines are launched concurrently inside this spec.
    *
    * Setting this parameter to [Configuration.MaxConcurrency] will result in all tests of this spec
    * being launched concurrently.
    *
    * Note: This value does not change the number of threads used by the test engine. By default
    * the engine will create a single thread. To change that see [Configuration.parallelism]. In addition,
    * all tests inside a spec will use the same dispatcher to ensure callbacks operate on the same thread.
    * To change that behavior, see [dispatcherAffinity].
    */
   @ExperimentalKotest
   @JsName("concurrency_var")
   var concurrency: Int? = null

   /**
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky in regard to
    * the execution thread. To change this, set this value to false. This value can also be
    * set globally in [Configuration.dispatcherAffinity].
    *
    * When this value is false, the framework is free to assign different dispatchers to different
    * root tests (nested tests always run in the same thread as their parent test).
    *
    * Note: This setting has no effect unless the number of threads is increasd; see [Configuration.parallelism].
    */
   @ExperimentalKotest
   @JsName("dispatcherAffinity_var")
   var dispatcherAffinity: Boolean? = null

   /**
    * Sets a millisecond timeout for each test case in this spec unless overriden in the test config itself.
    *
    * If this value is null, and the [SpecFunctionConfiguration.timeout] is also null, the project default will be used.
    */
   @JsName("timeout_var")
   var timeout: Long? = null

   /**
    * Sets a millisecond invocation timeout for each test case in this spec unless overriden in the test config itself.
    * If this value is null, and the [SpecFunctionConfiguration.invocationTimeout] is also null,
    * the project default will be used.
    *
    * When using a nested test style, this invocation timeout does not apply to container tests (parent tests)
    * but only leaf tests (outermost tests).
    */
   @JsName("invocationTimeout_var")
   var invocationTimeout: Long? = null

   /**
    * Sets the [TestCaseOrder] for root tests in this spec.
    * If null, then the order is defined by the project default.
    */
   var testOrder: TestCaseOrder? = null

   // When set to true, execution will switch to a dedicated thread for each test case in this spec,
   // therefore allowing the test engine to safely interrupt tests via Thread.interrupt when they time out.
   // This is useful if you are testing blocking code and want to use timeouts because coroutine timeouts
   // are cooperative by nature.
   var blockingTest: Boolean? = null

   @JsName("coroutineDispatcherFactory_var")
   @ExperimentalKotest
   var coroutineDispatcherFactory: CoroutineDispatcherFactory? = null

   /**
    * If set to true then the test engine will install a [TestCoroutineDispatcher].
    * This can be retrieved via `delayController` in your tests.
    * @see https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/index.html
    */
   @ExperimentalKotest
   var testCoroutineDispatcher: Boolean? = null

   /**
    * Sets the number of threads that will be used for executing root tests in this spec.
    *
    * By setting this a value, a [CoroutineDispatcherFactory] will be installed for this spec
    * that shares a fixed number of threads for this spec only. If the [coroutineDispatcherFactory]
    * is also set, then that will have precedence.
    *
    * This setting is JVM only.
    */
   @JsName("threads_var")
   var threads: Int? = null

   /**
    * Controls what to do when a duplicated test name is discovered.
    * See possible settings in [DuplicateTestNameMode].
    *
    * If not specified, then defaults to the global setting.
    */
   var duplicateTestNameMode: DuplicateTestNameMode? = null

   /**
    * Executed before any tests are invoked on this spec instance.
    *
    * For non-default isolation modes, this will run for every spec instance created.
    */
   open fun beforeSpec(spec: Spec) {}

   /**
    * Executed after the spec instance is complete.
    *
    * For non-default isolation modes, this will run for every spec instance created.
    */
   open fun afterSpec(spec: Spec) {}

   /**
    * This function is invoked before every [TestCase] in this Spec.
    * Override this function to provide custom behavior.
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   open fun beforeTest(testCase: TestCase) {}

   /**
    * This function is invoked after every [TestCase] in this Spec.
    * Override this function to provide custom behavior.
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   open fun afterTest(testCase: TestCase, result: TestResult) {}

   open fun beforeContainer(testCase: TestCase) {}

   open fun afterContainer(testCase: TestCase, result: TestResult) {}

   open fun beforeEach(testCase: TestCase) {}

   open fun afterEach(testCase: TestCase, result: TestResult) {}

   open fun beforeAny(testCase: TestCase) {}

   /**
    * This function is invoked after every [TestCase] in this Spec.
    * Override this function to provide custom behavior.
    *
    * The [TestCase] and it's [TestResult] are provided as parameters.
    */
   open fun afterAny(testCase: TestCase, result: TestResult) {}

   fun declaredTags(): Set<Tag> = tags() + _tags
}

/**
 * A [RootTest] is a defined test that has not yet been materialized at runtime.
 * The materialization process turns a root test into a test case.
 */
data class RootTest(
   val name: TestName,
   val test: suspend TestContext.() -> Unit,
   val type: TestType,
   val source: SourceRef,
   val disabled: Boolean?, // if the test is explicitly disabled, say through an annotation or method name
   val config: ConfigurableTestConfig?, // if specified by the test, may be null
)
