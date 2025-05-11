package io.kotest.assertions.eq

/**
 * An [EqCompare] is used to compare two values of the same type by looking up an [Eq] instance.
 *
 * See [EqResolver].
 */
object EqCompare {
   @Suppress("UNCHECKED_CAST")
   fun <T> compare(actual: T, expected: T, strictNumberEq: Boolean): Throwable? {
      val eq = EqResolver.resolve(actual, expected) as Eq<T>
      return eq.equals(actual, expected, strictNumberEq)
   }
}
