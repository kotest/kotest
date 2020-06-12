package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RootTestWithConfigBuilder(
   private val name: TestName,
   private val registration: RootTestRegistration,
   private val xdisabled: Boolean
) {

   init {
      DslState.state = "Test '$name' is incomplete"
   }

   fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      test: suspend TestContext.() -> Unit
   ) {
      DslState.state = null
      val derivedConfig = registration
         .defaultConfig.deriveTestConfig(
            enabled,
            tags,
            extensions,
            timeout,
            invocationTimeout,
            enabledIf,
            invocations,
            threads
         )
      registration.addTest(name, xdisabled, derivedConfig, TestType.Test, test)
   }
}

@OptIn(ExperimentalTime::class)
class TestWithConfigBuilder(
   private val name: TestName,
   private val context: TestContext,
   private val defaultTestConfig: TestCaseConfig,
   private val xdisabled: Boolean
) {

   init {
      DslState.state = "Test '$name' is incomplete"
   }

   suspend fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      test: suspend TestContext.() -> Unit
   ) {
      DslState.state = null
      val derivedConfig =
         defaultTestConfig.deriveTestConfig(
            enabled,
            tags,
            extensions,
            timeout,
            invocationTimeout,
            enabledIf,
            invocations,
            threads
         )
      val activeConfig = if (xdisabled) derivedConfig.copy(enabled = false) else derivedConfig
      context.registerTestCase(name, test, activeConfig, TestType.Test)
   }
}
