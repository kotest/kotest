package io.kotest.core

import io.kotest.core.config.Configuration
import io.kotest.common.ExperimentalKotest
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseOrder

/**
 * Defines functions which can be overriden to set configuration options at the spec level.
 * This is an alternative style to using inline functions.
 */
interface SpecFunctionConfiguration {

   /**
    * Override this function to register instances of [TestCaseExtension]
    * which will be invoked during execution of this spec.
    *
    * If you wish to register an extension for all specs
    * then use [Configuration.registerExtension].
    */
   fun extensions(): List<Extension> = listOf()

   /**
    * Override this function to register instances of
    * [TestListener] which will be notified of events during
    * execution of this spec.
    *
    * If you wish to register a listener for all specs
    * then use [Configuration.registerListener].
    */
   fun listeners(): List<TestListener> = emptyList()

   /**
    * Override this function to set default [TestCaseConfig] which will be applied to each
    * test case. If null, then will use project defaults.
    *
    * Any test case config set a test itself will override any value here.
    */
   fun defaultTestCaseConfig(): TestCaseConfig? = null

   /**
    * Returns the [IsolationMode] to be used by the test engine when running tests in this spec.
    * If null, then the project default is used.
    */
   fun isolationMode(): IsolationMode? = null

   /**
    * Sets the order of root [TestCase]s in this spec.
    * If this function returns a null value, then the project default will be used.
    */
   fun testCaseOrder(): TestCaseOrder? = null

   /**
    * Returns the timeout to be used by each test case. This value is overriden by a timeout
    * specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define a timeout, then the project
    * default is used.
    */
   fun timeout(): Long? = null

   /**
    * Returns the invocation timeout to be used by each test case. This value is overriden by a
    * value specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define an invocation timeout, then
    * the project default is used.
    */
   fun invocationTimeout(): Long? = null

   /**
    * Any tags added here will be in applied to all [TestCase]s defined in this spec
    * in additional to any defined on the individual tests themselves.
    *
    * Note: The spec instance will still need to be instantiated to retrieve these tags.
    * If you want to exclude a Spec without an instance being created, use @Tags
    * on the Spec class.
    */
   fun tags(): Set<Tag> = emptySet()

   /**
    * Sets the [AssertionMode] to be used by test cases in this spec. This value is overriden
    * by a value specified on a [TestCase] itself.
    *
    * If this value returns null, and the test case does not define a value, then the project
    * default is used.
    */
   fun assertionMode(): AssertionMode? = null

   /**
    * Sets the number of threads that will be used for executing root tests in this spec.
    *
    * On the JVM this will result in multiple threads being used.
    * On other platforms this setting will have no effect.
    */
   @Deprecated("Use concurrency setting. This Will be removed in 4.7")
   fun threads(): Int? = null

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
   fun concurrency(): Int? = null

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
   fun dispatcherAffinity(): Boolean? = null
}
