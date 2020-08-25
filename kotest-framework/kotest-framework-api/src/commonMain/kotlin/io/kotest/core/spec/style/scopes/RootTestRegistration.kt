package io.kotest.core.spec.style.scopes

import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.withXDisabled

/**
 * Handles registration of top level tests inside a [RootScope].
 */
interface RootTestRegistration {

   val defaultConfig: TestCaseConfig

   /**
    * Adds a new root [TestCase] to this scope with type [TestType.Container].
    */
   fun addContainerTest(name: DescriptionName.TestName, xdisabled: Boolean, test: suspend TestContext.() -> Unit) =
      addTest(name, xdisabled, defaultConfig, TestType.Container, test)

   /**
    * Adds a new root [TestCase] to this scope with type [TestType.Test].
    */
   fun addTest(
      name: DescriptionName.TestName,
      xdisabled: Boolean,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, xdisabled, defaultConfig, TestType.Test, test)

   /**
    * Adds a new root [TestCase] to this scope with type [TestType.Test] and custom config.
    */
   fun addTest(
      name: DescriptionName.TestName,
      xdisabled: Boolean,
      config: TestCaseConfig,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, xdisabled, config, TestType.Test, test)

   /**
    * Adds a new root [TestCase] to this scope with the given test type and config.
    *
    * @param xdisabled if true then this test has been disabled by using an xKeyword dsl method.
    */
   fun addTest(
      name: DescriptionName.TestName,
      xdisabled: Boolean,
      config: TestCaseConfig,
      type: TestType,
      test: suspend TestContext.() -> Unit
   )

   companion object {
      fun from(factory: TestFactoryConfiguration): RootTestRegistration = object : RootTestRegistration {
         override val defaultConfig: TestCaseConfig = factory.resolvedDefaultConfig()
         override fun addTest(
            name: DescriptionName.TestName,
            xdisabled: Boolean,
            config: TestCaseConfig,
            type: TestType,
            test: suspend TestContext.() -> Unit
         ) {
            factory.addTest(name, test, config.withXDisabled(xdisabled), type)
         }
      }

      fun from(spec: DslDrivenSpec): RootTestRegistration = object : RootTestRegistration {
         override val defaultConfig: TestCaseConfig = spec.resolvedDefaultConfig()
         override fun addTest(
            name: DescriptionName.TestName,
            xdisabled: Boolean,
            config: TestCaseConfig,
            type: TestType,
            test: suspend TestContext.() -> Unit
         ) {
            spec.addTest(name, test, config.withXDisabled(xdisabled), type)
         }
      }
   }
}
