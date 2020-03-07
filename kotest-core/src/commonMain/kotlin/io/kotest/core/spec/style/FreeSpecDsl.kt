package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface FreeSpecDsl : SpecDsl {

   infix operator fun String.minus(test: suspend FreeSpecScope.() -> Unit) =
      addTest(this, { FreeSpecScope(this, this@FreeSpecDsl).test() }, defaultConfig(), TestType.Container)

   infix operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addTest(this, test, defaultConfig(), TestType.Test)

   @OptIn(ExperimentalTime::class)
   fun String.config(
      enabled: Boolean? = null,
      timeout: Duration? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null,
      test: suspend TestContext.() -> Unit
   ) {
      val config = defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout)
      addTest(this, test, config, TestType.Test)
   }
}

class FreeSpecScope(val context: TestContext, private val dsl: FreeSpecDsl) {

   suspend infix operator fun String.minus(test: suspend FreeSpecScope.() -> Unit) =
      context.registerTestCase(this, { FreeSpecScope(this, dsl).test() }, dsl.defaultConfig(), TestType.Container)

   suspend infix operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      context.registerTestCase(this, test, dsl.defaultConfig(), TestType.Test)

   @OptIn(ExperimentalTime::class)
   suspend fun String.config(
      enabled: Boolean? = null,
      timeout: Duration? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null,
      test: suspend FreeSpecScope.() -> Unit
   ) {
      val config = dsl.defaultConfig().deriveTestConfig(enabled, tags, extensions, timeout)
      context.registerTestCase(this, { FreeSpecScope(this, dsl).test() }, config, TestType.Test)
   }
}
