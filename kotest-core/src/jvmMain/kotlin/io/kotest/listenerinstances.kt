package io.kotest

import io.kotest.extensions.TestListener
import java.util.*
import kotlin.reflect.KProperty

val SpecClass.listenerInstances by LazyWithReceiver<SpecClass, List<TestListener>> { this.listeners() }

private class LazyWithReceiver<This, Return>(val initializer: This.() -> Return) {
   private val values = WeakHashMap<This, Return>()

   @Suppress("UNCHECKED_CAST")
   operator fun getValue(thisRef: Any, property: KProperty<*>): Return = synchronized(values) {
      thisRef as This
      return values.getOrPut(thisRef) { thisRef.initializer() }
   }
}
