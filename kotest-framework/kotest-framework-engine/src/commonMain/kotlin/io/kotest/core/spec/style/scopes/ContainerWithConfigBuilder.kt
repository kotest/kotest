package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.names.TestName
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.MetadataKey
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig
import kotlin.time.Duration

class ContainerWithConfigBuilder<T>(
   private val name: TestName,
   private val context: ContainerScope, // this should really be called scope but cannot be renamed due to keeping backwards compatibility
   private val xmethod: TestXMethod,
   private val contextFn: (TestScope) -> T
) {

   suspend fun config(config: TestConfig, test: suspend T.() -> Unit) {
      context.registerTest(
         TestDefinitionBuilder.builder(name, TestType.Container)
            .withXmethod(xmethod)
            .withConfig(config)
            .build { contextFn(this).test() }
      )
   }

   suspend fun config(
      enabled: Boolean? = null,
      enabledIf: EnabledIf? = null,
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      failfast: Boolean? = null,
      blockingTest: Boolean? = null,
      metadata: Map<MetadataKey<*>, Any> = emptyMap(),
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
         metadata = metadata,
      )
      config(config, test)
   }
}
