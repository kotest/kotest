package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.plan.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestContainerConfig
import io.kotest.core.test.toTestCaseConfig
import io.kotest.core.test.toTestContainerConfig
import kotlin.time.Duration

@ExperimentalKotest
class RootContextConfigBuilder<T>(
   private val name: TestName,
   private val registration: RootTestRegistration,
   private val xdisabled: Boolean,
   val contextFn: (TestContext) -> T
) {

   @ExperimentalKotest
   fun config(
      enabled: Boolean? = null,
      enabledIf: EnabledIf? = null,
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      failfast: Boolean? = null,
      test: suspend T.() -> Unit
   ) {

      val derivedConfig = registration.defaultConfig
         .toTestContainerConfig()
         .deriveTestContainerConfig(
            enabled = enabled,
            enabledIf = enabledIf,
            enabledOrReasonIf = enabledOrReasonIf,
            tags = tags,
            timeout = timeout,
            failfast = failfast,
         )

      registration.addTest(
         name,
         xdisabled,
         derivedConfig.toTestCaseConfig(),
         TestType.Container
      ) { contextFn(this).test() }
   }
}
