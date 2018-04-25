package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.Tag
import io.kotlintest.TestCase
import io.kotlintest.TestCaseConfig
import io.kotlintest.TestContainer
import io.kotlintest.TestContext
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.lineNumber
import java.time.Duration

abstract class AbstractFreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  final override fun isInstancePerTest(): Boolean = false

  infix operator fun String.minus(init: FreeSpecContext.() -> Unit) =
      addRootScope(TestContainer(rootDescription().append(this), this@AbstractFreeSpec::class, { FreeSpecContext(it).init() }))

  fun String.config(
      invocations: Int? = null,
      enabled: Boolean? = null,
      timeout: Duration? = null,
      threads: Int? = null,
      tags: Set<Tag>? = null,
      extensions: List<TestCaseExtension>? = null,
      test: TestContext.() -> Unit): TestCase {
    val config = TestCaseConfig(
        enabled ?: defaultTestCaseConfig.enabled,
        invocations ?: defaultTestCaseConfig.invocations,
        timeout ?: defaultTestCaseConfig.timeout,
        threads ?: defaultTestCaseConfig.threads,
        tags ?: defaultTestCaseConfig.tags,
        extensions ?: defaultTestCaseConfig.extensions)
    val tc = TestCase(rootDescription().append("should " + this), this@AbstractFreeSpec, test, lineNumber(), config)
    addRootScope(tc)
    return tc
  }

  infix operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
    val tc = TestCase(rootDescription().append(this), this@AbstractFreeSpec, test, lineNumber(), defaultTestCaseConfig)
    addRootScope(tc)
    return tc
  }

  inner class FreeSpecContext(val context: TestContext) {

    infix operator fun String.minus(init: FreeSpecContext.() -> Unit) =
        context.executeScope(TestContainer(context.currentScope().description().append(this), this@AbstractFreeSpec::class, { FreeSpecContext(it).init() }))

    fun String.config(
        invocations: Int? = null,
        enabled: Boolean? = null,
        timeout: Duration? = null,
        threads: Int? = null,
        tags: Set<Tag>? = null,
        extensions: List<TestCaseExtension>? = null,
        test: TestContext.() -> Unit): TestCase {
      val config = TestCaseConfig(
          enabled ?: defaultTestCaseConfig.enabled,
          invocations ?: defaultTestCaseConfig.invocations,
          timeout ?: defaultTestCaseConfig.timeout,
          threads ?: defaultTestCaseConfig.threads,
          tags ?: defaultTestCaseConfig.tags,
          extensions ?: defaultTestCaseConfig.extensions)
      val tc = TestCase(context.currentScope().description().append(this), this@AbstractFreeSpec, test, lineNumber(), config)
      context.executeScope(tc)
      return tc
    }

    infix operator fun String.invoke(test: TestContext.() -> Unit): TestCase {
      val tc = TestCase(context.currentScope().description().append(this), this@AbstractFreeSpec, test, lineNumber(), defaultTestCaseConfig)
      context.executeScope(tc)
      return tc
    }
  }
}