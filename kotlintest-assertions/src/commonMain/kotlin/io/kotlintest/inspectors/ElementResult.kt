package io.kotlintest.inspectors

import kotlin.js.JsName

sealed class ElementResult<out T> {
  abstract fun value(): T
  abstract fun error(): Throwable?
}

class ElementPass<out T>(
        @JsName("valueProperty") val value: T
) : ElementResult<T>() {
  override fun value(): T = value
  override fun error(): Throwable? = null
}

class ElementFail<out T>(
        @JsName("valueProperty") val value: T,
        @JsName("errorProperty") val error: Throwable
) : ElementResult<T>() {
  override fun value(): T = value
  override fun error(): Throwable? = error
}