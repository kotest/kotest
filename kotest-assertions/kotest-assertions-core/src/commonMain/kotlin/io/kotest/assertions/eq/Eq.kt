package io.kotest.assertions.eq

/**
 * A [Eq] typeclass compares two values for equality, returning a [Throwable] if they are
 * not equal, or null if they are equal.
 *
 * This equality typeclass is at the heart of the shouldBe matcher.
 */
interface Eq<T> {

   /**
    * @param context extra context used during comparison such as cyclic references and strict mode. See [EqContext].
    */
   fun equals(actual: T, expected: T, context: EqContext): EqResult
}

sealed interface EqResult {

   data object Success : EqResult

   /**
    * Wraps a failure generating function in an [EqResult].
    *
    * Should be used when we know an [Eq] has failed, and the error message is being lazily
    * generated so it can be avoided when the comparison is inverted.
    */
   data class Failure(val error: () -> Throwable) : EqResult

   companion object {

      /**
       * Wraps a Throwable in an [EqResult].
       *
       * Should be used when we have already generated a Throwable, or null, and we want the appropriate
       * [EqResult] type.
       */
      fun wrap(t: Throwable?): EqResult {
         return if (t == null) Success else Failure { t }
      }
   }
}
