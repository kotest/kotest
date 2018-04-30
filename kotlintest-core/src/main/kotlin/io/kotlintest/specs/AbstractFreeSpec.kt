package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContext
import io.kotlintest.extensions.TestCaseExtension
import java.time.Duration

abstract class AbstractFreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  @Deprecated("You can now nest contexts without needing the minus symbol")
  infix operator fun String.minus(test: FreeSpecContext.() -> Unit) = this.invoke(test)

  infix operator fun String.invoke(test: FreeSpecContext.() -> Unit) =
      addTestCase(this, { FreeSpecContext(this).test() }, defaultTestCaseConfig)

  fun String.config(
      invocations: Int? = null,
      enabled: Boolean? = null,
      timeout: Duration? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null,
      test: FreeSpecContext.() -> Unit) {
    val config = TestCaseConfig(
        enabled ?: defaultTestCaseConfig.enabled,
        invocations ?: defaultTestCaseConfig.invocations,
        timeout ?: defaultTestCaseConfig.timeout,
        threads ?: defaultTestCaseConfig.threads,
        tags ?: defaultTestCaseConfig.tags,
        extensions ?: defaultTestCaseConfig.extensions)
    addTestCase(this, { FreeSpecContext(this).test() }, config)
  }

  inner class FreeSpecContext(val context: TestContext) {

    @Deprecated("You can now nest contexts without needing the minus symbol")
    infix operator fun String.minus(test: FreeSpecContext.() -> Unit) = this.invoke(test)

    infix operator fun String.invoke(test: FreeSpecContext.() -> Unit) {
      context.registerTestScope(this, this@AbstractFreeSpec, { FreeSpecContext(this).test() }, defaultTestCaseConfig)
    }

    fun String.config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: FreeSpecContext.() -> Unit) {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      context.registerTestScope(this, this@AbstractFreeSpec, { FreeSpecContext(this).test() }, config)
    }
  }
}