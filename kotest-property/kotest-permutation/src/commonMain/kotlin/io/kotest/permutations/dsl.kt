package io.kotest.permutations

import io.kotest.common.ExperimentalKotest
import io.kotest.permutations.statistics.Classifications

/**
 * The [permutationConfiguration] builder is used to configure various settings for permutation tests
 * that can be reused across different permutations.
 */
@ExperimentalKotest
fun permutationConfiguration(configure: PermutationConfiguration.() -> Unit): PermutationConfiguration {
   val configuration = PermutationConfiguration()
   configuration.configure()
   return configuration
}

/**
 * The entry point to running a permutation test. This function takes a lambda that configures the permutation.
 *
 * Once the [configure] callback has completed, the permutations are executed.
 */
@ExperimentalKotest
suspend fun permutations(
   configure: suspend PermutationConfiguration.() -> Unit,
): PermutationResult {
   return permutations(PermutationConfiguration(), configure)
}

/**
 * The entry point to running a permutation test. This function takes a lambda that configures the permutation.
 *
 * Once the [configure] callback has completed, the permutations are executed.
 */
@ExperimentalKotest
suspend fun permutations(
   default: PermutationConfiguration,
   configure: suspend PermutationConfiguration.() -> Unit,
): PermutationResult {

   // create a new configuration and apply the default settings, then apply the configuration overrides
   val configuration = PermutationConfiguration()
   configuration.from(default)
   configuration.configure()

   val context = configuration.toContext()
   val executor = PermutationExecutor(context)
   val result = executor.execute { context.test.invoke(Permutation(0, context.rs, Classifications())) }
   return result
}

