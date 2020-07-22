package io.kotest.core.config

import io.kotest.core.extensions.Extension
import io.kotest.core.filters.Filter
import io.kotest.core.listeners.Listener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.LexicographicSpecExecutionOrder
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCaseConfig
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Project-wide configuration. Extensions returned by an
 * instance of this class will be applied to all [Spec] and [TestCase]s.
 *
 * Create an object that is derived from this class, name the object `ProjectConfig`
 * and place it in your classpath in a package called `io.kotest.provided`.
 *
 * Kotest will detect its presence and use it when executing tests.
 *
 * Note: This is a breaking change from versions 2.0 and before, in which Kotest would
 * scan the classpath for instances of this class. It no longer does that, in favour
 * of the predefined package name + classname.
 */
abstract class AbstractProjectConfig {

   /**
    * List of project wide [Extension] instances.
    */
   open fun extensions(): List<Extension> = emptyList()

   /**
    * List of project wide [Listener] instances.
    */
   open fun listeners(): List<Listener> = emptyList()

   /**
    * List of project wide [ProjectListener] instances.
    */
   @Deprecated("Override listeners() which now supports all type of listeners")
   open fun projectListeners(): List<ProjectListener> = emptyList()

   /**
    * List of project wide [Filter] instances.
    */
   open fun filters(): List<Filter> = emptyList()

   /**
    * Override this function and return an instance of [SpecExecutionOrder] which will
    * be used to sort specs before execution.
    *
    * Implementations are currently:
    *  - [LexicographicSpecExecutionOrder]
    *  - [FailureFirstSpecExecutionOrder]
    *  - [RandomSpecExecutionOrder]
    */
   @Deprecated("use the val version")
   open fun specExecutionOrder(): SpecExecutionOrder? = null

   /**
    * Note: This has no effect on non-JVM targets.
    */
   open val specExecutionOrder: SpecExecutionOrder? = null

   /**
    * The [IsolationMode] set here will be applied if the isolation mode in a spec is null.
    */
   @Deprecated("use the val version")
   open fun isolationMode(): IsolationMode? = null

   open val isolationMode: IsolationMode? = null

   /**
    * A global timeout that is applied to all tests if not null.
    * Tests which define their own timeout will override this.
    * The value here is in millis
    */
   @OptIn(ExperimentalTime::class)
   open val timeout: Duration? = null

   /**
    * Override this function and return a number greater than 1 if you wish to
    * enable parallel execution of tests. The number returned is the number of
    * concurrently executing specs.
    *
    * An alternative way to enable this is the system property kotest.parallelism
    * which will always (if defined) take priority over the value here.
    */
   @Deprecated("use the val version")
   open fun parallelism(): Int = 1

   open val parallelism: Int = 1

   /**
    * When set to true, failed specs are written to a file called spec_failures.
    * This file is used on subsequent test runs to run the failed specs first.
    *
    * To enable this feature, set this to true, or set the system property
    * 'kotest.write.specfailures=true'
    */
   @Deprecated("use the val version")
   open fun writeSpecFailureFile(): Boolean = false

   open val writeSpecFailureFile: Boolean? = null

   /**
    * Sets the order of top level tests in a spec.
    * The value set here will be used unless overriden in a [Spec].
    * The value in a [Spec] is always taken in preference to the value here.
    * Nested tests will always be executed in discovery order.
    *
    * If this function returns null then the default of Sequential
    * will be used.
    */
   @Deprecated("use the val version")
   open fun testCaseOrder(): TestCaseOrder? = null

   open val testCaseOrder: TestCaseOrder? = null

   /**
    * Override this value and set it to true if you want all tests to behave as if they
    * were operating in an [assertSoftly] block.
    */
   open val globalAssertSoftly: Boolean? = null

   /**
    * Override this value and set it to false if you want to disable autoscanning of extensions
    * and listeners.
    */
   open val autoScanEnabled: Boolean? = null

   /**
    * Override this value with a list of classes for which @autoscan is disabled.
    */
   open val autoScanIgnoredClasses: List<KClass<*>> = emptyList()

   /**
    * Override this value and set it to true if you want the build to be marked as failed
    * if there was one or more tests that were disabled/ignored.
    */
   open val failOnIgnoredTests: Boolean = false

   /**
    * Override this value to set a global [AssertionMode].
    * If a [Spec] sets an assertion mode, then the spec will override.
    */
   open val assertionMode: AssertionMode? = null

   /**
    * Any [TestCaseConfig] set here is used as the default for tests, unless overriden in a spec,
    * or in a test itself. In other words the order is test -> spec -> project config default -> kotest default
    */
   open val defaultTestCaseConfig: TestCaseConfig? = null

   /**
    * Some specs have DSLs that include "prefix" words in the test name.
    * For example, when using ExpectSpec like this:
    *
    * expect("this test 1") {
    *   feature("this test 2") {
    *   }
    * }
    *
    * Will result in:
    *
    * Expect: this test 1
    *   Feature: this test 2
    *
    * From 4.2, this feature can be disabled by setting this value to false.
    * Then the output of the previous test would be:
    *
    * this test 1
    *   this test 2
    */
   open val includeTestScopePrefixes: Boolean? = null

   /**
    * The casing of the tests' names can be adjusted using different strategies. It affects **only**
    * the first letter of tests' prefixes (I.e.: Given, When, Then) and the first letter of the tests'
    * titles.
    *
    * This setting's options are defined in [TestNameCaseOptions]. Check the previous enum for the
    * available options and examples.
    */
   open val testNameCase: TestNameCaseOptions? = null

   /**
    * Executed before the first test of the project, but after the
    * [ProjectListener.beforeProject] methods.
    */
   open fun beforeAll() {}

   /**
    * Executed after the last test of the project, but before the
    * [ProjectListener.afterProject] methods.
    */
   open fun afterAll() {}
}
