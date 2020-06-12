package io.kotest.core.spec.style.scopes

import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.spec.style.DslDrivenSpec
import io.kotest.core.test.*

interface RootTestRegistration {

   val defaultConfig: TestCaseConfig

   fun addContainerTest(name: TestName, xdisabled: Boolean, test: suspend TestContext.() -> Unit) =
      addTest(name, xdisabled, defaultConfig, TestType.Container, test)

   fun addTest(
      name: TestName,
      xdisabled: Boolean,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, xdisabled, defaultConfig, TestType.Test, test)

   fun addTest(
      name: TestName,
      xdisabled: Boolean,
      config: TestCaseConfig,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, xdisabled, config, TestType.Test, test)

   fun addTest(
      name: TestName,
      xdisabled: Boolean,
      config: TestCaseConfig,
      type: TestType,
      test: suspend TestContext.() -> Unit
   )

   companion object {
      fun from(factory: TestFactoryConfiguration): RootTestRegistration = object : RootTestRegistration {
         override val defaultConfig: TestCaseConfig = factory.resolvedDefaultConfig()
         override fun addTest(
            name: TestName,
            xdisabled: Boolean,
            config: TestCaseConfig,
            type: TestType,
            test: suspend TestContext.() -> Unit
         ) {
            factory.addDynamicTest(name, test, config.withXDisabled(xdisabled), type)
         }
      }

      fun from(spec: DslDrivenSpec): RootTestRegistration = object : RootTestRegistration {
         override val defaultConfig: TestCaseConfig = spec.resolvedDefaultConfig()
         override fun addTest(
            name: TestName,
            xdisabled: Boolean,
            config: TestCaseConfig,
            type: TestType,
            test: suspend TestContext.() -> Unit
         ) {
            spec.addRootTestCase(name, test, config.withXDisabled(xdisabled), type)
         }
      }
   }
}
