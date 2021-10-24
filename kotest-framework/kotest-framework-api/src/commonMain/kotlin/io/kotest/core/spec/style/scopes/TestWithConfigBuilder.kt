package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.descriptors.append
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.config.ConfigurableTestConfig
import kotlin.time.Duration

class TestWithConfigBuilder(
   private val name: TestName,
   private val context: ContainerContext,
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

      TestDslState.clear(context.testCase.descriptor.append(name))

      val config = ConfigurableTestConfig(
         enabled = enabled,
         enabledIf = enabledIf,
         tags = tags,
         extensions = extensions,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         invocations = invocations,
         threads = threads,
         severity = severity
      )

      context.registerTest(name, xdisabled, config, test)
   }
}
