package io.kotest.core.spec.style.scopes

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RootTestWithConfigBuilder(
   private val name: String,
   private val r: RootTestRegistration
) {
   fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val config = r.defaultConfig.deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
      r.addTest(name, test, config, TestType.Test)
   }
}

@OptIn(ExperimentalTime::class)
class TestWithConfigBuilder(
   private val name: String,
   private val lifecycle: ScopeContext
) {
   suspend fun config(
      enabled: Boolean? = null,
      invocations: Int? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      timeout: Duration? = null,
      extensions: List<TestCaseExtension>? = null,
      enabledIf: EnabledIf? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val config =
         lifecycle.defaultConfig.deriveTestConfig(enabled, tags, extensions, timeout, enabledIf, invocations, threads)
      lifecycle.addTest(name, test, config, TestType.Test)
   }
}
