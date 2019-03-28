package io.kotlintest

import io.kotlintest.listener.TestListener
import kotlin.reflect.KProperty

actual val Spec.listenerInstances: List<TestListener> by LazyWithReceiver<Spec, List<TestListener>> { this.listeners() }

private class LazyWithReceiver<This, Return>(val initializer: This.() -> Return) {
  private val values = HashMap<This, Return>()

  @Suppress("UNCHECKED_CAST")
  operator fun getValue(thisRef: Any, property: KProperty<*>): Return = synchronized(values)
  {
    thisRef as This
    return values.getOrPut(thisRef) { thisRef.initializer() }
  }
}