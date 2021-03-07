package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContainerConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.config.deriveTestContainerConfig
import io.kotest.core.test.toTestConfig
import kotlin.time.Duration

class ShouldSpecContextConfigBuilder(
   private val name: DescriptionName.TestName,
   private val description: Description,
   private val context: TestContext,
   private val defaultTestConfig: TestContainerConfig,
   private val lifecycle: Lifecycle,
   private val xdisabled: Boolean,
) {

   suspend fun config(
      enabled: Boolean? = null,
      enabledIf: EnabledIf? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      test: suspend ShouldSpecContextScope.() -> Unit
   ) {
      val derivedConfig = defaultTestConfig.deriveTestContainerConfig(
         enabled = enabled,
         enabledIf = enabledIf,
         tags = tags,
         timeout = timeout,
      )
      val activeConfig = if (xdisabled) derivedConfig.copy(enabled = false) else derivedConfig
      context.registerTestCase(name, {
         ShouldSpecContextScope(
            description,
            lifecycle,
            this,
            defaultTestConfig.toTestConfig(),
            coroutineContext
         ).test()
      }, activeConfig.toTestConfig(), TestType.Container)
   }
}
