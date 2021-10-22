package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import io.kotest.core.test.deriveTestContainerConfig
import io.kotest.core.test.toTestCaseConfig
import io.kotest.core.test.toTestContainerConfig
import kotlin.time.Duration

@ExperimentalKotest
class ContainerContextConfigBuilder<T>(
   private val name: TestName,
   private val context: TestContext,
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

      val derivedConfig = context.testCase.config.toTestContainerConfig().deriveTestContainerConfig(
         enabled = enabled,
         enabledIf = enabledIf,
         enabledOrReasonIf = enabledOrReasonIf,
         tags = tags,
         timeout = timeout,
         failfast = failfast,
      )

      val activeConfig = if (xdisabled) derivedConfig.copy(enabled = false) else derivedConfig

      context.registerTestCase(
         createNestedTest(
            descriptor = context.testCase.descriptor.append(name),
            name = name,
            xdisabled = xdisabled,
            config = activeConfig.toTestCaseConfig(),
            type = TestType.Container,
            factoryId = context.testCase.factoryId,
            test = { contextFn(this).test() },
         )
      )
   }
}
