package io.kotest.permutations

internal object ConfigWriter {

   fun writeIfEnabled(context: PermutationContext) {
      if (context.printConfig)
         doWrite(context)
   }

   private fun doWrite(context: PermutationContext) {
      println("Permutation test config:")
      if (context.maxFailures > 0) println("  Max failures: ${context.maxFailures}")
      if (context.maxDiscardPercentage > 0) println("  Max discard percentage: ${context.maxDiscardPercentage}")
      if (context.minSuccess > 0) println("  Min successes: ${context.minSuccess}")
      if (context.edgecasesGenerationProbability > 0.0) println("  Edgecases generation probability: ${context.edgecasesGenerationProbability}")
      println("  Output statistics: ${context.outputStatistics}")
      println("  Print generated values: ${context.printGeneratedValues}")
      println("  Print shrink steps: ${context.printShrinkSteps}")
      println("  Fail on seed: ${context.failOnSeed}")
      println("  Write failed seed: ${context.writeFailedSeed}")
      if (context.customSeed) println("  Custom seed: ${context.customSeed}")
   }
}
