package io.kotest.core.spec.style.scopes

import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.DslDrivenSpec
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

/**
 * Contains methods to register callbacks and tests in the current scope, whether
 * that be top level tests in a [DslDrivenSpec], top level tests in a [TestFactoryConfiguration],
 * or nested tests via a [TestContext].
 */
interface ScopeContext {

   val defaultConfig: TestCaseConfig

   fun addListener(listener: TestListener)

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

   suspend fun addTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   )

   companion object {
      fun from(factory: TestFactoryConfiguration): ScopeContext = FactoryLifecycle(factory)
      fun from(spec: DslDrivenSpec) = SpecLifecyle(spec)
      fun from(context: TestContext, config: TestCaseConfig) = ScopeLifecycle(context, config)
   }

   /**
    * Returns a new lifecycle using the test context and the current test case config.
    */
   fun with(context: TestContext) = ScopeLifecycle(context, defaultConfig)
}

class SpecLifecyle(private val spec: DslDrivenSpec) : ScopeContext {

   override val defaultConfig: TestCaseConfig = spec.resolvedDefaultConfig()

   override fun addListener(listener: TestListener) {
      spec.listener(listener)
   }

   override suspend fun addTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      spec.addRootTestCase(name, test, config, type)
   }
}

class ScopeLifecycle(private val context: TestContext, defaultConfig: TestCaseConfig) : ScopeContext {

   override val defaultConfig: TestCaseConfig = defaultConfig

   override fun addListener(listener: TestListener) {
      context.testCase.spec.listener(listener)
   }

   override suspend fun addTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      context.registerTestCase(name, test, config, type)
   }
}

class FactoryLifecycle(private val factory: TestFactoryConfiguration) : ScopeContext {
   override fun addListener(listener: TestListener) {
      factory.listener(listener)
   }

   override val defaultConfig: TestCaseConfig = factory.resolvedDefaultConfig()

   override suspend fun addTest(
      name: String,
      test: suspend TestContext.() -> Unit,
      config: TestCaseConfig,
      type: TestType
   ) {
      factory.addDynamicTest(name, test, config, type)
   }
}
