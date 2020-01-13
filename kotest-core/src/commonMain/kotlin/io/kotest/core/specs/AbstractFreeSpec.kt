package io.kotest.core.specs

import io.kotest.core.Tag
import io.kotest.core.test.TestType
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

abstract class AbstractFreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractSpecDsl() {

  init {
    body()
  }

  infix operator fun String.minus(test: suspend FreeSpecScope.() -> Unit) =
      addTestCase(this, { FreeSpecScope(this).test() }, defaultTestCaseConfig, TestType.Container)

  infix operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addTestCase(this, test, defaultTestCaseConfig, TestType.Test)

   @UseExperimental(ExperimentalTime::class)
   fun String.config(
      invocations: Int? = null,
      enabled: Boolean? = null,
      timeout: Duration? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null,
      test: suspend TestContext.() -> Unit) {
      val config = TestCaseConfig(
         enabled ?: defaultTestCaseConfig.enabled,
         invocations ?: defaultTestCaseConfig.invocations,
         timeout ?: defaultTestCaseConfig.timeout,
         threads ?: defaultTestCaseConfig.threads,
         tags ?: defaultTestCaseConfig.tags,
         extensions ?: defaultTestCaseConfig.extensions
      )
      addTestCase(this, test, config, TestType.Test)
   }

  inner class FreeSpecScope(val context: TestContext) {

    suspend infix operator fun String.minus(test: suspend FreeSpecScope.() -> Unit) =
        context.registerTestCase(this, this@AbstractFreeSpec, { FreeSpecScope(this).test() }, defaultTestCaseConfig, TestType.Container)

    suspend infix operator fun String.invoke(test: suspend TestContext.() -> Unit) =
        context.registerTestCase(this, this@AbstractFreeSpec, test, defaultTestCaseConfig, TestType.Test)

     @UseExperimental(ExperimentalTime::class)
     suspend fun String.config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: suspend FreeSpecScope.() -> Unit) {
        val config = TestCaseConfig(
           enabled ?: defaultTestCaseConfig.enabled,
           invocations ?: defaultTestCaseConfig.invocations,
           timeout ?: defaultTestCaseConfig.timeout,
           threads ?: defaultTestCaseConfig.threads,
           tags ?: defaultTestCaseConfig.tags,
           extensions ?: defaultTestCaseConfig.extensions
        )
        context.registerTestCase(this, this@AbstractFreeSpec, { FreeSpecScope(this).test() }, config, TestType.Test)
     }
  }
}
