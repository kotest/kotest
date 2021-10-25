package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.config.UnresolvedTestConfig
import kotlin.time.Duration

@Deprecated("Renamed to FreeSpecRootContext. Deprecated since 4.5.")
typealias FreeSpecRootScope = FreeSpecRootContext

data class FreeSpecContextConfigBuilder(val name: String, val config: UnresolvedTestConfig)

interface FreeSpecRootContext : RootContext {

   // eg, "this test" - { } // adds a container test
   infix operator fun String.minus(test: suspend FreeSpecContainerContext.() -> Unit) {
      addContainer(TestName(this), false, null) { FreeSpecContainerContext(this).test() }
   }

   // "this test" { } // adds a leaf test
   infix operator fun String.invoke(test: suspend FreeSpecTerminalContext.() -> Unit) {
      addTest(TestName(this), false, null) { FreeSpecTerminalContext(this).test() }
   }

   /**
    * Starts a config builder, which can be added to the scope by invoking [minus] on the returned value.
    *
    * eg, "this test".config(...) - { }
    */
   fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      failfast: Boolean? = null,
   ): FreeSpecContextConfigBuilder {
      val config = UnresolvedTestConfig(
         enabled = enabled,
         tags = tags,
         extensions = extensions,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         enabledIf = enabledIf,
         invocations = invocations,
         threads = threads,
         severity = severity,
         failfast = failfast,
      )
      return FreeSpecContextConfigBuilder(this, config)
   }

   /**
    * Adds the contained config and test to this scope as a container test.
    *
    * eg, "this test".config(...) - { }
    */
   infix operator fun FreeSpecContextConfigBuilder.minus(test: suspend FreeSpecContainerContext.() -> Unit) {
      addContainer(TestName(name), false, config) { FreeSpecContainerContext(this).test() }
   }

   /**
    * Adds a configured test to this scope as a leaf test.
    *
    * eg, "this test".config(...) { }
    */
   fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      failfast: Boolean? = null,
      test: suspend TestContext.() -> Unit,
   ) {
      val config = UnresolvedTestConfig(
         enabled = enabled,
         tags = tags,
         extensions = extensions,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         enabledIf = enabledIf,
         invocations = invocations,
         threads = threads,
         severity = severity,
         failfast = failfast
      )
      addTest(TestName(this), false, config, test)
   }
}

