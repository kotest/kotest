package io.kotest.assertions

import java.util.Stack

actual object ErrorCollector {

  private val clueContext = object : ThreadLocal<Stack<Any>>() {
    override fun initialValue(): Stack<Any> = Stack()
  }

  private val failures = object : ThreadLocal<MutableList<Throwable>>() {
    override fun initialValue(): MutableList<Throwable> = mutableListOf()
  }

  private val collectionMode = object : ThreadLocal<ErrorCollectionMode>() {
    override fun initialValue() = ErrorCollectionMode.Hard
  }

  actual fun setCollectionMode(mode: ErrorCollectionMode) = collectionMode.set(mode)

  actual fun getCollectionMode(): ErrorCollectionMode = collectionMode.get()

  actual fun pushClue(clue: Any) {
    clueContext.get().push(clue)
  }

  actual fun popClue() {
    clueContext.get().pop()
  }

  actual fun clueContext(): List<Any> = clueContext.get()

  actual fun errors(): List<Throwable> = failures.get().toList()

  /**
   * Adds the given error to the current context.
   */
  actual fun pushError(t: Throwable) {
    failures.get().add(t)
  }

  /**
   * Clears all errors from the current context.
   */
  actual fun clear() = failures.get().clear()
}
