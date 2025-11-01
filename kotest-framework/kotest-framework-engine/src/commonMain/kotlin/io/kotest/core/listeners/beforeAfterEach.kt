package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.test.TestResult

interface BeforeEachListener : Extension {

   /**
    * Registers a new before-each callback to be executed before every [TestCase]
    * with type [TestType.Test].
    *
    * @param testCase the test about to be executed.
    */
   suspend fun beforeEach(testCase: TestCase): Unit = Unit
}

interface AfterEachListener : Extension {

   /**
    * Registers a new after-each callback to be executed after every [TestCase]
    * with type [TestType.Test].
    *
    * @param testCase the test that completed.
    * @param result the result of that test.
    */
   suspend fun afterEach(testCase: TestCase, result: TestResult): Unit = Unit
}
