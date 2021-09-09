package io.kotest.core.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.SpecFunctionCallbacks
import io.kotest.core.SpecFunctionConfiguration
import io.kotest.core.Tag
import io.kotest.core.TestConfiguration
import io.kotest.core.config.Configuration
import io.kotest.core.config.configuration
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import kotlin.js.JsName

/**
 * The base class of all specs.
 *
 * Functions that can be overriden to customize spec execution are found in [SpecFunctionConfiguration].
 * Functions that can be overriden for lifecycle callbacks are found in [SpecFunctionCallbacks].
 * Functions to register [AutoCloseable] instances can be found in [AutoClosing].
 */
abstract class Spec : TestConfiguration(), SpecFunctionConfiguration, SpecFunctionCallbacks {

   /**
    * Returns the tests defined in this spec as [RootTest] instances.
    *
    * If this spec does not create the test cases upon instantiation, then this method
    * will materialize the tests (Eg when a test is defined as a function as in annotation spec).
    */
   abstract fun materializeRootTests(): List<RootTest>

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
   var timeoutInterruption: Boolean? = null

   /**
    * Returns the actual test order to use, taking into account spec config and global config.
    */
   fun declaredTestCaseOrder(): TestCaseOrder = this.testCaseOrder() ?: this.testOrder ?: configuration.testCaseOrder

   fun declaredTags(): Set<Tag> = tags() + _tags
}

fun Spec.resolvedDefaultConfig() = defaultTestCaseConfig() ?: defaultTestConfig ?: configuration.defaultTestConfig

data class RootTest(val testCase: TestCase, val order: Int)
