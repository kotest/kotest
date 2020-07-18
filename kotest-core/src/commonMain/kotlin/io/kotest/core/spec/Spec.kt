package io.kotest.core.spec

import io.kotest.core.Tag
import io.kotest.core.Tuple2
import io.kotest.core.config.Project
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.TestListener
import io.kotest.core.runtime.executeSpec
import io.kotest.core.sourceRef
import io.kotest.core.tags
import io.kotest.core.test.*
import io.kotest.mpp.log
import kotlin.js.JsName

abstract class Spec : TestConfiguration(), SpecConfigurationMethods {

   /**
    * Sets the number of root test cases that can be executed concurrently in this spec.
    * On the JVM this will result in multiple threads being used.
    * On other platforms this setting will have no effect.
    * Defaults to 1.
    */
   @JsName("threadsJs")
   var threads: Int? = null

   /**
    * Materializes the tests defined in this spec as [TestCase] instances.
    */
   protected abstract fun materializeRootTests(): List<TestCase>

   /**
    * Returns the root tests of this spec.
    *
    * The returned list will be ordered according to the [TestCaseOrder] set on this spec,
    * or if null, then the project config value, or finally the kotest default.
    */
   fun rootTests(): List<RootTest> {

      val order = resolvedTestCaseOrder()
      val materializedTests = materializeRootTests()

      // apply the configuration from this spec to each resolved test
      return materializedTests
         .map {
            it.copy(
               assertionMode = it.assertionMode ?: this.assertions ?: this.assertionMode(),
               config = it.config.copy(tags = it.config.tags + this._tags + this.tags())
            )
         }
         .ordered(order)
         .withIndex()
         .map { RootTest(it.value, it.index) }
         .also {
            log("Materialized roots: $it")
         }
   }


   @Deprecated(
      "This var was replaced by [isolationMode]. Use it instead. This var will be removed in 4.3",
      ReplaceWith("isolationMode")
   )
   var isolation: IsolationMode? = null

   /**
    * Sets the [IsolationMode] used by the test engine when running tests in this spec.
    * If left null, then the project default is applied.
    */
   @JsName("isolationModeJs")
   var isolationMode: IsolationMode? = null

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
}

fun Spec.createTestCase(
   name: TestName,
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
 * Returns the resolved listeners for a given [Spec].
 * That is, the listeners defined directly on the spec, listeners generated from the
 * callback-dsl methods, and listeners defined in any included test factories.
 */
fun Spec.resolvedTestListeners(): List<TestListener> {
   val callbacks = object : TestListener {
      override suspend fun afterSpec(spec: Spec) {
         this@resolvedTestListeners.afterSpec(spec)
      }

      override suspend fun afterTest(testCase: TestCase, result: TestResult) {
         this@resolvedTestListeners.afterTest(testCase, result)
      }

      override suspend fun beforeTest(testCase: TestCase) {
         this@resolvedTestListeners.beforeTest(testCase)
      }

      override suspend fun beforeSpec(spec: Spec) {
         this@resolvedTestListeners.beforeSpec(spec)
      }
   }
   return this._listeners + this.listeners() + callbacks + factories.flatMap { it.listeners }
}

fun Spec.resolvedExtensions(): List<Extension> {
   return this._extensions + this.extensions() + factories.flatMap { it.extensions }
}

fun Spec.resolvedTestCaseOrder() =
   this.testOrder ?: this.testCaseOrder() ?: Project.testCaseOrder()

fun Spec.resolvedIsolationMode() =
   this.isolationMode ?: this.isolation ?: this.isolationMode() ?: Project.isolationMode()

fun Spec.resolvedThreads() = this.threads ?: this.threads() ?: 1

/**
 * Returns all spec level tags associated with this spec instance.
 */
fun Spec.resolvedTags(): Set<Tag> {
   return this::class.tags() + this.tags() + this._tags
}

/**
 * Orders the collection of [TestCase]s based on the provided [TestCaseOrder].
 */
fun List<TestCase>.ordered(spec: TestCaseOrder): List<TestCase> {
   return when (spec) {
      TestCaseOrder.Sequential -> this
      TestCaseOrder.Random -> this.shuffled()
      TestCaseOrder.Lexicographic -> this.sortedBy { it.description.name.displayName().toLowerCase() }
   }
}

data class RootTest(val testCase: TestCase, val order: Int)
