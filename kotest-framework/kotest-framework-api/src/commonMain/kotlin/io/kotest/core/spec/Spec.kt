package io.kotest.core.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.common.KotestInternal
import io.kotest.common.SoftDeprecated
import io.kotest.core.Tag
import io.kotest.core.TestConfiguration
import io.kotest.core.Tuple2
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.factory.FactoryId
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestName
import io.kotest.core.source.SourceRef
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.core.test.config.TestCaseConfig
import io.kotest.core.test.config.TestConfig
import kotlin.js.JsName

/**
 * A [Spec] is the top most container of tests.
 *
 * It allows tests to be defined, either through DSL-methods or via annotated methods, or
 * through any user defined way by subclassing this class and implementing [rootTests].
 *
 * Test case defaults can be specified by either assignment to the settings var's or
 * by overriding the applicable setting function and returning the required value.
 *
 * For example, to set a default timeout for all tests in a spec, one can do either:
 *
 * ```
 * timeout = 100.seconds
 * ```
 *
 * or
 *
 * ```
 * override fun timeout() = 100.seconds
 * ```
 *
 * The former is useful for declaring inside an init block and the latter useful for outside.
 * They are functionally equivalent.
 *
 * Lifecycle callbacks, such as before and after test can be registered, either by defining
 * an inline lambda or by overriding the appropriate function.
 *
 * For example, to apply a before-test callback, one can do either:
 *
 * ```
 * beforeTest { println("bonjour!") }
 * ```
 *
 * or
 *
 * ```
 * override fun beforeTest() { println("bonjour!") }
 * ```
 *
 * Functions to register [AutoCloseable] instances can be found in [AutoClosing].
 *
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
    * If you wish to register an extension for all specs then register the extension
    * with project config.
    */
   open fun extensions(): List<Extension> = listOf()

   /**
    * Override this function to register instances of
    * [TestListener] which will be notified of events during
    * execution of this spec.
    *
    * If you wish to register a listener for all specs
    * then use [ProjectConfiguration.registerListener].
    */
   @SoftDeprecated("Override extensions rather than listeners. Listeners are just a type of extension. Deprecated since 5.0")
   open fun listeners(): List<TestListener> = emptyList()

   /**
    * Override this function to set default [ResolvedTestConfig] which will be applied to each
    * test case. If null, then will use project defaults.
    *
    * Any test case config set a test itself will override any value here.
    */
   @Suppress("DEPRECATION") // Remove when removing TestCaseConfig
   @Deprecated("These settings should be specified individually to provide finer grain control. Deprecated since 5.0")
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
    * Returns the timeout to be used by each test case. This value is overridden by a timeout
    * specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define a timeout, then the project
    * default is used.
    */
   open fun timeout(): Long? = null

   /**
    * Returns the invocation timeout to be used by each test case. This value is overridden by a
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
    * Sets the [AssertionMode] to be used by test cases in this spec. This value is overridden
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
    * Setting this parameter to [ProjectConfiguration.MaxConcurrency] will result in all tests of this spec
    * being launched concurrently.
    *
    * Note: This value does not change the number of threads used by the test engine. By default
    * the engine will create a single thread. To change that see [ProjectConfiguration.parallelism]. In addition,
    * all tests inside a spec will use the same dispatcher to ensure callbacks operate on the same thread.
    * To change that behavior, see [dispatcherAffinity].
    */
   @ExperimentalKotest
   open fun concurrency(): Int? = null

   /**
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky in regard to
    * the execution thread. To change this, set this value to false. This value can also be
    * set globally in [ProjectConfiguration.dispatcherAffinity].
    *
    * When this value is false, the framework is free to assign different dispatchers to different
    * root tests (nested tests always run in the same thread as their parent test).
    *
    * Note: Setting this value alone will not increase the number of threads used. For that,
    * see [ProjectConfiguration.parallelism].
    */
   @ExperimentalKotest
   open fun dispatcherAffinity(): Boolean? = null

   open fun coroutineDispatcherFactory(): CoroutineDispatcherFactory? = null

   /**
    * Returns any extensions registered via this spec that should be added to the global scope.
    */
   abstract fun globalExtensions(): List<Extension>

   @JsName("severity_js")
   var severity: TestCaseSeverityLevel? = null

   @JsName("isolation_mode_js")
   var isolationMode: IsolationMode? = null

   var failfast: Boolean? = null

   /**
    * Sets the number of tests that will be launched concurrently.
    *
    * Each test is launched into its own coroutine. This parameter determines how many test
    * coroutines are launched concurrently inside this spec.
    *
    * Setting this parameter to [ProjectConfiguration.MaxConcurrency] will result in all tests of this spec
    * being launched concurrently.
    *
    * Note: This value does not change the number of threads used by the test engine. By default
    * the engine will create a single thread. To change that see [ProjectConfiguration.parallelism]. In addition,
    * all tests inside a spec will use the same dispatcher to ensure callbacks operate on the same thread.
    * To change that behavior, see [dispatcherAffinity].
    */
   @ExperimentalKotest
   @JsName("concurrency_js")
   var concurrency: Int? = null

   /**
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky in regard to
    * the execution thread. To change this, set this value to false. This value can also be
    * set globally in [ProjectConfiguration.dispatcherAffinity].
    *
    * When this value is false, the framework is free to assign different dispatchers to different
    * root tests (nested tests always run in the same thread as their parent test).
    *
    * Note: This setting has no effect unless the number of threads is increased; see [ProjectConfiguration.parallelism].
    */
   @ExperimentalKotest
   @JsName("dispatcherAffinity_js")
   var dispatcherAffinity: Boolean? = null

   /**
    * Sets a millisecond timeout for each test case in this spec unless overridden in the test config itself.
    *
    * If this value is null, and the [SpecFunctionConfiguration.timeout] is also null, the project default will be used.
    */
   @JsName("timeout_var")
   var timeout: Long? = null

   /**
    * Sets a millisecond invocation timeout for each test case in this spec unless overridden in the test config itself.
    * If this value is null, and the [SpecFunctionConfiguration.invocationTimeout] is also null,
    * the project default will be used.
    *
    * When using a nested test style, this invocation timeout does not apply to container tests (parent tests)
    * but only leaf tests (outermost tests).
    */
   @JsName("invocationTimeout_js")
   var invocationTimeout: Long? = null

   /**
    * Sets the [TestCaseOrder] for root tests in this spec.
    * If null, then the order is defined by the project default.
    */
   var testOrder: TestCaseOrder? = null

   /**
    * When set to true, execution will switch to a dedicated thread for each test
    * case in this spec, therefore allowing the test engine to safely interrupt
    * tests via `Thread.interrupt` when they time out.
    *
    * This is useful if you are testing blocking code and want to use timeouts
    * because coroutine timeouts are cooperative by nature.
    *
    * Without setting this value, the test engine will be unable to interrupt
    * threads that are blocked.
    */
   @ExperimentalKotest
   @JsName("blockingTest_js")
   var blockingTest: Boolean? = null

   @JsName("coroutineDispatcherFactory_js")
   @ExperimentalKotest
   var coroutineDispatcherFactory: CoroutineDispatcherFactory? = null

   /**
    * If set to true then the test engine will install a
    * [`TestDispatcher`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-dispatcher/).
    * This can be retrieved via `delayController` in your tests.
    *
    * See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/index.html
    */
   @ExperimentalKotest
   @Deprecated("Replaced with coroutineTestScope. Deprecated since 5.3")
   var testCoroutineDispatcher: Boolean? = null

   var coroutineTestScope: Boolean? = null

   /**
    * Flag controlling whether virtual time is enabled for non-deterministic test functions (`eventually`, ...).
    * This is for Kotest-internal testing only and should not be used outside Kotest.
    */
   @KotestInternal
   var nonDeterministicTestVirtualTimeEnabled: Boolean = false

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
    * Set to true to enable enhanced tracing of coroutines when an error occurs.
    *
    * This value overrides the global configuration value.
    */
   var coroutineDebugProbes: Boolean? = null

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
   open suspend fun beforeSpec(spec: Spec) {}

   /**
    * Executed after the spec instance is complete.
    *
    * For non-default isolation modes, this will run for every spec instance created.
    */
   open suspend fun afterSpec(spec: Spec) {}

   /**
    * This function is invoked before every [TestCase] in this Spec.
    * Override this function to provide custom behavior.
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   open suspend fun beforeTest(testCase: TestCase) {}

   /**
    * Registers a callback to be executed before every [TestCase] in this [Spec].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   override fun beforeTest(f: BeforeTest) {
      extension(object : BeforeTestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (testCase.spec::class == this@Spec::class)
               f(testCase)
         }
      })
   }

   /**
    * Registers a callback to be executed after every [TestCase] in this [Spec].
    * This means it will be invoked for both inner and outer test blocks.
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    *
    * After-test callbacks are executed in reverse order. That is callbacks registered
    * first are executed last, which allows for nested test blocks to add callbacks that run before
    * top level callbacks.
    */
   override fun afterTest(f: AfterTest) {
      prependExtension(object : AfterTestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (testCase.spec::class == this@Spec::class)
               f(Tuple2(testCase, result))
         }
      })
   }


   /**
    * Registers a callback to be executed after all tests in this spec.
    * The spec instance is provided as a parameter.
    */
   override fun afterSpec(f: AfterSpec) {
      register(object : AfterSpecListener {
         override suspend fun afterSpec(spec: Spec) {
            if (spec::class == this@Spec::class)
               f(spec)
         }
      })
   }

   /**
    * This function is invoked after every [TestCase] in this Spec.
    * Override this function to provide custom behavior.
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   open suspend fun afterTest(testCase: TestCase, result: TestResult) {}

   open suspend fun beforeContainer(testCase: TestCase) {}

   open suspend fun afterContainer(testCase: TestCase, result: TestResult) {}

   open suspend fun beforeEach(testCase: TestCase) {}

   open suspend fun afterEach(testCase: TestCase, result: TestResult) {}

   open suspend fun beforeAny(testCase: TestCase) {}

   /**
    * This function is invoked after every [TestCase] in this Spec.
    * Override this function to provide custom behavior.
    *
    * The [TestCase] and it's [TestResult] are provided as parameters.
    */
   open suspend fun afterAny(testCase: TestCase, result: TestResult) {}
}

/**
 * A [RootTest] is a defined test that has not yet been materialized at runtime.
 * The materialization process turns a root test into a test case.
 */
data class RootTest(
   val name: TestName,
   val test: suspend TestScope.() -> Unit,
   val type: TestType,
   val source: SourceRef,
   val disabled: Boolean?, // if the test is explicitly disabled, say through an annotation or method name
   val config: TestConfig?, // if specified by the test, may be null
   val factoryId: FactoryId?, // if this root test was added from a factory
)
