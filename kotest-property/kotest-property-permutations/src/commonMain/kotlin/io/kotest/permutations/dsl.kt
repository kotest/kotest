package io.kotest.permutations

import io.kotest.common.ExperimentalKotest

/**
 * The [permconfig] builder is used to configure various settings for permutation tests
 * that can be reused across different permutations.
 */
@ExperimentalKotest
fun permconfig(configure: PermutationConfiguration.() -> Unit): PermutationConfiguration {
   val configuration = PermutationConfiguration()
   configuration.configure()
   return configuration
}

/**
 * The entry point to defining a permutation test.
 * This function accepts a [configure] lambda that configures the permutation.
 *
 * Once the [configure] callback has completed, the permutations are executed.
 */
@ExperimentalKotest
@IgnorableReturnValue
suspend fun permutations(
   configure: suspend PermutationConfiguration.() -> Unit,
): PermutationResult {
   return permutations(PermutationConfiguration(), configure)
}

/**
 * The entry point to running a permutation test.
 * This variation also accepts a default configuration applied before any overrides in the [configure] lambda.
 *
 * Once the [configure] callback has completed, the permutations are executed.
 */
@ExperimentalKotest
@IgnorableReturnValue
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
   val result = executor.execute { iteration ->
      context.test.invoke(Permutation(iteration, context.rs, context.classifications))
   }
   return result
}

