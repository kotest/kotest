package io.kotlintest

import io.kotlintest.Failures.removeKotlintestElementsFromStacktrace
import io.kotlintest.tables.MultiAssertionError
import java.util.Stack

@PublishedApi
internal object ErrorCollector {

  private val failures = object : ThreadLocal<MutableList<Throwable>>() {
    override fun initialValue(): MutableList<Throwable> = mutableListOf()
  }

  @PublishedApi
  internal val shouldCollectErrors = object : ThreadLocal<Boolean>() {
    override fun initialValue() = false
  }

  internal val clueContext = object: ThreadLocal<Stack<Any>>() {
    override fun initialValue(): Stack<Any> = Stack()
  }

  internal fun clueContextAsString() = clueContext.get().let { if (it.isEmpty()) "" else it.joinToString("\n", postfix = "\n") }

  @PublishedApi
  internal fun collectOrThrow(error: Throwable) {
    if (shouldCollectErrors.get()) {
      failures.get().add(error)
    } else {
      throw error
    }
  }

  @PublishedApi
  internal fun throwCollectedErrors() {
    shouldCollectErrors.set(false)
    val failures = this.failures.get()
    if (failures.isNotEmpty()) {
      this.failures.set(mutableListOf())
      if (failures.size == 1) throw failures[0]
      val error = MultiAssertionError(failures)
      if (Failures.shouldRemoveKotlintestElementsFromStacktrace) {
        removeKotlintestElementsFromStacktrace(error)
      }
      throw error
    }
  }
}
