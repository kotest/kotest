package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig
import kotlin.time.Duration

class FreeSpecContainerScope(val testScope: TestScope) : AbstractContainerScope(testScope) {

   /**
    * Creates a new container scope inside this spec.
    */
   suspend infix operator fun String.minus(test: suspend FreeSpecContainerScope.() -> Unit) {
      registerContainer(
         name = TestNameBuilder.builder(this).build(),
         disabled = false,
         config = null
      ) { FreeSpecContainerScope(this).test() }
   }

   /**
    * Creates a new terminal test scope inside this spec.
    */
   suspend infix operator fun String.invoke(test: suspend FreeSpecTerminalScope.() -> Unit) {
      registerTest(TestNameBuilder.builder(this).build(), false, null) { FreeSpecTerminalScope(this).test() }
   }

   /**
    * Adds a configured test to this scope as a leaf test.
    *
    * E.g.
    * ```
    * "this test".config(...) { }
    * ```
    */
   suspend fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      blockingTest: Boolean? = null,
      coroutineTestScope: Boolean? = null,
      test: suspend TestScope.() -> Unit,
   ) {
      TestWithConfigBuilder(
         TestNameBuilder.builder(this).build(),
         this@FreeSpecContainerScope,
         xdisabled = false,
      ).config(
         enabled = enabled,
         invocations = invocations,
         tags = tags,
         timeout = timeout,
         extensions = extensions,
         enabledIf = enabledIf,
         invocationTimeout = invocationTimeout,
         severity = severity,
         blockingTest = blockingTest,
         coroutineTestScope = coroutineTestScope,
         test = test
      )
   }

   suspend fun String.config(
      config: TestConfig,
      test: suspend TestScope.() -> Unit,
   ) {
      TestWithConfigBuilder(
         name = TestNameBuilder.builder(this).build(),
         context = this@FreeSpecContainerScope,
         xdisabled = false,
      ).config(
         config = config,
         test = test,
      )
   }


   /**
    * Adds the contained config and test to this scope as a container test.
    *
    * E.g.
    * ```
    * "this test".config(...) - { }
    * ```
    */
   suspend infix operator fun FreeSpecContextConfigBuilder.minus(test: suspend FreeSpecContainerScope.() -> Unit) {
      registerContainer(TestNameBuilder.builder(name).build(), false, config) { FreeSpecContainerScope(this).test() }
   }

   /**
    * Starts a config builder, which can be added to the scope by invoking [minus] on the returned value.
    *
    * E.g.
    * ```
    * "this test".config(...) - { }
    * ```
    */
   fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      severity: TestCaseSeverityLevel? = null,
      failfast: Boolean? = null,
   ): FreeSpecContextConfigBuilder {
      val config = TestConfig(
         enabled = enabled,
         tags = tags ?: emptySet(),
         extensions = extensions,
         timeout = timeout,
         invocationTimeout = invocationTimeout,
         enabledIf = enabledIf,
         invocations = invocations,
         severity = severity,
         failfast = failfast,
      )
      return FreeSpecContextConfigBuilder(this, config)
   }
}
