package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ConfigurableTestConfig
import kotlin.time.Duration

@Deprecated("Renamed to FreeSpecContainerContext. Deprecated since 4.5.")
typealias FreeScope = FreeSpecContainerContext

class FreeSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> name.minus(test)
         TestType.Test -> name.invoke(test)
      }
   }

   /**
    * Creates a new container scope inside this spec.
    */
   suspend infix operator fun String.minus(test: suspend FreeSpecContainerContext.() -> Unit) {
      registerContainer(TestName(this), false, null) { FreeSpecContainerContext(this).test() }
   }

   /**
    * Creates a new terminal test scope inside this spec.
    */
   suspend infix operator fun String.invoke(test: suspend FreeSpecTerminalContext.() -> Unit) {
      registerTest(TestName(this), false, null) { FreeSpecTerminalContext(this).test() }
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
      test: suspend TestContext.() -> Unit,
   ) {
      TestWithConfigBuilder(
         TestName(this),
         this@FreeSpecContainerContext,
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
   suspend infix operator fun FreeSpecContextConfigBuilder.minus(test: suspend FreeSpecContainerContext.() -> Unit) {
      registerContainer(TestName(name), false, config) { FreeSpecContainerContext(this).test() }
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
      val config = ConfigurableTestConfig(
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
