package io.kotlintest.inspectors

sealed class ElementResult<out T> {
  abstract fun value(): T
  abstract fun error(): Throwable?
}

class ElementPass<out T>(val value: T) : ElementResult<T>() {
  override fun value(): T = value
  override fun error(): Throwable? = null
}

class ElementFail<out T>(val value: T, val error: Throwable) : ElementResult<T>() {
  override fun value(): T = value
  override fun error(): Throwable? = error
}