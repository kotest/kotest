package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RootTestWithConfigBuilder(
   private val name: String,
   private val registration: RootTestRegistration,
   private val xdisabled: Boolean
) {
   fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val derivedConfig = registration
         .defaultConfig.deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
      registration.addTest(name, xdisabled, derivedConfig, TestType.Test, test)
   }
}

@OptIn(ExperimentalTime::class)
class TestWithConfigBuilder(
   private val name: String,
   private val context: TestContext,
   private val defaultTestConfig: TestCaseConfig,
   private val xdisabled: Boolean
) {
   suspend fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val derivedConfig =
         defaultTestConfig.deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
      val activeConfig = if (xdisabled) derivedConfig.copy(enabled = false) else derivedConfig
      context.registerTestCase(name, test, activeConfig, TestType.Test)
   }
}
