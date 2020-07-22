package io.kotest.core.spec.style.scopes

import io.kotest.core.Tuple2
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import io.kotest.core.test.*
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

   suspend fun addContainerTest(name: TestName, xdisabled: Boolean, test: suspend TestContext.() -> Unit) {
      addTest(name, xdisabled, defaultConfig, TestType.Container, test)
   }

   suspend fun addTest(name: TestName, xdisabled: Boolean, test: suspend TestContext.() -> Unit) {
      addTest(name, xdisabled, defaultConfig, TestType.Test, test)
   }

   suspend fun addTest(
      name: TestName,
      xdisabled: Boolean,
      config: TestCaseConfig,
      type: TestType,
      test: suspend TestContext.() -> Unit
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

}
