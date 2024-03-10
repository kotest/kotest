package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.descriptors.append
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig
import kotlin.time.Duration

class TestWithConfigBuilder(
   private val name: TestName,
   private val context: ContainerScope,
   private val xdisabled: Boolean,
) {

   suspend fun config(config: TestConfig, test: suspend TestScope.() -> Unit) {
      TestDslState.clear(context.testCase.descriptor.append(name))
      context.registerTest(name, xdisabled, config, test)
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
      severity: TestCaseSeverityLevel? = null,
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      blockingTest: Boolean? = null,
      coroutineTestScope: Boolean? = null,
      test: suspend TestScope.() -> Unit
   ) {
      val config = TestConfig(
         enabled = enabled,
         enabledIf = enabledIf,
         enabledOrReasonIf = enabledOrReasonIf,
         tags = tags,
         extensions = extensions,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         invocations = invocations,
         threads = threads,
         severity = severity,
         blockingTest = blockingTest,
         coroutineTestScope = coroutineTestScope,
      )
      config(config, test)
   }
}
