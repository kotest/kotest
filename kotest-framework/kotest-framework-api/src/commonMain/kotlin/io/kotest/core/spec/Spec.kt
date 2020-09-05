package io.kotest.core.spec

import io.kotest.core.SpecFunctionCallbacks
import io.kotest.core.SpecFunctionConfiguration
import io.kotest.core.Tag
import io.kotest.core.TestConfiguration
import io.kotest.core.config.configuration
import io.kotest.core.js.JsTest
import io.kotest.core.js.useKotlinJs
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import kotlin.js.JsName

/**
 * This is needed to initialize the JS support for Kotest
 */
@Suppress("unused")
val initKotlinJsSupport = useKotlinJs()

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

   @JsName("isolation_js")
   @Deprecated("Use isolationMode. This will be removed in 4.4")
   var isolation: IsolationMode? = null

   @JsName("isolation_mode_js")
   var isolationMode: IsolationMode? = null

   /**
    * Sets the number of root test cases that can be executed concurrently in this spec.
    * On the JVM this will result in multiple threads being used.
    * On other platforms this setting will have no effect.
    */
   @JsName("threads_js")
   var threads: Int? = null

   /**
    * Sets a timeout for each test case in this spec unless overriden in the test config itself.
    * If this value is null, and the [SpecFunctionConfiguration.timeout] is also null, the project default will be used.
    */
   @JsName("timeout_var")
   var timeout: Long? = null

   /**
    * Sets an invocation timeout for each test case in this spec unless overriden in the test config itself.
    * If this value is null, and the [SpecFunctionConfiguration.invocationTimeout] is also null,
    * the project default will be used.
    */
   @JsName("invocationTimeout_var")
   var invocationTimeout: Long? = null

   /**
    * Sets the [TestCaseOrder] for root tests in this spec.
    * If null, then the order is defined by the project default.
    */
   var testOrder: TestCaseOrder? = null

   /**
    * Returns the actual test order to use, taking into account spec config and global config.
    */
   fun declaredTestCaseOrder(): TestCaseOrder = this.testCaseOrder() ?: this.testOrder ?: configuration.testCaseOrder

   fun declaredTags(): Set<Tag> = tags() + _tags

   /**
    * The annotation [JsTest] is intercepted by the kotlin.js compiler and invoked in the generated
    * javascript code. If the kotest framework adapter is installed, this will intercept the call
    * and invokes the tests using the kotest engine.
    *
    * Kotest automatically installs a Javascript test-adapter to intercept calls to all tests and when
    * this test is invoked, avoids passing it to the underlying javascript test framework. Instead it
    * invokes the tests using the Kotest engine.
    */
   @JsTest
   fun kotestJavascript() = this
}

fun Spec.resolvedDefaultConfig() = defaultTestCaseConfig() ?: defaultTestConfig ?: configuration.defaultTestConfig

data class RootTest(val testCase: TestCase, val order: Int)
