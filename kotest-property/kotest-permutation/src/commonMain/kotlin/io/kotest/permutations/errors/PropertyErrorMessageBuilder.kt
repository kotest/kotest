package io.kotest.permutations.errors

import io.kotest.permutations.Input

data class PropertyErrorMessageBuilder(
   private val attempts: Int,
   private val seed: Long?,
   private val maxFailures: Int,
   private val cause: Throwable,
   private val inputs: List<Input>,
) {

   companion object {
      fun builder(attempts: Int, cause: Throwable): PropertyErrorMessageBuilder {
         return PropertyErrorMessageBuilder(
            attempts = attempts,
            seed = null,
            maxFailures = 0,
            cause = cause,
            inputs = emptyList(),
         )
      }
   }

   fun withSeed(seed: Long): PropertyErrorMessageBuilder {
      return copy(seed = seed)
   }

   fun build(): String {

      val sb = StringBuilder()
      sb.appendLine("Property failed after $attempts attempts")

      if (maxFailures > 0) {
         sb.appendLine("Max failures was $maxFailures")
      }

      if (seed != null) {
         sb.appendLine("Repeat this test by using seed $seed")
      }

      val finalCause = cause

      // don't bother to include the exception type if it's AssertionError
      val causedBy = when (finalCause::class) {
         is AssertionError -> "Caused by: ${finalCause.message?.trim()}"
         else -> "Caused by ${finalCause::class.simpleName}: ${finalCause.message?.trim()}"
      }
      sb.appendLine()
      sb.append(causedBy)
      sb.appendLine()
      sb.appendLine()

      if (inputs.isNotEmpty()) {
         sb.appendLine("Inputs:")
         inputs.withIndex().map { (index, input) ->
            val name = input.name ?: index
            sb.appendLine("$name) ${input.value}")
         }
      }
      sb.appendLine()

      return sb.toString()
   }

   fun withMaxFailures(maxFailures: Int): PropertyErrorMessageBuilder {
      return copy(maxFailures = maxFailures)
   }

   fun withInputs(inputs: List<Input>): PropertyErrorMessageBuilder {
      return copy(inputs = inputs)
   }
}
