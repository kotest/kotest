package io.kotest.property.arbitrary

import io.kotest.property.Arbitrary
import io.kotest.property.Shrinker

/**
 * Models the type of inputs that can be generated from an [Arbitrary].
 *
 * There are two implementations of [PropertyInput]. The first is a wrapper for values without
 * a shrinker, which is [PropertyInput.Value].
 *
 * The second is a wrapper for values associated with shrinks, which is [PropertyInput.ValueAndShrinker].
 */
sealed class PropertyInput<out T> {
   abstract val value: T

   /**
    * Models a value that cannot be shrunk, or for which shrinking is not supported.
    *
    * @param value the property test input value
    */
   data class Value<T>(override val value: T) : PropertyInput<T>()

   /**
    * Models a value that can be shrunk if the value fails to pass a test.
    *
    * @param value the property test input value
    * @param shrinker a function that returns shrunk values of the input value
    */
   data class ValueAndShrinker<T>(
      override val value: T,
      val shrinker: () -> List<PropertyInput<T>>
   ) : PropertyInput<T>()

   companion object {
      operator fun <T> invoke(value: T) = Value(value)
      operator fun <T> invoke(value: T, shrinker: Shrinker<T>) = ValueAndShrinker(value, { shrinker.shrink(value) })
   }
}

fun <T, U> PropertyInput<T>.map(f: (T) -> U): PropertyInput<U> = when (this) {
   is PropertyInput.Value<T> -> PropertyInput.Value(f(value))
   is PropertyInput.ValueAndShrinker<T> -> {
      val shrinks = { this.shrinker().map { it.map(f) } }
      PropertyInput.ValueAndShrinker(f(value), shrinks)
   }
}
