package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType

interface BeforeListener : Extension {

   /**
    * Registers a new before callback to be executed once before all nested [TestCase]
    * with type [TestType.Test].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   suspend fun before(testCase: TestCase): Unit = Unit
}

interface AfterListener : Extension {

   /**
    * Registers a new after callback to be executed once after all nested [TestCase]
    * with type [TestType.Test].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   suspend fun after(testCase: TestCase, result: TestResult): Unit = Unit
}
