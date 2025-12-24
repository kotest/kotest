package io.kotest.assertions.eq

import io.kotest.assertions.AssertionErrorBuilder

/**
 * An [Eq] for [Sequence] types which throws an error as sequences cannot be compared using shouldBe.
 */
object SequenceEq : Eq<Sequence<*>> {

   @Deprecated("Use the overload with one more parameter of type EqContext.")
   override fun equals(actual: Sequence<*>, expected: Sequence<*>, strictNumberEq: Boolean): Throwable? =
      equals(actual, expected, strictNumberEq, EqContext())

   /**
    * [Sequence] is not a supported type.  Sequences can be infinite, and should be
    * compared only with custom code.
    */

   override fun equals(actual: Sequence<*>, expected: Sequence<*>, strictNumberEq: Boolean, context: EqContext): Throwable? {
      return AssertionErrorBuilder.create()
         .withMessage("Sequence type is not supported in shouldBe: use custom test code")
         .build()
   }
}
