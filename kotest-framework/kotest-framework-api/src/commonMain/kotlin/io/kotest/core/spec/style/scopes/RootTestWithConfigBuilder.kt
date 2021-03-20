package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrCauseIf
import io.kotest.core.test.IsActive
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
import io.kotest.core.test.TestCaseSeverityLevel
import kotlin.time.Duration

class RootTestWithConfigBuilder(
   private val name: DescriptionName.TestName,
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
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      enabledOrCause: IsActive? = null,
      enabledOrCauseIf: EnabledOrCauseIf? = null,
      test: suspend TestContext.() -> Unit,
   ) {
      val derivedConfig = registration.defaultConfig.deriveTestConfig(
         enabled,
         tags,
         extensions,
         timeout,
         invocationTimeout,
         enabledIf,
         invocations,
         threads,
         severity,
         enabledOrCause = enabledOrCause,
         enabledOrCauseIf = enabledOrCauseIf,
      )
      registration.addTest(name, xdisabled, derivedConfig, TestType.Test, test)
   }
}
