package io.kotest.assertions.eq

/**
 * An [EqCompare] is used to compare two values of the same type by looking up an [Eq] instance.
 * The [Eq] is resolved via the [EqResolver] carried on the supplied [EqContext]; this defaults
 * to [DefaultEqResolver] but is replaced by a [LayeredEqResolver] when `withEqs { ... }` supplies
 * per-call overrides.
 */
object EqCompare {
   /**
    * @param context extra context used during comparison such as cyclic references, strict mode,
    *                and the active [EqResolver]. See [EqContext].
    */
   @Suppress("UNCHECKED_CAST")
   internal fun <T> compare(actual: T, expected: T, context: EqContext): EqResult {
      val eq = context.resolver.resolve(actual, expected) as Eq<T>
      return eq.equals(actual, expected, context)
   }
}
