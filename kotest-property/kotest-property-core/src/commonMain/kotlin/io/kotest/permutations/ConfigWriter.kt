package io.kotest.permutations

internal object ConfigWriter {

   fun writeIfEnabled(context: PermutationContext) {
      if (context.shouldPrintConfig)
         doWrite(context)
   }

   private fun doWrite(context: PermutationContext) {

   }
}
