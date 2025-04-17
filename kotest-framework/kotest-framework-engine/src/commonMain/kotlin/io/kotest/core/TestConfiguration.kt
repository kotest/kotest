@file:Suppress("PropertyName")

package io.kotest.core

import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.listeners.AfterContainerListener
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterInvocationListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.listeners.BeforeInvocationListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.AfterAny
import io.kotest.core.spec.AfterContainer
import io.kotest.core.spec.AfterEach
import io.kotest.core.spec.AfterInvocation
import io.kotest.core.spec.AfterSpec
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.AroundTestFn
import io.kotest.core.spec.AutoCloseable
import io.kotest.core.spec.BeforeAny
import io.kotest.core.spec.BeforeContainer
import io.kotest.core.spec.BeforeEach
import io.kotest.core.spec.BeforeInvocation
import io.kotest.core.spec.BeforeSpec
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.Extendable
import io.kotest.core.spec.Spec
import io.kotest.core.spec.TestCaseExtensionFn
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlin.js.JsName

/**
 * An abstract base implementation for shared configuration between [Spec] and [TestFactoryConfiguration].
 * Provides convenience methods for adding callbacks.
 */
abstract class TestConfiguration : Extendable() {

   @JsName("_tags")
   internal var _tags: Set<Tag> = emptySet()

   private var _autoCloseables = emptyList<Lazy<AutoCloseable>>()

   private var _parentConfiguration: TestConfiguration? = null

   /**
    * Sets an assertion mode which is applied to every test.
    * If `null`, then the project default is used.
    */
   var assertions: AssertionMode? = null

   /**
    * Whether soft assertion mode should be applied for all tests in the spec.
    */
   var assertSoftly: Boolean? = null

   /**
    * Adds [Tag]s to this spec or factory, which will be applied to each test case.
    *
    * When applied in a factory, only tests generated from that factory will have the tags applied.
    * When applied to a spec, all tests will have the tags applied.
    */
   open fun tags(vararg tags: Tag) {
      _tags = _tags + tags.toSet()
   }

   internal fun appliedTags() = _tags

   /**
    * Registers an [AutoCloseable] to be closed when the spec is completed.
    */
   fun <T : AutoCloseable> autoClose(closeable: T): T =
      autoClose(lazy(LazyThreadSafetyMode.NONE) { closeable }).value

   /**
    * Registers a lazy [AutoCloseable] to be closed when the spec is completed.
    */
   fun <T : AutoCloseable> autoClose(closeable: Lazy<T>): Lazy<T> {
      _parentConfiguration?.autoClose(closeable)
      _autoCloseables = listOf(closeable) + _autoCloseables
      return closeable
   }

   /**
    * Returns a list of all [AutoCloseable]s registered in this spec or factory.
    */
   fun autoCloseables(): List<Lazy<AutoCloseable>> = _autoCloseables.toList()

   /**
    * Registers a callback to be executed before every [TestCase].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   open fun beforeTest(f: BeforeTest) {
      extension(object : BeforeTestListener {
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
   open fun afterTest(f: AfterTest) {
      extension(object : AfterTestListener {
         override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a callback to be executed before every [TestCase] with type [TestType.Container].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeContainer(f: BeforeContainer) {
      extension(object : BeforeContainerListener {
         override suspend fun beforeContainer(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   /**
    * Registers a callback to be executed after every [TestCase] with type [TestType.Container].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    *
    * After-container callbacks are executed in reverse order. That is callbacks registered
    * first are executed last, which allows for nested test blocks to add callbacks that run before
    * top level callbacks.
    */
   fun afterContainer(f: AfterContainer) {
      prependExtension(object : AfterContainerListener {
         override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a callback to be executed before every [TestCase] with type [TestType.Test].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeEach(f: BeforeEach) {
      this@TestConfiguration.extension(object : BeforeEachListener {
         override suspend fun beforeEach(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   /**
    * Registers a callback to be executed after every [TestCase] with type [TestType.Test].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    *
    * After-each callbacks are executed in reverse order. That is callbacks registered
    * first are executed last, which allows for nested test blocks to add callbacks that run before
    * top level callbacks.
    */
   fun afterEach(f: AfterEach) {
      prependExtension(object : AfterEachListener {
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
      extension(object : BeforeTestListener {
         override suspend fun beforeAny(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   /**
    * Registers a callback to be executed before every invocation of [TestCase]
    * with type [TestType.Test].
    *
    * The [TestCase] about to be executed and invocation iteration is provided as the parameter.
    */
   fun beforeInvocation(f: BeforeInvocation) {
      extension(object : BeforeInvocationListener {
         override suspend fun beforeInvocation(testCase: TestCase, iteration: Int) {
            f(testCase, iteration)
         }
      })
   }

   /**
    * Registers a callback to be executed after every invocation of [TestCase]
    * with type [TestType.Test].
    *
    * The [TestCase] about to be executed and invocation iteration is provided as the parameter.
    */
   fun afterInvocation(f: AfterInvocation) {
      extension(object : AfterInvocationListener {
         override suspend fun afterInvocation(testCase: TestCase, iteration: Int) {
            f(testCase, iteration)
         }
      })
   }

   /**
    * Registers a callback to be executed after every [TestCase]
    * with type [TestType.Container] or [TestType.Test].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    *
    * After-any callbacks are executed in reverse order. That is callbacks registered
    * first are executed last, which allows for nested test blocks to add callbacks that run before
    * top level callbacks.
    */
   fun afterAny(f: AfterAny) {
      prependExtension(object : AfterTestListener {
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
      extension(object : BeforeSpecListener {
         override suspend fun beforeSpec(spec: Spec) {
            f(spec)
         }
      })
   }

   /**
    * Register an extension callback
    */
   fun extension(f: TestCaseExtensionFn) {
      this@TestConfiguration.extension(object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult =
            f(Tuple2(testCase, execute))
      })
   }

   /**
    * Registers a callback to be executed after all tests in this spec.
    * The spec instance is provided as a parameter.
    */
   open fun afterSpec(f: AfterSpec) {
      extension(object : AfterSpecListener {
         override suspend fun afterSpec(spec: Spec) {
            f(spec)
         }
      })
   }

   fun aroundTest(aroundTestFn: AroundTestFn) {
      this@TestConfiguration.extension(object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
            val f: suspend (TestCase) -> TestResult = { execute(it) }
            return aroundTestFn(Tuple2(testCase, f))
         }
      })
   }

   internal fun setParentConfiguration(configuration: TestConfiguration) {
      _parentConfiguration = configuration
   }

   /**
    * Register a single [Extension] of type T return that listener.
    */
   @Deprecated("Use extension instead", ReplaceWith("extension"))
   fun <T : Extension> register(extension: T): T {
      extensions(listOf(extension))
      return extension
   }

   @Deprecated("Use autoCloseables instead", ReplaceWith("autoCloseables"))
   fun registeredAutoCloseables(): List<Lazy<AutoCloseable>> = _autoCloseables.toList()
}


/**
 * Lazily creates and registers an extension of type [T], unless it's already registered (keyed by
 * type [T]).
 */
internal inline fun <reified T : Extension> TestConfiguration.extensionLazy(
   createExtension: () -> T,
): T {
   val existingExtension = this@extensionLazy.extensions().filterIsInstance<T>().singleOrNull()
   return existingExtension ?: extension(createExtension())
}
