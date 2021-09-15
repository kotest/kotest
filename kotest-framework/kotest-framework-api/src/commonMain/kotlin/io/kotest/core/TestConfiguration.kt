@file:Suppress("PropertyName")

package io.kotest.core

import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.names.TestName
import io.kotest.core.spec.AfterAny
import io.kotest.core.spec.AfterContainer
import io.kotest.core.spec.AfterEach
import io.kotest.core.spec.AfterSpec
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.AroundTestFn
import io.kotest.core.spec.AutoCloseable
import io.kotest.core.spec.BeforeAny
import io.kotest.core.spec.BeforeContainer
import io.kotest.core.spec.BeforeEach
import io.kotest.core.spec.BeforeSpec
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.Spec
import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlin.js.JsName

/**
 * An abstract base implementation for shared configuration between [Spec] and [TestFactoryConfiguration].
 */
abstract class TestConfiguration {

   @JsName("_tags")
   internal var _tags: Set<Tag> = emptySet()

   @JsName("_extensions")
   internal var _extensions = emptyList<Extension>()

   private var _autoCloseables = emptyList<Lazy<AutoCloseable>>()

   /**
    * Config applied to each test case if not overridden per test case.
    * If left null, then defaults to the project default.
    */
   var defaultTestConfig: TestCaseConfig? = null

   /**
    * Sets an assertion mode which is applied to every test.
    * If null, then the project default is used.
    */
   var assertions: AssertionMode? = null

   /**
    * Register a single [TestListener] of type T return that listener.
    */
   @Deprecated("Use register. This method will be removed in 6.0", ReplaceWith("register(listener)"))
   fun <T : TestListener> listener(listener: T): T {
      return register(listener)
   }

   /**
    * Register multiple [TestListener]s.
    */
   @Deprecated("Use register. This method will be removed in 6.0", ReplaceWith("register(listeners)"))
   fun listeners(listeners: List<TestListener>) {
      register(listeners)
   }

   /**
    * Register multiple [TestListener]s.
    */
   @Deprecated("Use register. This method will be removed in 6.0", ReplaceWith("register(listeners)"))
   fun listeners(vararg listeners: TestListener) = register(listeners.toList())

   /**
    * Register a single [TestListener] of type T return that listener.
    */
   fun <T : TestListener> register(extension: T): T {
      register(listOf(extension))
      return extension
   }

   /**
    * Register a single [TestCaseExtension] of type T return that extension.
    */
   fun <T : Extension> extension(extension: T): T {
      extensions(extension)
      return extension
   }

   fun register(vararg extensions: Extension) {
      register(extensions.toList())
   }

   fun register(extensions: List<Extension>) {
      _extensions = _extensions + extensions
   }

   /**
    * Register multiple [Extension]s.
    */
   fun extensions(vararg extensions: Extension) {
      register(extensions.toList())
   }

   /**
    * Register multiple [Extension]s.
    */
   fun extensions(extensions: List<Extension>) {
      register(extensions)
   }

   /**
    * Adds [Tag]s to this spec or factory, which will be applied to each test case.
    *
    * When applied in a factory, only tests generated from that factory will have the tags applied.
    * When applied to a spec, all tests will have the tags applied.
    */
   fun tags(vararg tags: Tag) {
      _tags = _tags + tags.toSet()
   }

   /**
    * Registers an [AutoCloseable] to be closed when the spec is completed.
    */
   @Suppress("PropertyName")
   fun <T : AutoCloseable> autoClose(closeable: T): T {
      autoClose(lazy { closeable })
      return closeable
   }

   /**
    * Registers a lazy [AutoCloseable] to be closed when the spec is completed.
    */
   fun <T : AutoCloseable> autoClose(closeable: Lazy<T>): Lazy<T> {
      _autoCloseables = listOf(closeable) + _autoCloseables
      return closeable
   }

   abstract fun addTest(
      name: TestName,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   )

   /**
    * Registers a callback to be executed before every [TestCase].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeTest(f: BeforeTest) {
      register(object : TestListener {
         override suspend fun beforeAny(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   /**
    * Registers a callback to be executed after every [TestCase].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterTest(f: AfterTest) {
      register(object : TestListener {
         override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a callback to be executed before every [TestCase]
    * with type [TestType.Container].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeContainer(f: BeforeContainer) {
      register(object : TestListener {
         override suspend fun beforeContainer(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   /**
    * Registers a callback to be executed after every [TestCase]
    * with type [TestType.Container].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterContainer(f: AfterContainer) {
      register(object : TestListener {
         override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a callback to be executed before every [TestCase]
    * with type [TestType.Test].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeEach(f: BeforeEach) {
      register(object : TestListener {
         override suspend fun beforeEach(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   /**
    * Registers a callback to be executed after every [TestCase]
    * with type [TestType.Test].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterEach(f: AfterEach) {
      register(object : TestListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a callback to be executed before every [TestCase]
    * with type [TestType.Test] or [TestType.Container].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeAny(f: BeforeAny) {
      register(object : TestListener {
         override suspend fun beforeAny(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   /**
    * Registers a callback to be executed after every [TestCase]
    * with type [TestType.Container] or [TestType.Test].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterAny(f: AfterAny) {
      register(object : TestListener {
         override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a callback to be executed before any tests in this spec.
    * The spec instance is provided as a parameter.
    */
   fun beforeSpec(f: BeforeSpec) {
      register(object : TestListener {
         override suspend fun beforeSpec(spec: Spec) {
            f(spec)
         }
      })
   }

   /**
    * Register an extension callback
    */
   fun extension(f: TestCaseExtensionFn) {
      extension(object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult =
            f(Tuple2(testCase, execute))
      })
   }

   /**
    * Registers a callback to be executed after all tests in this spec.
    * The spec instance is provided as a parameter.
    */
   fun afterSpec(f: AfterSpec) {
      register(object : TestListener {
         override suspend fun afterSpec(spec: Spec) {
            f(spec)
         }
      })
   }

   fun aroundTest(aroundTestFn: AroundTestFn) {
      extension(object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
            val f: suspend (TestCase) -> TestResult = { execute(it) }
            return aroundTestFn(Tuple2(testCase, f))
         }
      })
   }

   fun registeredAutoCloseables(): List<Lazy<AutoCloseable>> = _autoCloseables.toList()

   /**
    * Returns any [Extension] instances registered directly on this class.
    */
   fun registeredExtensions() = _extensions
}
