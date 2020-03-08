@file:Suppress("PropertyName")

package io.kotest.core.spec

import io.kotest.core.Tag
import io.kotest.core.config.Project
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.factory.DynamicTest
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.listeners.TestListener
import io.kotest.core.runtime.configureRuntime
import io.kotest.core.runtime.executeSpec
import io.kotest.core.sourceRef
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.fp.Tuple2
import org.junit.platform.commons.annotation.Testable
import kotlin.reflect.KClass

typealias BeforeTest = suspend (TestCase) -> Unit
typealias AfterTest = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeSpec = (Spec) -> Unit
typealias AfterSpec = (Spec) -> Unit
typealias AfterProject = () -> Unit
typealias PrepareSpec = (KClass<out Spec>) -> Unit
typealias FinalizeSpec = (Tuple2<KClass<out Spec>, Map<TestCase, TestResult>>) -> Unit
typealias TestCaseExtensionFn = suspend (Tuple2<TestCase, suspend (TestCase) -> TestResult>) -> TestResult
typealias AroundTestFn = suspend (Tuple2<TestCase, suspend (TestCase) -> TestResult>) -> TestResult
typealias AroundSpecFn = suspend (Tuple2<KClass<out Spec>, suspend () -> Unit>) -> Unit

expect interface AutoCloseable {
   fun close()
}

/**
 * The parent of all configuration DSL objects and contains configuration methods
 * common to both [Spec] and [TestFactoryConfiguration] implementations.
 */
abstract class TestConfiguration {

   /**
    * Config applied to each test case if not overridden per test case.
    * If left null, then defaults to the project config default.
    */
   var defaultTestConfig: TestCaseConfig? = null

   /**
    * Sets an assertion mode which is applied to every test.
    */
   var assertions: AssertionMode? = null

   /**
    * Contains the [Tag]s that will be applied to every test.
    */
   internal var _tags: Set<Tag> = emptySet()

   /**
    * Contains the [TestFactory] instances that have been included with this config.
    */
   internal var factories = emptyList<TestFactory>()

   // test listeners
   // using underscore name to avoid clash in JS compiler with existing methods
   internal var _listeners = emptyList<TestListener>()

   internal var _extensions = emptyList<Extension>()

   fun extension(f: TestCaseExtensionFn) {
      _extensions = _extensions + object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
            return f(Tuple2(testCase, execute))
         }
      }
   }

   fun aroundTest(aroundTestFn: AroundTestFn) {
      _extensions = _extensions + object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
            val f: suspend (TestCase) -> TestResult = { execute(it) }
            return aroundTestFn(Tuple2(testCase, f))
         }
      }
   }

   fun aroundSpec(aroundSpecFn: AroundSpecFn) {
      _extensions = _extensions + object : SpecExtension {
         override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
            aroundSpecFn(Tuple2(spec, process))
         }
      }
   }

   /**
    * Registers a new before-test callback to be executed before every [TestCase].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   abstract fun beforeTest(f: BeforeTest)

   /**
    * Registers a new after-test callback to be executed after every [TestCase].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   abstract fun afterTest(f: AfterTest)

   fun beforeSpec(f: BeforeSpec) {
      listener(object : TestListener {
         override suspend fun beforeSpec(spec: Spec) {
            f(spec)
         }
      })
   }

   fun afterSpec(f: AfterSpec) {
      listener(object : TestListener {
         override suspend fun afterSpec(spec: Spec) {
            f(spec)
         }
      })
   }

   fun prepareSpec(f: PrepareSpec) {
      listeners(object : TestListener {
         override suspend fun prepareSpec(kclass: KClass<out Spec>) {
            f(kclass)
         }
      })
   }

   fun finalizeSpec(f: FinalizeSpec) {
      listeners(object : TestListener {
         override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
            f(Tuple2(kclass, results))
         }
      })
   }

   /**
    * Registers a callback that will execute after all specs have completed.
    * This is a convenience method for creating a [ProjectListener] and registering it.
    */
   fun afterProject(f: AfterProject) {
      Project.registerListener(object : ProjectListener {
         override fun afterProject() {
            f()
         }
      })
   }

   /**
    * Adds [Tag]s to this factory, which will be applied to each test case generated by
    * this [TestFactoryConfiguration]. When this factory is included in a [Spec], only the tests generated
    * from this factory will have these tags applied.
    */
   fun tags(vararg tags: Tag) {
      this._tags = this._tags + tags.toSet()
   }

   fun listener(listener: TestListener) {
      listeners(listener)
   }

   fun listeners(vararg listener: TestListener) {
      this._listeners = this._listeners + listener.toList()
   }

   fun extensions(vararg extensions: TestCaseExtension) {
      this._extensions = this._extensions + extensions.toList()
   }

   /**
    * Include the tests from the given [TestFactory] in this configuration.
    */
   fun include(factory: TestFactory) {
      factories = factories + factory
   }

   /**
    * Includes the tests from the given [TestFactory] with the root tests of the
    * factory given the prefix.
    */
   fun include(prefix: String, factory: TestFactory) {
      fun DynamicTest.addPrefix() = copy(name = prefix + name)
      factories = factories + factory.copy(tests = factory.tests.map { it.addPrefix() })
   }
}

// we need to include setting the adapter as a top level val in here so that it runs before any suite/test in js
@Suppress("unused")
val initializeRuntime = configureRuntime()

@Testable
abstract class Spec : TestConfiguration(), SpecCallbackMethods {

   /**
    * Contains the root [TestCase]s used in this spec.
    */
   var rootTestCases = emptyList<TestCase>()

   /**
    * Sets the [IsolationMode] used by the test engine when running tests in this spec.
    * If left null, then the project default is applied.
    */
   var isolation: IsolationMode? = null

   /**
    * Sets the [TestCaseOrder] to control the order of execution of root level tests in this spec.
    * If left null, then the project default is applied.
    */
   var testOrder: TestCaseOrder? = null

   override fun beforeTest(f: BeforeTest) {
      listener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   override fun afterTest(f: AfterTest) {
      listener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * The annotation [JsTest] is intercepted by the kotlin.js compiler and invoked in the generated
    * javascript code. We need to hook into this function to invoke our execution code which will
    * run tests defined by kotest.
    *
    * Kotest automatically installs a Javascript test-adapter to intercept calls to all tests so we can
    * avoid passing this generating function to the underyling test framework so it doesn't appear
    * in the test report.
    */
   @JsTest
   fun javascriptTestInterceptor() {
      executeSpec(this)
   }

   private fun createTestCase(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ): TestCase {
      return TestCase(
         this::class.description().append(name),
         this,
         test,
         sourceRef(),
         type,
         config,
         null,
         null
      )
   }

   /**
    * Adds a new root-level [TestCase] to this [Spec].
    */
   protected fun addRootTestCase(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      require(rootTestCases.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      require(name.isNotBlank() && name.isNotEmpty()) { "Cannot add test with blank or empty name" }
      //require(acceptingTopLevelRegistration) { "Cannot add nested test here. Please see documentation on testing styles for how to layout nested tests correctly" }
      rootTestCases = rootTestCases + createTestCase(name, test, config, type)
   }
}

/**
 * Register an [AutoCloseable] so that it's close methods is automatically invoked
 * when the tests are completed.
 */
fun <T : AutoCloseable> TestConfiguration.autoClose(closeable: T): T {
   afterSpec {
      closeable.close()
   }
   return closeable
}

fun <T : AutoCloseable> TestConfiguration.autoClose(closeable: Lazy<T>): Lazy<T> {
   afterSpec {
      closeable.value.close()
   }
   return closeable
}
