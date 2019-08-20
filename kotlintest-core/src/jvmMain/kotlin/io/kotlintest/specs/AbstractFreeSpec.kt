package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestType
import io.kotlintest.core.TestCaseConfig
import io.kotlintest.core.TestContext
import io.kotlintest.extensions.TestCaseExtension

abstract class AbstractFreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  infix operator fun String.minus(test: suspend FreeSpecScope.() -> Unit) =
      addTestCase(this, { FreeSpecScope(this).test() }, defaultTestCaseConfig, TestType.Container)

  infix operator fun String.invoke(test: suspend TestContext.() -> Unit) =
      addTestCase(this, test, defaultTestCaseConfig, TestType.Test)

  fun String.config(
      invocations: Int? = null,
      enabled: Boolean? = null,
      timeout: Long? = null,
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
        extensions ?: defaultTestCaseConfig.extensions)
    addTestCase(this, test, config, TestType.Test)
  }

  inner class FreeSpecScope(val context: TestContext) {

    suspend infix operator fun String.minus(test: suspend FreeSpecScope.() -> Unit) =
        context.registerTestCase(this, this@AbstractFreeSpec, { FreeSpecScope(this).test() }, defaultTestCaseConfig, TestType.Container)

    suspend infix operator fun String.invoke(test: suspend TestContext.() -> Unit) =
        context.registerTestCase(this, this@AbstractFreeSpec, test, defaultTestCaseConfig, TestType.Test)

    suspend fun String.config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Long? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: FreeSpecScope.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      context.registerTestCase(this, this@AbstractFreeSpec, { FreeSpecScope(this).test() }, config, TestType.Test)
    }
  }
}
