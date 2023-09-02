package io.kotest.equals

import io.kotest.assertions.print.print

interface EqualityResult {
   fun areEqual(): Boolean

   fun details(): EqualityResultDetails

   companion object {
      fun <T> equal(actual: T, expected: T, verifier: Equality<*>): SimpleEqualityResult {
         return create(equal = true, actual = actual, expected = expected, verifier = verifier)
      }

      fun <T> notEqual(actual: T, expected: T, verifier: Equality<*>): SimpleEqualityResult {
         return create(equal = false, actual = actual, expected = expected, verifier = verifier)
      }

      private fun <T> create(
         equal: Boolean,
         actual: T,
         expected: T,
         verifier: Equality<*>
      ): SimpleEqualityResult {
         return SimpleEqualityResult(
            equal = equal,
            detailsValue = SimpleEqualityResultDetail(
               explainFn = {
                  val expectedStr = expected.print().value
                  val actualStr = actual.print().value
                  """
                     | $expectedStr is ${if (equal) "" else "not "}equal to $actualStr by ${verifier.name()}
                     | Expected: $expectedStr
                     | Actual  : $actualStr
                  """.trimMargin()
               }
            )
         )
      }
   }
}

fun EqualityResult.areNotEqual() = !areEqual()

interface EqualityResultDetails {
   fun explain(): String

   companion object {
      fun create(reasonFn: () -> String): EqualityResultDetails {
         return object : EqualityResultDetails {
            override fun explain(): String = reasonFn()
         }
      }
   }
}

data class SimpleEqualityResult(
   val equal: Boolean,
   val detailsValue: EqualityResultDetails,
) : EqualityResult {
   fun withDetails(details: EqualityResultDetails): SimpleEqualityResult {
      return copy(detailsValue = details)
   }

   fun withDetails(explainFn: () -> String): SimpleEqualityResult {
      return withDetails(SimpleEqualityResultDetail(explainFn))
   }

   override fun areEqual(): Boolean = equal

   override fun details(): EqualityResultDetails = detailsValue
}

data class SimpleEqualityResultDetail(
   val explainFn: () -> String
) : EqualityResultDetails {
   override fun explain(): String = explainFn()
}
