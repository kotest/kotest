package io.kotest.assertions.eq

/**
 * An [EqCompare] is used to compare two values of the same type by looking up an [Eq] instance.
 * The appropriate [Eq] is resolved using the [DefaultEqResolver] class.
 */
object EqCompare {
   internal fun <T> compare(actual: T, expected: T, context: EqContext): Throwable? {
      val eq = EqResolver.resolve(actual, expected) as Eq<T>
      return eq.equals(actual, expected, context)
   }
}
