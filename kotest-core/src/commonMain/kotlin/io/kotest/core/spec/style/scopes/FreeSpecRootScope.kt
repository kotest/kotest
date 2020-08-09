package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestName
import io.kotest.core.test.deriveTestConfig
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface FreeSpecRootScope : RootScope {

   // eg, "this test" - { } // adds a container test
   infix operator fun String.minus(test: suspend FreeScope.() -> Unit) {
      registration().addContainerTest(TestName(this), xdisabled = false) {
         FreeScope(
            description().append(this@minus),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   // "this test" { } // adds a leaf test
   infix operator fun String.invoke(test: suspend TestContext.() -> Unit) {
      registration().addTest(TestName(this), xdisabled = false, test = test)
   }

   // adds a leaf test with config
   @OptIn(ExperimentalTime::class)
   fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val config = defaultConfig().deriveTestConfig(
         enabled,
         tags,
         extensions,
         timeout,
         invocationTimeout,
         enabledIf,
         invocations,
         threads
      )
      registration().addTest(TestName(this), false, config, test)
   }
}

