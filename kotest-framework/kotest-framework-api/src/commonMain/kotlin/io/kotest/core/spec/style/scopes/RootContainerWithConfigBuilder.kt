package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.Tag
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.EnabledOrReasonIf
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.UnresolvedTestConfig
import kotlin.time.Duration

@ExperimentalKotest
class RootContainerWithConfigBuilder<T>(
   private val name: TestName,
   private val xdisabled: Boolean,
   private val context: RootScope,
   val contextFn: (TestScope) -> T
) {

   @ExperimentalKotest
   fun config(
      enabled: Boolean? = null,
      enabledIf: EnabledIf? = null,
      enabledOrReasonIf: EnabledOrReasonIf? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      failfast: Boolean? = null,
      blockingTest: Boolean? = null,
      test: suspend T.() -> Unit
   ) {
      val config = UnresolvedTestConfig(
         enabled = enabled,
         enabledIf = enabledIf,
         enabledOrReasonIf = enabledOrReasonIf,
         tags = tags,
         timeout = timeout,
         failfast = failfast,
         blockingTest = blockingTest,
      )
      context.addContainer(name, xdisabled, config) { contextFn(this).test() }
   }
}
