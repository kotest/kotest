package io.kotest.assertions.equals

class CommutativeEquality<T: Any?>(
   private val equality: Equality<T>
): Equality<T> {
   override fun name() = equality.name()

   override fun verify(actual: T, expected: T): EqualityResult {
      val result = equality.verify(actual, expected)
      val resultInReverse = equality.verify(expected, actual)
      return when {
         result.areEqual() == resultInReverse.areEqual() -> result
         else -> SimpleEqualityResult(
            equal = false,
            detailsValue = SimpleEqualityResultDetail(
               explainFn = {
                  """
                     |Non-commutative comparison
                     | Actual vs expected: ${resultDescription(result.areEqual())}, ${result.details().explain()}
                     | Expected vs actual: ${resultDescription(resultInReverse.areEqual())}, ${resultInReverse.details().explain()}
                  """.trimMargin()
               }
            )
         )
      }
   }

   private fun resultDescription(equal: Boolean) = if(equal) "passed" else "failed"
}
