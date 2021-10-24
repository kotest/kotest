package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.config.ConfigurableTestConfig
import kotlin.time.Duration

@ExperimentalKotest
class ContainerContextConfigBuilder<T>(
   private val name: TestName,
   private val context: ContainerContext,
   private val xdisabled: Boolean,
   private val contextFn: (TestContext) -> T
) {

   suspend fun config(
      enabled: Boolean? = null,
      enabledIf: EnabledIf? = null,
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      failfast: Boolean? = null,
      test: suspend T.() -> Unit
   ) {
      val config = ConfigurableTestConfig(
         enabled = enabled,
         enabledIf = enabledIf,
         enabledOrReasonIf = enabledOrReasonIf,
         tags = tags,
         timeout = timeout,
         failfast = failfast,
      )
      context.registerContainer(name, xdisabled, config) { contextFn(this).test() }
   }
}
