package io.kotest.specs

import io.kotest.core.Description
import io.kotest.SpecInterface
import io.kotest.core.DisplayName
import io.kotest.extensions.TestListener
import java.util.*
import kotlin.reflect.KProperty

/**
 * A name / id for this class which is used as the parent route for tests.
 * By default this will return the fully qualified class name, unless the spec
 * class is annotated with @DisplayName.
 *
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name.
 */
fun Class<*>.displayName(): String {
   return when (val displayName = annotations.find { it is DisplayName }) {
      is DisplayName -> displayName.name
      else -> canonicalName
   }
}

fun Class<out SpecInterface>.description() =
   Description.spec(this.displayName())

val SpecInterface.listenerInstances by LazyWithReceiver<SpecInterface, List<TestListener>> { this.listeners() }

private class LazyWithReceiver<This, Return>(val initializer: This.() -> Return) {
   private val values = WeakHashMap<This, Return>()

   @Suppress("UNCHECKED_CAST")
   operator fun getValue(thisRef: Any, property: KProperty<*>): Return = synchronized(values) {
      thisRef as This
      return values.getOrPut(thisRef) { thisRef.initializer() }
   }
}
