package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig
import kotlin.time.Duration

class RootTestWithConfigBuilder(
   private val context: RootScope,
   private val name: TestName,
   private val focused: Boolean,
   private val xdisabled: Boolean,
) {

   fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      coroutineDebugProbes: Boolean? = null,
      blockingTest: Boolean? = null,
      coroutineTestScope: Boolean? = null,
      retries: Int? = null,
      retryDelay: Duration? = null,
      test: suspend TestScope.() -> Unit,
   ) {
      val config = TestConfig(
         enabled = enabled,
         tags = tags ?: emptySet(),
         extensions = extensions,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         enabledIf = enabledIf,
         invocations = invocations,
         severity = severity,
         enabledOrReasonIf = enabledOrReasonIf,
         coroutineDebugProbes = coroutineDebugProbes,
         blockingTest = blockingTest,
         coroutineTestScope = coroutineTestScope,
         retries = retries,
         retryDelay = retryDelay,
      )
      context.addTest(testName = name, focused = focused, disabled = xdisabled, config = config, test = test)
   }
}
