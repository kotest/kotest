package io.kotest.assertions.eq

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

object SequenceEq : Eq<Sequence<*>> {

   /**
    * [Sequence] is not a supported type.  Sequences can be infinite, and should be
    * compared only with custom code.
    */

   fun isValidSequence(it: Any): Boolean {
      return when (it) {
         is Sequence<*> -> true
         else -> false
      }
   }

   fun asSequence(it: Any): Sequence<*> {
      return when (it) {
         is Sequence<*> -> it
         else -> error("Cannot convert $it to Sequence<*>")
      }
   }

   override fun equals(actual: Sequence<*>, expected: Sequence<*>, strictNumberEq: Boolean): Throwable? =
      failure(Expected(expected.print()), Actual(actual.print()), "Sequence type is not supported: use custom code")

}
