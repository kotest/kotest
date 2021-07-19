package io.kotest.core.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.SpecFunctionCallbacks
import io.kotest.core.SpecFunctionConfiguration
import io.kotest.core.Tag
import io.kotest.core.TestConfiguration
import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.execution.ExecutionContext
import io.kotest.core.plan.TestName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import kotlin.js.JsName

/**
 * The base class of all specs.
 *
 * Functions that can be overriden to customize spec execution are found in [SpecFunctionConfiguration].
 * Functions that can be overriden for lifecycle callbacks are found in [SpecFunctionCallbacks].
 */
abstract class Spec : TestConfiguration(), SpecFunctionConfiguration, SpecFunctionCallbacks {

   /**
    * Materializes the tests defined in this spec as [RootTest] instances.
    */
   abstract fun materializeRootTests(context: ExecutionContext): List<RootTest>

   /**
    * Returns the [TestName] for each of the currently registered top level tests.
    */
   abstract fun testNames(): List<TestName>

   @JsName("isolation_mode_js")
   var isolationMode: IsolationMode? = null

   /**
    * Sets the number of threads that will be used for executing root tests in this spec.
    *
    * On the JVM this will result in multiple threads being used.
    * On other platforms this setting will have no effect.
    */
   @JsName("threads_js")
   @Deprecated("Replaced with concurrency and parallelism. This parameter will be removed in 4.7")
   var threads: Int? = null

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
   @JsName("concurrency_var")
   var concurrency: Int? = null

   /**
    * By default, all tests inside a single spec are executed using the same dispatcher to ensure
    * that callbacks all operate on the same thread. In other words, a spec is sticky with regards
    * to the execution thread. To change this, set this value to false. This value can also be
    * set globally in [Configuration.dispatcherAffinity].
    *
    * When this value is false, the framework is free to assign different dispatchers to different
    * root tests (nested tests always run in the same thread as their parent test).
    *
    * Note: Setting this value alone will not increase the number of threads used. For that,
    * see [Configuration.parallelism].
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
    * but only leaf tests (outer most tests).
    */
   @JsName("invocationTimeout_var")
   var invocationTimeout: Long? = null

   /**
    * Sets the [TestCaseOrder] for top level tests in this spec.
    * If null, then the order is defined by the project default.
    */
   @JsName("testOrder_var")
   var testOrder: TestCaseOrder? = null

   /**
    * Returns the actual test order to use, taking into account spec config and global config.
    */
   fun declaredTestCaseOrder(): TestCaseOrder = this.testCaseOrder() ?: this.testOrder ?: configuration.testCaseOrder

   fun declaredTags(): Set<Tag> = tags() + _tags
}

fun Spec.resolvedDefaultConfig() = defaultTestCaseConfig() ?: defaultTestConfig ?: configuration.defaultTestConfig

data class RootTest(val testCase: TestCase, val order: Int)
