package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.deriveTestConfig
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration

class TestWithConfigBuilder(
   private val name: DescriptionName.TestName,
   private val context: TestContext,
   private val defaultTestConfig: TestCaseConfig,
   private val xdisabled: Boolean,
) {

   suspend fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val derivedConfig = defaultTestConfig.deriveTestConfig(
         enabled,
         tags,
         extensions,
         timeout,
         invocationTimeout,
         enabledIf,
         invocations,
         threads,
         severity
      )
      val activeConfig = if (xdisabled) derivedConfig.copy(enabled = false) else derivedConfig
      context.registerTestCase(name, test, activeConfig, TestType.Test)
   }
}
