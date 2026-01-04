package io.kotest.assertions.eq

/**
 * An [EqCompare] is used to compare two values of the same type by looking up an [Eq] instance.
 * The appropriate [Eq] is resolved using the [DefaultEqResolver] class.
 */
object EqCompare {
   /**
    * @param context extra context used during comparison such as cyclic references and strict mode.
    *
    * See [EqContext].
    */
   @Suppress("UNCHECKED_CAST")
   internal fun <T> compare(actual: T, expected: T, context: EqContext): EqResult {
      val eq = DefaultEqResolver.resolve(actual, expected) as Eq<T>
      return eq.equals(actual, expected, context)
   }
}
