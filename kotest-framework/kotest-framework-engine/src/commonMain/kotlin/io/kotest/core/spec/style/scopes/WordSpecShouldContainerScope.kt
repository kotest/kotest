package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestScope
import io.kotest.datatest.WithDataTerminalRegistrar
import kotlin.time.Duration

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * ```
 * "some context" { }
 * ```
 *
 * or
 *
 * ```
 * "some context".config(...) { }
 * ```
 *
 */
@KotestTestScope
class WordSpecShouldContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope), WithDataTerminalRegistrar<WordSpecTerminalScope> {

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
      test: suspend TestScope.() -> Unit
   ) {
      TestWithConfigBuilder(
         TestNameBuilder.builder(this).build(),
         context = this@WordSpecShouldContainerScope,
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
         test = test
      )
   }

   suspend infix operator fun String.invoke(test: suspend WordSpecTerminalScope.() -> Unit) {
      registerTest(TestNameBuilder.builder(this).build(), false, null) { WordSpecTerminalScope(this).test() }
   }
}
