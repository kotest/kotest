package io.kotest.core.spec.style.scopes

import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.spec.style.DslDrivenSpec
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

interface RootTestRegistration {

   val defaultConfig: TestCaseConfig

   fun addContainerTest(name: String, xdisabled: Boolean, test: suspend TestContext.() -> Unit) =
      addTest(name, xdisabled, defaultConfig, TestType.Container, test)

   fun addTest(
      name: String,
      xdisabled: Boolean,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, xdisabled, defaultConfig, TestType.Test, test)

   fun addTest(
      name: String,
      xdisabled: Boolean,
      config: TestCaseConfig,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, xdisabled, config, TestType.Test, test)

   fun addTest(
      name: String,
      xdisabled: Boolean,
      config: TestCaseConfig,
      type: TestType,
      test: suspend TestContext.() -> Unit
   )

   companion object {
      fun from(factory: TestFactoryConfiguration): RootTestRegistration = object : RootTestRegistration {
         override val defaultConfig: TestCaseConfig = factory.resolvedDefaultConfig()
         override fun addTest(
            name: String,
            xdisabled: Boolean,
            config: TestCaseConfig,
            type: TestType,
            test: suspend TestContext.() -> Unit
         ) {
            val activeConfig = if (xdisabled) config.copy(enabled = false) else config
            factory.addDynamicTest(name, test, activeConfig, type)
         }
      }

      fun from(spec: DslDrivenSpec): RootTestRegistration = object : RootTestRegistration {
         override val defaultConfig: TestCaseConfig = spec.resolvedDefaultConfig()
         override fun addTest(
            name: String,
            xdisabled: Boolean,
            config: TestCaseConfig,
            type: TestType,
            test: suspend TestContext.() -> Unit
         ) {
            val activeConfig = if (xdisabled) config.copy(enabled = false) else config
            spec.addRootTestCase(name, test, activeConfig, type)
         }
      }
   }
}
