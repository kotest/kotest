package io.kotest.core.config

import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.filter.Filter
import io.kotest.core.listeners.Listener
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestNameCase

/**
 * The global configuration singleton.
 */
val configuration = Configuration()

/**
 * This class defines project wide settings that are used when executing tests.
 *
 * Some settings here are fallback values. That is, a setting specified in a Spec or Test
 * will override the value here.
 */
class Configuration {

   private val listeners = mutableListOf<Listener>()
   private val filters = mutableListOf<Filter>()
   private val extensions = mutableListOf<Extension>()

   /**
    * If enabled, then all failing spec names will be written to a "failure file".
    * This file can then be used by [SpecExecutionOrder.FailureFirst].
    *
    * Defaults to [Defaults.writeSpecFailureFile]
    */
   var writeSpecFailureFile: Boolean = Defaults.writeSpecFailureFile

   /**
    * The path to write the failed spec list to, if enabled.
    */
   var specFailureFilePath: String = Defaults.specFailureFilePath

   /**
    * If true, then all test cases are implicitly wrapped in an assertSoftly call.
    *
    * Defaults to [Defaults.globalAssertSoftly].
    */
   var globalAssertSoftly: Boolean = Defaults.globalAssertSoftly

   /**
    * The casing of the tests' names can be adjusted using different strategies. It affects tests'
    * prefixes (I.e.: Given, When, Then) and tests' titles.
    *
    * This setting's options are defined in [TestNameCase].
    *
    * Defaults to [Defaults.defaultTestNameCase]
    */
   var testNameCase: TestNameCase = Defaults.defaultTestNameCase

   /**
    * If true, then the test execution will fail if any test is set to ignore.
    * If false, then ignored tests are outputted as normal.
    *
    * Defaults to [Defaults.failOnIgnoredTests].
    */
   var failOnIgnoredTests: Boolean = Defaults.failOnIgnoredTests

   /**
    * Returns the default assertion mode.
    */
   var assertionMode: AssertionMode = Defaults.assertionMode

   /**
    * Returns the parallelization factor for executing specs.
    * If 1 then all specs are executed in isolation (sequentially).
    * If larger than 1, then k specs will be executed in parallel.
    * Any specs that should be executed in isolation should be marked with @DoNotParallelize.
    *
    * Defaults to [Defaults.parallelism].
    */
   var parallelism: Int = Defaults.parallelism

   /**
    * Returns the timeout for the execution of a test case.
    * Note: This timeout includes the time required to executed nested tests.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.defaultTimeoutInMillis].
    */
   var timeout: Long = Defaults.defaultTimeoutInMillis

   /**
    * Returns the timeout for any single invocation of a test.
    *
    * This value is used if a timeout is not specified in the test case itself.
    *
    * Defaults to [Defaults.defaultInvocationTimeoutInMillis].
    */
   var invocationTimeout: Long = Defaults.defaultInvocationTimeoutInMillis

   /**
    * Returns the default [TestCaseConfig] to be assigned to tests when not specified either in
    * the spec, test factory, or test case itself.
    *
    * If this is null, then defaults to [Defaults.testCaseConfig]
    */
   var defaultTestConfig: TestCaseConfig = Defaults.testCaseConfig

   /**
    * Some specs have DSLs that include prefix or suffix words in the test name.
    *
    * If this method returns true, then test names include those prefix and suffix names
    * in reports and the IDE.
    *
    * For example, when using ExpectSpec like this:
    *
    * expect("this test 1") {
    *   feature("this test 2") {
    *   }
    * }
    *
    * If prefixes are enabled, the output would be:
    *
    * Expect: this test 1
    *   Feature: this test 2
    *
    * And if disabled, the output would be:
    *
    * this test 1
    *    test this 2
    *
    * Defaults to null, which is spec specific behavior.
    */
   var includeTestScopeAffixes: Boolean? = Defaults.defaultIncludeTestScopeAffixes

   /**
    * Controls the default [IsolationMode] that each spec will execute in.
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Default to [Defaults.isolationMode]
    */
   var isolationMode: IsolationMode = Defaults.isolationMode

   /**
    * Controls the ordering of root test cases in each spec.
    *
    * Valid options are the enum values of [TestCaseOrder].
    *
    * This value is used if a value is not specified in the spec itself.
    *
    * Defaults to [Defaults.testCaseOrder]
    */
   var testCaseOrder: TestCaseOrder = Defaults.testCaseOrder

   /**
    * Returns the sort order to use when executing specs.
    *
    * Defaults to [Defaults.specExecutionOrder]
    */
   var specExecutionOrder: SpecExecutionOrder = Defaults.specExecutionOrder

   /**
    * Returns all globally registered [Listener]s.
    */
   fun listeners() = listeners.toList()

   /**
    * Returns all globally registered [Extension]s.
    */
   fun extensions() = extensions.toList()

   /**
    * Returns all globally registered [Filter]s.
    */
   fun filters() = filters.toList()

   fun registerFilters(vararg filters: Filter) = filters.forEach { registerFilter(it) }
   fun registerFilters(filters: Collection<Filter>) = filters.forEach { registerFilter(it) }
   fun deregisterFilters(filters: Collection<Filter>) = filters.forEach { deregisterFilter(it) }

   fun registerFilter(filter: Filter) {
      filters.add(filter)
   }

   fun deregisterFilter(filter: Filter) {
      filters.remove(filter)
   }

   fun registerExtensions(vararg extensions: Extension) = extensions.forEach { registerExtension(it) }
   fun registerExtensions(extensions: List<Extension>) = extensions.forEach { registerExtension(it) }
   fun deregisterExtensions(extensions: List<Extension>) = extensions.forEach { deregisterExtension(it) }

   fun registerExtension(extension: Extension) {
      extensions.add(extension)
   }

   fun deregisterExtension(extension: Extension) {
      extensions.remove(extension)
   }

   fun registerListeners(vararg listeners: Listener) = listeners.forEach { registerListener(it) }
   fun registerListeners(listeners: List<Listener>) = listeners.forEach { registerListener(it) }
   fun deregisterListeners(listeners: List<Listener>) = listeners.forEach { deregisterListener(it) }

   fun registerListener(listener: Listener) {
      listeners.add(listener)
   }

   fun deregisterListener(listener: Listener) {
      listeners.remove(listener)
   }

   fun removeListeners() {
      listeners.clear()
   }

   fun removeExtensions() {
      extensions.clear()
   }

   fun removeFilters() {
      filters.clear()
   }
}

fun Configuration.testListeners(): List<TestListener> = listeners().filterIsInstance<TestListener>()
fun Configuration.testCaseExtensions(): List<TestCaseExtension> = listeners().filterIsInstance<TestCaseExtension>()
