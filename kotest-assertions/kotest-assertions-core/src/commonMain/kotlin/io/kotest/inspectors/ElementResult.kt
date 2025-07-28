package io.kotest.inspectors

@PublishedApi
internal sealed class ElementResult<out T> {
  abstract fun value(): T
  abstract fun error(): Throwable?
}

@PublishedApi
internal class ElementPass<out T>(val index: Int, val t: T) : ElementResult<T>() {
  override fun value(): T = t
  override fun error(): Throwable? = null
}

@PublishedApi
internal class ElementFail<out T>(val index: Int, val t: T, val throwable: Throwable) : ElementResult<T>() {
  override fun value(): T = t
  override fun error(): Throwable? = throwable
}
