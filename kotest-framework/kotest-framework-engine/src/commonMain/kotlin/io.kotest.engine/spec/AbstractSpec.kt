package io.kotest.engine.spec

import io.kotest.core.Tag
import io.kotest.core.Tuple2
import io.kotest.engine.config.Project
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AfterAny
import io.kotest.core.spec.AfterContainer
import io.kotest.core.spec.AfterEach
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeAny
import io.kotest.core.spec.BeforeContainer
import io.kotest.core.spec.BeforeEach
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.js.JsTest
import io.kotest.engine.js.executeSpec
import io.kotest.engine.tags.tags
import io.kotest.engine.test.ordered
import io.kotest.mpp.log
import kotlin.js.JsName

abstract class AbstractSpec : TestSuite(), Spec {

   /**
    * Sets the number of root test cases that can be executed concurrently in this spec.
    * On the JVM this will result in multiple threads being used.
    * On other platforms this setting will have no effect.
    * Defaults to 1.
    */
   @JsName("threadsJs")
   var threads: Int? = null

   override fun rootTests(): List<RootTest> {

      val testCaseOrder = resolvedTestCaseOrder()
      val materializedTests = materializeRootTests()

      // apply the configuration from this spec to each resolved test
      return materializedTests
         .map {
            it.copy(
               assertionMode = it.assertionMode ?: this.assertions ?: this.assertionMode(),
               config = it.config.copy(tags = it.config.tags + this._tags + this.tags())
            )
         }
         .ordered(testCaseOrder)
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
    * Registers a new before-container callback to be executed before every [TestCase]
    * with type [TestType.Container].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   override fun beforeContainer(f: BeforeContainer) {
      listener(object : TestListener {
         override suspend fun beforeContainer(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   override fun afterContainer(f: AfterContainer) {
      listener(object : TestListener {
         override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a new before-each callback to be executed before every [TestCase]
    * with type [TestType.Test].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   override fun beforeEach(f: BeforeEach) {
      listener(object : TestListener {
         override suspend fun beforeEach(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   override fun afterEach(f: AfterEach) {
      listener(object : TestListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   override fun beforeAny(f: BeforeAny) {
      listener(object : TestListener {
         override suspend fun beforeAny(testCase: TestCase) {
            f(testCase)
         }
      })
   }

   override fun afterAny(f: AfterAny) {
      listener(object : TestListener {
         override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * The annotation [JsTest] is intercepted by the kotlin.js compiler and invoked in the generated
    * javascript code. We need to hook into this function to invoke our test execution code which will
    * run tests defined by kotest.
    *
    * Kotest automatically installs a Javascript test-adapter to intercept calls to all tests so we can
    * avoid passing this special test-generating-test to the underyling javascript test framework so it
    * doesn't appear in test output / reports.
    */
   @JsTest
   fun javascriptTestInterceptor() {
      executeSpec(this)
   }
}

fun Spec.resolvedExtensions(): List<Extension> {
   return when (this) {
      is AbstractSpec -> this._extensions + this.extensions() + factories.flatMap { it.extensions }
      else -> emptyList()
   }
}

fun Spec.resolvedTestCaseOrder() = when (this) {
   is AbstractSpec -> this.testOrder ?: this.testCaseOrder() ?: Project.testCaseOrder()
   else -> Project.testCaseOrder()
}


fun Spec.resolvedIsolationMode() = when (this) {
   is AbstractSpec -> this.isolationMode ?: this.isolation ?: this.isolationMode() ?: Project.isolationMode()
   else -> Project.isolationMode()
}


fun Spec.resolvedThreads() = when (this) {
   is AbstractSpec -> this.threads ?: this.threads() ?: 1
   else -> 1
}

/**
 * Returns all spec level tags associated with this spec instance.
 */
fun Spec.resolvedTags(): Set<Tag> = when (this) {
   is AbstractSpec -> this::class.tags() + this.tags() + this._tags
   else -> emptySet()
}
