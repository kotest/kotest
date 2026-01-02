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
      fun wrap(t: Throwable?): EqResult {
         return if (t == null) Success else Failure { t }
      }
   }
}

//data class EqResult(
//   val equal: Boolean,
//   val error: () -> Throwable?
//) {
//
//   companion object {
//
//      /**
//       * Constant [EqResult] used for successful comparisons.
//       */
//      val Success = EqResult(true) { null }
//
//      /**
//       * Wraps a failure generating function in an [EqResult].
//       *
//       * Should be used when we know an [Eq] has failed, and the error message is being lazily
//       * generated so it can be avoided when the comparison is inverted.
//       */
//      fun failure(error: () -> Throwable): EqResult = EqResult(false, error)
//
//      /**
//       * When we have an already generated error, or null, we can wrap it in a [EqResult] directly.
//       * This function will return a success if the given throwable is null, otherwise a failure.
//       */
//      fun wrap(t: Throwable?) = if (t == null) Success else failure { t }
//   }
//}
//
//fun EqResult.flatMapIfEqual(fn: () -> EqResult): EqResult {
//   return if (this.equal) fn() else this
//}
//
//fun EqResult.throwableWithFallback(): Throwable {
//   return error() ?: RuntimeException("Eq failed but no error was provided")
//}
