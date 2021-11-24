package io.kotest.engine.spec

import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef

/**
 * A [SpecExecutionOrderExtension] which uses the value of the given [SpecExecutionOrder] parameter
 * to delegate to a [SpecSorter] perform sorting.
 */
internal class DefaultSpecExecutionOrderExtension(
   private val order: SpecExecutionOrder
) : SpecExecutionOrderExtension {

   override fun sort(specs: List<SpecRef>): List<SpecRef> {
      return when (order) {
         SpecExecutionOrder.Undefined -> specs
         SpecExecutionOrder.Lexicographic -> LexicographicSpecSorter.sort(specs)
         SpecExecutionOrder.Random -> RandomSpecSorter.sort(specs)
         SpecExecutionOrder.Annotated -> AnnotatedSpecSorter.sort(specs)
         SpecExecutionOrder.FailureFirst -> FailureFirstSorter().sort(specs)
      }
   }
}
