package io.kotest.mpp.atomics

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AtomicProperty<T, V>(private val defaultValue: () -> V = { error("AtomicProperty not initialized") }) : ReadWriteProperty<T, V> {

   private val isInitialized = AtomicReference(false)
   private val atomicValue: AtomicReference<V?> = AtomicReference(null)

   override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
      atomicValue.value = value
      isInitialized.value = true
   }

   @Suppress("UNCHECKED_CAST")
   override fun getValue(thisRef: T, property: KProperty<*>): V =
      if (isInitialized.value) atomicValue.value as V
      else defaultValue()
}
