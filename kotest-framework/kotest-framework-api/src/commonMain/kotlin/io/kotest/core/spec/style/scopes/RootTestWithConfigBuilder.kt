package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestCaseConfig
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
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      test: suspend TestContext.() -> Unit,
   ) {
      val derivedConfig = registration.defaultConfig.deriveTestCaseConfig(
         enabled,
         tags,
         extensions,
         timeout,
         invocationTimeout,
         enabledIf,
         invocations,
         threads,
         severity,
         enabledOrReasonIf = enabledOrReasonIf,
      )
      registration.addTest(name, xdisabled, derivedConfig, TestType.Test, test)
   }
}
