package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestCaseConfig
import kotlin.time.Duration

@Deprecated("Renamed to FreeSpecRootContext. Deprecated since 4.5.")
typealias FreeSpecRootScope = FreeSpecRootContext

data class FreeSpecContextConfigBuilder(val name: String, val config: TestCaseConfig)

interface FreeSpecRootContext : RootContext {

   // eg, "this test" - { } // adds a container test
   infix operator fun String.minus(test: suspend FreeSpecContainerContext.() -> Unit) {
      val testName = TestName(this)
      registration().addContainerTest(testName, xdisabled = false) {
         val incomplete = IncompleteContainerContext(this)
         FreeSpecContainerContext(incomplete).test()
         if (!incomplete.registered) throw IncompleteContainerException(testName.testName)
      }
   }

   // "this test" { } // adds a leaf test
   infix operator fun String.invoke(test: suspend FreeSpecTerminalContext.() -> Unit) {
      registration().addTest(TestName(this), xdisabled = false, test = { FreeSpecTerminalContext(this).test() })
   }

   // eg, "this test".config(...) - { } // adds a container test with config
   infix operator fun FreeSpecContextConfigBuilder.minus(test: suspend FreeSpecContainerContext.() -> Unit) {
      val testName = TestName(name)
      registration().addTest(testName, xdisabled = false, config = config, type = TestType.Container) {
         val incomplete = IncompleteContainerContext(this)
         FreeSpecContainerContext(incomplete).test()
         if (!incomplete.registered) throw IncompleteContainerException(testName.testName)
      }
   }

   // starts a config builder for a context with config
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
      val config = defaultConfig().deriveTestCaseConfig(
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

   // starts a config builder for free spec
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
      val config = defaultConfig().deriveTestCaseConfig(
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
      registration().addTest(TestName(this), xdisabled = false, type = TestType.Test, config = config, test = test)
   }
}

