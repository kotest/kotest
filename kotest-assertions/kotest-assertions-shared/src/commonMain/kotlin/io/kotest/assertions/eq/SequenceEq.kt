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

   override fun equals(actual: Sequence<*>, expected: Sequence<*>, strictNumberEq: Boolean): EqResult =
      EqResult.NotEqual {
         failure(
            Expected(expected.print()),
            Actual(actual.print()),
            "Sequence type is not supported: use custom test code"
         )
      }

}
