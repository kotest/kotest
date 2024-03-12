package io.kotest.engine.spec

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.core.spec.Order
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.mpp.annotation
import kotlin.random.Random

/**
 * A [SpecExecutionOrderExtension] which uses the value of the given [SpecExecutionOrder] parameter
 * to delegate to a [SpecSorter] perform sorting.
 */
internal class DefaultSpecExecutionOrderExtension(
   private val order: SpecExecutionOrder,
   private val configuration: ProjectConfiguration,
) : SpecExecutionOrderExtension {

   override fun sort(specs: List<SpecRef>): List<SpecRef> {
      return when (order) {

         SpecExecutionOrder.Undefined -> {
            checkAnnotatedStatus(specs)
            specs
         }

         SpecExecutionOrder.Lexicographic -> LexicographicSpecSorter.sort(specs)
         SpecExecutionOrder.Annotated -> AnnotatedSpecSorter.sort(specs)
         SpecExecutionOrder.FailureFirst -> FailureFirstSorter.sort(specs)
         SpecExecutionOrder.Random -> {
            val random = when (val seed = configuration.randomOrderSeed) {
               null -> Random.Default
               else -> Random(seed)
            }
            RandomSpecSorter(random).sort(specs)
         }
      }
   }

   /**
    * If any specs have @Order and SpecExecutionOrder.Undefined is the sort mode, throw an error.
    */
   private fun checkAnnotatedStatus(specs: List<SpecRef>) {
      val annotatedSpec = specs.find { it.kclass.annotation<Order>() != null }
      if (annotatedSpec != null) error("Spec ${annotatedSpec.kclass} is annotated with @Order but SpecExecutionOrder is using the default Undefined. In order for @Order to be used, you must configure the SpecExecutionOrder inside project configuration.")
   }
}
