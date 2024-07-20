package io.kotest.core.listeners

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType

@Suppress("DEPRECATION") // Remove when removing Listener
interface BeforeEachListener : Listener {

   /**
    * Registers a new before-each callback to be executed before every [TestCase]
    * with type [TestType.Test].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   suspend fun beforeEach(testCase: TestCase): Unit = Unit
}

@Suppress("DEPRECATION") // Remove when removing Listener
interface AfterEachListener : Listener {

   /**
    * Registers a new after-each callback to be executed after every [TestCase]
    * with type [TestType.Test].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   suspend fun afterEach(testCase: TestCase, result: TestResult): Unit = Unit
}
