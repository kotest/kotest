package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType

interface BeforeEachListener : Extension {

   /**
    * Registers a new before-each callback to be executed before every [TestCase]
    * with type [TestType.Test].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   suspend fun beforeEach(testCase: TestCase): Unit = Unit
}

interface AfterEachListener : Extension {

   /**
    * Registers a new after-each callback to be executed after every [TestCase]
    * with type [TestType.Test].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   suspend fun afterEach(testCase: TestCase, result: TestResult): Unit = Unit
}
