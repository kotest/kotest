package io.kotest.core.specs

import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.core.fromSpecClass
import io.kotest.core.sourceRef
import org.junit.platform.commons.annotation.Testable

// these functions call out to the js test methods
// on the jvm these functions will be empty
expect fun generateTests(rootTests: List<TestCase>)

expect interface AutoCloseable {
   fun close()
}

@Suppress("MemberVisibilityCanBePrivate")
@Testable
abstract class AbstractSpec : Spec {

   protected val rootTestCases = mutableListOf<TestCase>()

   // this var can be set to false by a subclass to stop more root classes being registered added at runtime
   var acceptingTopLevelRegistration = true

   override fun testCases(): List<TestCase> = rootTestCases.toList()

   protected fun createTestCase(name: String,
                                test: suspend TestContext.() -> Unit,
                                config: TestCaseConfig,
                                type: TestType) =
      TestCase(Description.fromSpecClass(this::class).append(name),
         this,
         test,
         sourceRef(),
         type,
         config)

   protected fun addTestCase(name: String,
                             test: suspend TestContext.() -> Unit,
                             config: TestCaseConfig,
                             type: TestType) {
      require(rootTestCases.none { it.name == name }) { "Cannot add test with duplicate name $name" }
      require(acceptingTopLevelRegistration) { "Cannot add nested test here. Please see documentation on testing styles for how to layout nested tests correctly" }
      rootTestCases.add(createTestCase(name, test, config, type))
   }

   private val closeablesInReverseOrder = mutableListOf<AutoCloseable>()

   /**
    * Registers a field for auto closing after all tests have run.
    */
   protected fun <T : AutoCloseable> autoClose(closeable: T): T {
      closeablesInReverseOrder.add(0, closeable)
      return closeable
   }

   override fun closeResources() {
      closeablesInReverseOrder.forEach { it.close() }
   }

   /**
    * Config applied to each test case if not overridden per test case.
    */
   protected open val defaultTestCaseConfig: TestCaseConfig = TestCaseConfig()

   // this is a dummy method, intercepted by the kotlin.js framework adapter to generate tests
   @JsTest
   fun kotestGenerateTests() {
      generateTests(rootTestCases.toList())
   }
}
