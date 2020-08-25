package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * "some context" { }
 * "some context".config(...) { }
 *
 */
@KotestDsl
class WordSpecShouldScope(
   override val description: Description,
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   @OptIn(ExperimentalTime::class)
   suspend fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      invocationTimeout: Duration? = null,
      test: suspend TestContext.() -> Unit
   ) = TestWithConfigBuilder(
      createTestName(this),
      testContext,
      defaultConfig,
      false,
   ).config(
      enabled,
      invocations,
      threads,
      tags,
      timeout,
      extensions,
      enabledIf,
      invocationTimeout,
      test
   )

   suspend infix operator fun String.invoke(test: suspend WordSpecTerminalScope.() -> Unit) {
      addTest(createTestName(this), xdisabled = false, test = { WordSpecTerminalScope(this).test() })
   }

   // we need to override the should method to stop people nesting a should inside a should
   @Suppress("UNUSED_PARAMETER")
   @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
   infix fun String.should(init: () -> Unit) = Unit
}
