package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecExecutionOrder
import kotlin.reflect.KClass

fun List<KClass<out Spec>>.sort(order: SpecExecutionOrder): List<KClass<out Spec>> {
   return when (order) {
      SpecExecutionOrder.Undefined -> this
      SpecExecutionOrder.Lexicographic -> LexicographicSpecSorter.sortClasses(this)
      SpecExecutionOrder.Random -> RandomSpecSorter.sortClasses(this)
      SpecExecutionOrder.Annotated -> AnnotatedSpecSorter.sortClasses(this)
      SpecExecutionOrder.FailureFirst -> FailureFirstSorter().sortClasses(this)
   }
}

fun List<Spec>.sort(order: SpecExecutionOrder): List<Spec> {
   return when (order) {
      SpecExecutionOrder.Undefined -> this
      SpecExecutionOrder.Lexicographic -> LexicographicSpecSorter.sortSpecs(this)
      SpecExecutionOrder.Random -> RandomSpecSorter.sortSpecs(this)
      SpecExecutionOrder.Annotated -> AnnotatedSpecSorter.sortSpecs(this)
      SpecExecutionOrder.FailureFirst -> FailureFirstSorter().sortSpecs(this)
   }
}

