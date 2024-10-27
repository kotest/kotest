package io.kotest.property.core.constraints

import io.kotest.property.core.PermutationConfiguration

object ConstraintsBuilder {

   fun build(context: PermutationConfiguration): Constraints {
      return context.constraints
         ?: context.duration?.let { Constraints.duration(it) }
         ?: Constraints.iterations(context.iterations)
   }

}
