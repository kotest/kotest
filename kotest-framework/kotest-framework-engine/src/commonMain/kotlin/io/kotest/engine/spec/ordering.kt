package io.kotest.engine.spec

import io.kotest.core.extensions.SpecExecutionOrderExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecExecutionOrder
import kotlin.reflect.KClass

/**
 * A [SpecExecutionOrderExtension] which uses the value of the given [SpecExecutionOrder] parameter
 * to delegate to a [SpecSorter] perform sorting.
 */
class DefaultSpecExecutionOrderExtension(private val order: SpecExecutionOrder) : SpecExecutionOrderExtension {

   override fun sortSpecs(specs: List<Spec>): List<Spec> {
      return when (order) {
         SpecExecutionOrder.Undefined -> specs
         SpecExecutionOrder.Lexicographic -> LexicographicSpecSorter.sortSpecs(specs)
         SpecExecutionOrder.Random -> RandomSpecSorter.sortSpecs(specs)
         SpecExecutionOrder.Annotated -> AnnotatedSpecSorter.sortSpecs(specs)
         SpecExecutionOrder.FailureFirst -> FailureFirstSorter().sortSpecs(specs)
      }
   }

   override fun sortClasses(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      return when (order) {
         SpecExecutionOrder.Undefined -> classes
         SpecExecutionOrder.Lexicographic -> LexicographicSpecSorter.sortClasses(classes)
         SpecExecutionOrder.Random -> RandomSpecSorter.sortClasses(classes)
         SpecExecutionOrder.Annotated -> AnnotatedSpecSorter.sortClasses(classes)
         SpecExecutionOrder.FailureFirst -> FailureFirstSorter().sortClasses(classes)
      }
   }

}
