package io.kotest.permutations.constraints

import io.kotest.permutations.PermutationConfiguration

object ConstraintsBuilder {

   fun build(context: PermutationConfiguration): Constraints {
      return context.constraints
         ?: context.duration?.let { Constraints.duration(it) }
         ?: Constraints.iterations(context.iterations)
   }

}
