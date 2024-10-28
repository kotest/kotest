package io.kotest.permutations.checks

import io.kotest.permutations.PermutationContext

internal object AllowCustomSeedBeforeCheck {
   fun check(context: PermutationContext) {
      if (context.customSeed && context.failOnSeed)
         error("A seed is specified on this permutation but failOnSeed is true")
   }
}

