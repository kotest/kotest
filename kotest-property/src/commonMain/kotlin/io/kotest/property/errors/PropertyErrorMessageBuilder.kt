package io.kotest.property.errors

data class PropertyErrorMessageBuilder(
   private val attempts: Int,
   private val seed: Long?,
   private val maxFailures: Int,
   private val cause: Throwable,
) {

   companion object {
      fun builder(attempts: Int, cause: Throwable): PropertyErrorMessageBuilder {
         return PropertyErrorMessageBuilder(attempts = attempts, seed = null, maxFailures = 0, cause = cause)
      }
   }

   fun withSeed(seed: Long): PropertyErrorMessageBuilder {
      return copy(seed = seed)
   }

   fun build(): String {
      val sb = StringBuilder()
      sb.append("Property failed after $attempts attempts\n")

      if (maxFailures > 0) {
         sb.append("Max failures was $maxFailures\n")
      }

      if (seed != null) {
         sb.append("Repeat this test by using seed $seed\n")
      }

      val finalCause = cause

      // don't bother to include the exception type if it's AssertionError
      val causedBy = when (finalCause::class) {
         is AssertionError -> "Caused by: ${finalCause.message?.trim()}"
         else -> "Caused by ${finalCause::class.simpleName}: ${finalCause.message?.trim()}"
      }
      sb.append(causedBy)

      return sb.toString()
   }

   fun withMaxFailures(maxFailures: Int): PropertyErrorMessageBuilder {
      return copy(maxFailures = maxFailures)
   }
}
