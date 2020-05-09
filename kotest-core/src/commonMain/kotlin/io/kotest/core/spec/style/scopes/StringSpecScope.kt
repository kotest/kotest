package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

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
@OptIn(ExperimentalTime::class)
interface StringSpecScope : RootScope {

   fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      test: suspend TestContext.() -> Unit
   ) = RootTestWithConfigBuilder(this, registration(), false).config(
      enabled,
      invocations,
      threads,
      tags,
      timeout,
      extensions,
      enabledIf,
      test
   )

   /**
    * Adds a String Spec test using the default test case config.
    */
   operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      registration().addTest(this, xdisabled = false, test = test)
}
