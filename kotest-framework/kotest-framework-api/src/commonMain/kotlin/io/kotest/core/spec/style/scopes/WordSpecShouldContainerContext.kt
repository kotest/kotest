package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.descriptors.append
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import kotlin.time.Duration

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * "some context" { }
 *
 * or
 *
 * "some context".config(...) { }
 *
 */
@KotestDsl
class WordSpecShouldContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> error("Containers cannot be added to this context")
         TestType.Test -> name(test)
      }
   }

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
      test: suspend TestContext.() -> Unit
   ) = TestWithConfigBuilder(
      TestName(this),
      testContext,
      testCase.spec.resolvedDefaultConfig(),
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
      severity,
      test
   )

   suspend infix operator fun String.invoke(test: suspend WordSpecTerminalContext.() -> Unit) {
      registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(this),
            name = TestName(this),
            xdisabled = false,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Test,
            factoryId = testCase.factoryId,
            test = { WordSpecTerminalContext(this).test() }
         )
      )
   }

   // we need to override the should method to stop people nesting a should inside a should
   @Suppress("UNUSED_PARAMETER")
   @Deprecated("A should block can only be used at the top level", ReplaceWith("{}"), level = DeprecationLevel.ERROR)
   infix fun String.should(init: () -> Unit) = Unit
}
