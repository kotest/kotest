@file:Suppress("PropertyName")

package io.kotest.core.spec

import io.kotest.core.Tag
import io.kotest.core.Tuple2
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
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

typealias BeforeTest = suspend (TestCase) -> Unit
typealias AfterTest = suspend (Tuple2<TestCase, TestResult>) -> Unit
typealias BeforeSpec = suspend (Spec) -> Unit
typealias AfterSpec = suspend (Spec) -> Unit
typealias AfterProject = () -> Unit
typealias PrepareSpec = suspend (KClass<out Spec>) -> Unit
typealias FinalizeSpec = suspend (Tuple2<KClass<out Spec>, Map<TestCase, TestResult>>) -> Unit
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
    * Returns the number of concurrent root test cases of Spec that can be executed.
    * Defaults to 1.
    */
   var threads: Int = 1

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
         override suspend fun afterProject() {
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

   internal var autoCloseables = emptyList<AutoCloseable>()
}

// we need to include setting the adapter as a top level val in here so that it runs before any suite/test in js
@Suppress("unused")
val initializeRuntime = configureRuntime()

/**
 * Closes an [AutoCloseable] when the spec is completed by registering an afterSpec listener
 * which invokes the [AutoCloseable.close] method.
 */
fun <T : AutoCloseable> TestConfiguration.autoClose(closeable: T): T {
   autoCloseables = listOf(closeable) + autoCloseables
   return closeable
}

/**
 * Closes a lazy [AutoCloseable] when the spec is completed by registering an afterSpec listener
 * which invokes the [AutoCloseable.close] method.
 */
fun <T : AutoCloseable> TestConfiguration.autoClose(closeable: Lazy<T>): Lazy<T> {
   autoClose(closeable.value)
   return closeable
}
