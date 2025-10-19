package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig
import kotlin.time.Duration

@ExperimentalKotest
class ContainerWithConfigBuilder<T>(
   private val name: TestName,
   private val context: ContainerScope,
   private val xmethod: TestXMethod,
   private val contextFn: (TestScope) -> T
) {

   suspend fun config(
      enabled: Boolean? = null,
      enabledIf: EnabledIf? = null,
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      failfast: Boolean? = null,
      blockingTest: Boolean? = null,
      test: suspend T.() -> Unit
   ) {
      val config = TestConfig(
         enabled = enabled,
         enabledIf = enabledIf,
         enabledOrReasonIf = enabledOrReasonIf,
         tags = tags ?: emptySet(),
         timeout = timeout,
         failfast = failfast,
         blockingTest = blockingTest,
      )
      context.registerContainer(name, xmethod, config) { contextFn(this).test() }
   }
}
