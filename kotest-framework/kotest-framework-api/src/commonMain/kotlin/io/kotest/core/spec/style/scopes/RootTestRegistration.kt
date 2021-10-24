package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.test.TestCase
import io.kotest.core.test.config.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

/**
 * Handles registration of top level tests inside a [RootContext].
 */
interface RootTestRegistration {

   val defaultConfig: TestCaseConfig

   /**
    * Adds a new root [TestCase] to this scope with type [TestType.Container].
    */
   fun addContainerTest(name: TestName, xdisabled: Boolean, test: suspend TestContext.() -> Unit) =
      addTest(name, xdisabled, defaultConfig, TestType.Container, test)

   /**
    * Adds a new root [TestCase] to this scope with type [TestType.Test].
    */
   fun addTest(
      name: TestName,
      xdisabled: Boolean,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, xdisabled, defaultConfig, TestType.Test, test)

   /**
    * Adds a new root [TestCase] to this scope with type [TestType.Test] and custom config.
    */
   fun addTest(
      name: TestName,
      xdisabled: Boolean,
      config: TestCaseConfig,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, xdisabled, config, TestType.Test, test)

   /**
    * Adds a new root [TestCase] to this scope with the given test type and config.
    *
    * @param xdisabled if true then this test has been disabled by using an x-keyword via the dsl.
    */
   fun addTest(
      name: TestName,
      xdisabled: Boolean,
      config: TestCaseConfig,
      type: TestType,
      test: suspend TestContext.() -> Unit
   )
}
