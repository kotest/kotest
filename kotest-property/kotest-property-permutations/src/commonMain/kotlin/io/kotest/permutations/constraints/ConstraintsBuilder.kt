package io.kotest.permutations.constraints

import io.kotest.common.ExperimentalKotest
import io.kotest.permutations.PermutationConfiguration

@OptIn(ExperimentalKotest::class)
object ConstraintsBuilder {

   fun build(context: PermutationConfiguration): Constraints {
      return context.constraints
         ?: context.duration?.let { Constraints.duration(it) }
         ?: Constraints.iterations(context.iterations)
   }

}
