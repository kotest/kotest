package io.kotest.core.spec.style.scopes

import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.spec.style.DslDrivenSpec
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

interface RootTestRegistration {

   val defaultConfig: TestCaseConfig

   suspend fun addContainerTest(name: String, test: suspend TestContext.() -> Unit) =
      addTest(name, test, TestType.Container)

   suspend fun addContainerTest(name: String, enabled: Boolean, test: suspend TestContext.() -> Unit) {
      val config = if (enabled) defaultConfig else defaultConfig.copy(enabled = false)
      addTest(name, test, config, TestType.Container)
   }

   suspend fun addTest(
      name: String,
      test: suspend TestContext.() -> Unit
   ) = addTest(name, test, defaultConfig, TestType.Test)

   suspend fun addTest(
       name: String,
       test: suspend TestContext.() -> Unit,
       type: TestType
   ) = addTest(name, test, defaultConfig, type)

   fun addTest(
       name: String,
       test: suspend TestContext.() -> Unit,
       config: TestCaseConfig,
       type: TestType
   )

   companion object {
      fun from(factory: TestFactoryConfiguration): RootTestRegistration = object :
          RootTestRegistration {

         override val defaultConfig: TestCaseConfig = factory.resolvedDefaultConfig()

         override fun addTest(
             name: String,
             test: suspend TestContext.() -> Unit,
             config: TestCaseConfig,
             type: TestType
         ) = factory.addDynamicTest(name, test, config, type)
      }

      fun from(spec: DslDrivenSpec): RootTestRegistration = object :
          RootTestRegistration {

         override val defaultConfig: TestCaseConfig = spec.resolvedDefaultConfig()

         override fun addTest(
             name: String,
             test: suspend TestContext.() -> Unit,
             config: TestCaseConfig,
             type: TestType
         ) = spec.addRootTestCase(name, test, config, type)
      }
   }
}
