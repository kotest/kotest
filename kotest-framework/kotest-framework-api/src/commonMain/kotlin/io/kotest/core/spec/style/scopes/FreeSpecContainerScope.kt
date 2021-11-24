package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.UnresolvedTestConfig
import kotlin.time.Duration

@Deprecated("Renamed to FreeSpecContainerScope. Deprecated since 4.5")
typealias FreeScope = FreeSpecContainerScope

@Deprecated("Renamed to FreeSpecContainerScope. Deprecated since 5.0")
typealias FreeSpecContainerContext = FreeSpecContainerScope

@KotestTestScope
class FreeSpecContainerScope(val testScope: TestScope) : AbstractContainerScope(testScope) {

   /**
    * Creates a new container scope inside this spec.
    */
   suspend infix operator fun String.minus(test: suspend FreeSpecContainerScope.() -> Unit) {
      registerContainer(TestName(this), false, null) { FreeSpecContainerScope(this).test() }
   }

   /**
    * Creates a new terminal test scope inside this spec.
    */
   suspend infix operator fun String.invoke(test: suspend FreeSpecTerminalScope.() -> Unit) {
      registerTest(TestName(this), false, null) { FreeSpecTerminalScope(this).test() }
   }

   /**
    * Adds a configured test to this scope as a leaf test.
    *
    * eg, "this test".config(...) { }
    */
   suspend fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      test: suspend TestScope.() -> Unit,
   ) {
      TestWithConfigBuilder(
         TestName(this),
         this@FreeSpecContainerScope,
         xdisabled = false,
      ).config(
         enabled,
         invocations,
         threads,
         tags,
         timeout,
         extensions,
         enabledIf,
         invocationTimeout,
         severity,
         test
      )
   }


   /**
    * Adds the contained config and test to this scope as a container test.
    *
    * eg, "this test".config(...) - { }
    */
   suspend infix operator fun FreeSpecContextConfigBuilder.minus(test: suspend FreeSpecContainerScope.() -> Unit) {
      registerContainer(TestName(name), false, config) { FreeSpecContainerScope(this).test() }
   }

   /**
    * Starts a config builder, which can be added to the scope by invoking [minus] on the returned value.
    *
    * eg, "this test".config(...) - { }
    *
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
}
