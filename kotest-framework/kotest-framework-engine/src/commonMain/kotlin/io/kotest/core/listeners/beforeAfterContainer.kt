package io.kotest.core.listeners

import io.kotest.core.extensions.Extension
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.core.test.TestType

interface BeforeContainerListener : Extension {

   /**
    * Registers a new before-container callback to be executed before every [TestCase]
    * with type [TestType.Container].
    * The [TestCase] about to be executed is provided as the parameter.
    */
   suspend fun beforeContainer(testCase: TestCase): Unit = Unit
}

interface AfterContainerListener : Extension {

   /**
    * Registers a new after-container callback to be executed after every [TestCase]
    * with type [TestType.Container].
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   suspend fun afterContainer(testCase: TestCase, result: TestResult): Unit = Unit
}
