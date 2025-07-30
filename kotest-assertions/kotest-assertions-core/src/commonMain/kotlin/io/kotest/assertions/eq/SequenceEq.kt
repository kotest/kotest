package io.kotest.assertions.eq

import io.kotest.assertions.AssertionErrorBuilder

/**
 * An [Eq] for [Sequence] types which throws an error as sequences cannot be compared using shouldBe.
 */
object SequenceEq : Eq<Sequence<*>> {

   /**
    * [Sequence] is not a supported type.  Sequences can be infinite, and should be
    * compared only with custom code.
    */

   override fun equals(actual: Sequence<*>, expected: Sequence<*>, strictNumberEq: Boolean): Throwable? {
      return AssertionErrorBuilder.create()
         .withMessage("Sequence type is not supported in shouldBe: use custom test code")
         .build()
   }
}
