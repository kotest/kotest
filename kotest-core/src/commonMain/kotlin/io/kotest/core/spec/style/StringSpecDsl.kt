package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
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
interface StringSpecDsl : SpecDsl {

   fun String.config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val config = defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
      addTest(this, test, config, TestType.Test)
   }

   /**
    * Adds a String Spec test using the default test case config.
    */
   operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addTest(this, test, defaultConfig(), TestType.Test)
}
