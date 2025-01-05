package io.kotest.core.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.common.KotestInternal
import io.kotest.core.Tag
import io.kotest.core.TestConfiguration
import io.kotest.core.Tuple2
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.factory.FactoryId
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeTestListener
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
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.coroutines.CoroutineDispatcherFactory
import kotlin.js.JsName
import kotlin.time.Duration

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
    * Returns the [IsolationMode] to be used by the test engine when running tests in this spec.
    * If null, then the project default is used.
    */
   open fun isolationMode(): IsolationMode? = null

   /**
    * Sets the [IsolationMode] to be used by the test engine when running tests in this spec.
    * If null, then the project default is used.
    */
   @JsName("isolation_mode_js")
   var isolationMode: IsolationMode? = null

   /**
    * Sets the [TestCaseOrder] for root tests in this spec.
    * If null, then the project default is used.
    */
   open fun testCaseOrder(): TestCaseOrder? = null

   /**
    * Sets the [TestCaseOrder] for root tests in this spec.
    * If null, then the project default is used.
    */
   var testOrder: TestCaseOrder? = null

   /**
    * Returns the timeout to be used by each test case. This value is overridden by a timeout
    * specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define a timeout, then the project
    * default is used.
    */
   open fun timeout(): Long? = null

   /**
    * Sets a millisecond timeout for each test case in this spec unless overridden in the test config itself.
    * If this value is null, the project default will be used.
    */
   @JsName("timeout_var")
   var timeout: Long? = null

   /**
    * Sets the invocation timeout to be used by each test case, in milliseconds. This value is overridden by a
    * value specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define an invocation timeout, then
    * the project default is used.
    */
   open fun invocationTimeout(): Long? = null

   /**
    * Sets the invocation timeout to be used by each test case, in milliseconds. This value is overridden by a
    * value specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define an invocation timeout, then
    * the project default is used.
    */
   @JsName("invocationTimeout_js")
   var invocationTimeout: Long? = null

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
    * Each test is launched into its own coroutine. By default, the test engine waits for that
    * test to finish before launching the next test. By setting [testExecutionMode]
    * to [TestExecutionMode.Concurrent] all root tests will be launched at the same time.
    *
    * Setting this value to [TestExecutionMode.LimitedConcurrency] allows you to specify how
    * many root tests should be launched concurrently.
    *
    * Note: This value does not change the number of threads used by the test engine. If a test uses a
    * blocking method, then that thread cannot be utilized by another coroutine while the thread is
    * blocked. If you are using blocking calls in a test, setting [blockingTest] on that test's config
    * allows the test engine to spool up a new thread just for that test.
    */
   @ExperimentalKotest
   open fun testExecutionMode(): TestExecutionMode? = null

   @ExperimentalKotest
   @JsName("testExecutionMode_js")
   var testExecutionMode: TestExecutionMode? = null

   /**
    * Returns any extensions registered via this spec that should be added to the global scope.
    */
   abstract fun globalExtensions(): List<Extension>

   @JsName("severity_js")
   var severity: TestCaseSeverityLevel? = null

   /**
    * Marks all tests in this spec as fail fast.
    * So any test failure will fail any remaining tests in this spec, at any nested level
    */
   var failfast: Boolean? = null

   // if set to > 0, then the test will be retried this many times in the event of a failure
   // if left to null, then the default provided by the project config will be used
   var retries: Int? = null

   var retryDelay: Duration? = null

   open fun coroutineDispatcherFactory(): CoroutineDispatcherFactory? = null

   @JsName("coroutineDispatcherFactory_js")
   var coroutineDispatcherFactory: CoroutineDispatcherFactory? = null

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

   var coroutineTestScope: Boolean? = null

   /**
    * Flag controlling whether virtual time is enabled for non-deterministic test functions (`eventually`, ...).
    * This is for Kotest-internal testing only and should not be used outside Kotest.
    */
   @KotestInternal
   var nonDeterministicTestVirtualTimeEnabled: Boolean = false

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


   /**
    * Registers a callback to be executed after every [TestCase] with type [TestType.Test].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
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
   val config: TestConfig?, // if specified by the test, may be null if no config is set using the spec DSL
   val factoryId: FactoryId?, // if this root test was added from a factory
)
