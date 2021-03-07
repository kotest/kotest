package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName
import kotlin.time.Duration

/**
 * Defines the DSL for creating tests in the 'StringSpec' style.
 *
 * Example:
 *
 * "my test" {
 *   1 + 1 shouldBe 2
 * }
 *
 */
interface StringSpecRootScope : RootScope {

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
      test: suspend TestContext.() -> Unit,
   ) = RootTestWithConfigBuilder(createTestName(null, this, false), registration(), false)
      .config(
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

   /**
    * Adds a String Spec test using the default test case config.
    */
   operator fun String.invoke(test: suspend StringSpecScope.() -> Unit) =
      registration().addTest(
         createTestName(null, this, false),
         xdisabled = false,
         test = { StringSpecScope(this.coroutineContext, testCase).test() }
      )
}

/**
 * This scope exists purely to stop nested string scopes.
 */
class StringSpecScope(
   override val coroutineContext: CoroutineContext,
   override val testCase: TestCase
) : TestContext {

   @Deprecated("Cannot nest string scope tests", level = DeprecationLevel.ERROR)
   @JvmName("nestedStringInvoke")
   operator fun String.invoke(test: suspend StringSpecScope.() -> Unit) {
   }

   override suspend fun registerTestCase(nested: NestedTest) {
      error("Invalid state")
   }
}
