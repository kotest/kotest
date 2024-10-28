package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.permutations.statistics.Classifications

/**
 * The permutationConfig builder is used to configure various settings for property-based that can be reused
 * across all permutations in the same scope. This function allows you to set parameters such as the number of
 * iterations, the probability of generating edge cases, and whether to print generated values.
 */
@ExperimentalKotest
fun permutationConfiguration(configure: PermutationConfiguration.() -> Unit): PermutationConfiguration {
   val configuration = PermutationConfiguration()
   configuration.configure()
   return configuration
}

/**
 * The entry point to running a permutation test. This function takes a lambda that configures the permutation.
 * Once the configuration callback has completed, the permutations are generated and run.
 */
@ExperimentalKotest
suspend fun permutations(
   configure: suspend PermutationConfiguration.() -> Unit,
): PermutationResult {

   val configuration = PermutationConfiguration()
   configuration.configure()

   val context = configuration.toContext()
   val executor = PermutationExecutor(context)
   val result = executor.execute { context.test.invoke(Permutation(0, context.rs, Classifications())) }
   return result
}

