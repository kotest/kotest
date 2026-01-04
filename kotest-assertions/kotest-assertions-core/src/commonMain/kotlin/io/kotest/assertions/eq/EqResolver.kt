package io.kotest.assertions.eq

/**
 * An [EqResolver] is used to resolve the appropriate [Eq] instance for two values being compared.
 *
 * See [EqCompare].
 */
interface EqResolver {

   /**
    * Returns the [Eq] to use for comparison for the given values.
    */
   fun resolve(actual: Any?, expected: Any?): Eq<out Any?>
}
