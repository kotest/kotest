package io.kotlintest.matchers

import io.kotlintest.Failures
import io.kotlintest.Failures.removeKotlintestElementsFromStacktrace
import io.kotlintest.MultiAssertionError

actual object AssertionErrorCollector {

  private val failures = object : ThreadLocal<MutableList<Throwable>>() {
    override fun initialValue(): MutableList<Throwable> = mutableListOf()
  }

  private val clues = object : ThreadLocal<String>() {
    override fun initialValue(): String = ""
  }

  @PublishedApi
  internal val shouldCollectErrors = object : ThreadLocal<Boolean>() {
    override fun initialValue() = false
  }

  actual fun setClueContext(context: String?) {
    clues.set(context)
  }

  actual fun collectOrThrow(error: Throwable) {
    if (shouldCollectErrors.get()) {
      failures.get().add(error)
    } else {
      throw error
    }
  }

  actual fun throwCollectedErrors() {
    shouldCollectErrors.set(false)
    val failures = failures.get()
    if (failures.isNotEmpty()) {
      AssertionErrorCollector.failures.set(mutableListOf())
      if (failures.size == 1) throw failures[0]
      val error = MultiAssertionError(failures)
      if (Failures.shouldRemoveKotlintestElementsFromStacktrace) {
        removeKotlintestElementsFromStacktrace(error)
      }
      throw error
    }
  }

  actual fun getClueContext(): String = clues.get() ?: ""
}