package io.kotest.core.spec.style.scopes

import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.Tuple2
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.*
import io.kotest.core.test.DescriptionName
import kotlinx.coroutines.CoroutineScope

/**
 * Contains methods used by container levels tests - such as adding before / after test callbacks.
 */
interface ContainerScope : CoroutineScope {

   /**
    * Returns the [Description] for the test that defined this scope.
    * This is needed when registering before/after callbacks since they need to know the
    * description (path) of the current scope in order to filter.
    */
   val description: Description

   /**
    * A [Lifecycle] is used by contexts to register lifecycle callbacks.
    */
   val lifecycle: Lifecycle

   /**
    * The framework [TestContext] required to register a test at runtime.
    */
   val testContext: TestContext

   val defaultConfig: TestCaseConfig

   /**
    * Adds a new test case to this scope with type [TestType.Container].
    */
   suspend fun addContainerTest(name: DescriptionName.TestName, xdisabled: Boolean, test: suspend TestContext.() -> Unit) {
      addTest(name, xdisabled, defaultConfig, TestType.Container, test)
   }

   /**
    * Adds a new test case to this scope with type [TestType.Test].
    */
   suspend fun addTest(name: DescriptionName.TestName, xdisabled: Boolean, test: suspend TestContext.() -> Unit) {
      addTest(name, xdisabled, defaultConfig, TestType.Test, test)
   }

   /**
    * Registerd a new test case to this scope with the given test type.
    */
   suspend fun addTest(
      name: DescriptionName.TestName,
      xdisabled: Boolean,
      config: TestCaseConfig,
      type: TestType,
      test: suspend TestContext.() -> Unit,
   ) {
      val activeConfig = if (xdisabled) config.copy(enabled = false) else config
      testContext.registerTestCase(name, test, activeConfig, type)
   }

   /**
    * Registers a [BeforeTest] function that executes before every test in this scope.
    */
   fun beforeTest(f: BeforeTest) {
      lifecycle.addListener(object : TestListener {
         override suspend fun beforeTest(testCase: TestCase) {
            if (description.isAncestorOf(testCase.description)) f(testCase)
         }
      })
   }

   /**
    * Registers an [AfterTest] function that executes after every test in this scope.
    */
   fun afterTest(f: AfterTest) {
      lifecycle.addListener(object : TestListener {
         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            if (description.isAncestorOf(testCase.description)) f(Tuple2(testCase, result))
         }
      })
   }

   /**
    * Registers a [BeforeContainer] function that executes before every test with type [TestType.Container] in
    * this scope.
    */
   fun beforeContainer(f: BeforeContainer) {
      lifecycle.addListener(object : TestListener {
         override suspend fun beforeContainer(testCase: TestCase) {
            if (description.isAncestorOf(testCase.description)) {
               f(testCase)
            }
         }
      })
   }

   /**
    * Registers an [AfterContainer] function that executes after every test with type [TestType.Container] in
    * this scope.
    */
   fun afterContainer(f: AfterContainer) {
      lifecycle.addListener(object : TestListener {
         override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
            if (description.isAncestorOf(testCase.description)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }

   /**
    * Registers a [BeforeEach] function that executes before every test with type [TestType.Test] in this scope.
    */
   fun beforeEach(f: BeforeEach) {
      lifecycle.addListener(object : TestListener {
         override suspend fun beforeEach(testCase: TestCase) {
            if (description.isAncestorOf(testCase.description)) {
               f(testCase)
            }
         }
      })
   }

   /**
    * Registers an [AfterEach] function that executes after every test with type [TestType.Test] in this scope.
    */
   fun afterEach(f: AfterEach) {
      lifecycle.addListener(object : TestListener {
         override suspend fun afterEach(testCase: TestCase, result: TestResult) {
            if (description.isAncestorOf(testCase.description)) {
               f(Tuple2(testCase, result))
            }
         }
      })
   }

   /**
    * Registers a [BeforeAny] function that executes before every test with any [TestType] in this scope.
    */
   fun beforeAny(f: BeforeAny) {
      lifecycle.addListener(object : TestListener {
         override suspend fun beforeAny(testCase: TestCase) {
            if (description.isAncestorOf(testCase.description)) f(testCase)
         }
      })
   }

   /**
    * Registers an [AfterAny] function that executes after every test with any [TestType] in this scope.
    */
   fun afterAny(f: AfterAny) {
      lifecycle.addListener(object : TestListener {
         override suspend fun afterAny(testCase: TestCase, result: TestResult) {
            if (description.isAncestorOf(testCase.description)) f(Tuple2(testCase, result))
         }
      })
   }
}
