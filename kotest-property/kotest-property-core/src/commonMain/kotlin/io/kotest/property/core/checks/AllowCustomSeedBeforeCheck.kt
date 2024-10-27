package io.kotest.property.core.checks

import io.kotest.property.core.PermutationContext

internal object AllowCustomSeedBeforeCheck : BeforeCheck {
   override fun evaluate(context: PermutationContext) {
      if (context.customSeed && context.failOnSeed)
         error("A seed is specified on this permutation but failOnSeed is true")
   }
}

