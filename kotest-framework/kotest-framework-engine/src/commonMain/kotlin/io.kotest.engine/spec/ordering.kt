package io.kotest.engine.spec

import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

fun List<KClass<out Spec>>.sort(order: SpecExecutionOrder): List<KClass<out Spec>> {
   return when (order) {
      SpecExecutionOrder.Undefined -> this
      SpecExecutionOrder.Lexicographic -> LexicographicSpecSorter.sort(this)
      SpecExecutionOrder.Random -> RandomSpecSorter.sort(this)
      SpecExecutionOrder.Annotated -> AnnotatedSpecSorter.sort(this)
      SpecExecutionOrder.FailureFirst -> failureFirstSort(this)
   }
}

expect fun failureFirstSort(classes: List<KClass<out Spec>>): List<KClass<out Spec>>

