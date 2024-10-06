package io.kotest.property.core

import io.kotest.common.ExperimentalKotest

/**
 * The permutationConfig builder is used to configure various settings for property-based that can be reused
 * across all permutations in the same scope. This function allows you to set parameters such as the number of
 * iterations, the probability of generating edge cases, and whether to print generated values.
 */
@ExperimentalKotest
fun permutationContext(configure: PermutationContext.() -> Unit): PermutationContext {
   // would need to add this from the coroutineContext, but that is immutable
   // so we need to extend kotest framework with a mutable map of elements that can change at runtime and put it there
   val context = PermutationContext()
   context.configure()
   return context
}

@ExperimentalKotest
suspend fun permutations(
   configure: suspend PermutationContext.() -> Unit
): PermutationResult {
   val context = PermutationContext()
   context.configure()
   return PermutationResult(100, 0.0)
}

data class PermutationResult(
   val evaluations: Int,
   val discardPercentage: Double,
)
