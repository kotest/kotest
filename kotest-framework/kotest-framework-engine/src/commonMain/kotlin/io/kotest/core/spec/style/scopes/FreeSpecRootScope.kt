package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig
import io.kotest.datatest.WithDataRootRegistrar
import kotlin.time.Duration

data class FreeSpecContextConfigBuilder(val name: String, val config: TestConfig)

interface FreeSpecRootScope : RootScope, WithDataRootRegistrar<FreeSpecContainerScope> {

   // eg, "this test" - { } // adds a container test
   infix operator fun String.minus(test: suspend FreeSpecContainerScope.() -> Unit) {
      addContainer(TestNameBuilder.builder(this).build(), false, null) { FreeSpecContainerScope(this).test() }
   }

   // "this test" { } // adds a leaf test
   infix operator fun String.invoke(test: suspend FreeSpecTerminalScope.() -> Unit) {
      addTest(TestNameBuilder.builder(this).build(), false, null) { FreeSpecTerminalScope(this).test() }
   }

   /**
    * Starts a config builder, which can be added to the scope by invoking [minus] on the returned value.
    *
    * E.g.
    *
    * ```
    * "this test".config(...) - { }
    * ```
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
      blockingTest: Boolean? = null,
      coroutineTestScope: Boolean? = null,
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
         blockingTest = blockingTest,
         coroutineTestScope = coroutineTestScope,
      )
      return config(config)
   }

   /**
    * Starts a config builder, which can be added to the scope by invoking [minus] on the returned value.
    *
    * E.g.
    *
    * ```
    * "this test".config(...) - { }
    * ```
    */
   fun String.config(
      config: TestConfig,
   ): FreeSpecContextConfigBuilder {
      return FreeSpecContextConfigBuilder(this, config)
   }

   /**
    * Adds the contained config and test to this scope as a container test.
    *
    * E.g.
    * ```
    * "this test".config(...) - { }
    * ```
    */
   infix operator fun FreeSpecContextConfigBuilder.minus(test: suspend FreeSpecContainerScope.() -> Unit) {
      addContainer(TestNameBuilder.builder(name).build(), false, config) { FreeSpecContainerScope(this).test() }
   }

   /**
    * Adds a configured test to this scope as a leaf test.
    *
    * E.g.
    * ```
    * "this test".config(...) { }
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
      blockingTest: Boolean? = null,
      coroutineTestScope: Boolean? = null,
      test: suspend TestScope.() -> Unit,
   ) {
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
         blockingTest = blockingTest,
         coroutineTestScope = coroutineTestScope,
      )
      addTest(TestNameBuilder.builder(this).build(), false, config, test)
   }

   fun String.config(config: TestConfig, test: suspend TestScope.() -> Unit): FreeSpecContextConfigBuilder {
      return FreeSpecContextConfigBuilder(this, config)
   }

   override fun registerWithDataTest(
      name: String,
      test: suspend FreeSpecContainerScope.() -> Unit
   ) {
      name.minus { test() }
   }
}
