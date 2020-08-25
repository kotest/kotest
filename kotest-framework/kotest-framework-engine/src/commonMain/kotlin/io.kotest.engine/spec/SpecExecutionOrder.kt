package io.kotest.engine.spec

import io.kotest.core.spec.Order
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.Spec
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

interface SpecSorter {
   fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>>
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a lexicographic order.
 */
object LexicographicSpecSorter : SpecSorter {
   override fun sort(classes: List<KClass<out Spec>>) = classes.sortedBy { it.simpleName }
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a different random order each time the are executed.
 */
object RandomSpecSorter : SpecSorter {
   override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> = classes.shuffled()
}

object AnnotatedSpecSorter : SpecSorter {
   override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> =
      classes.sortedBy { it.annotation<Order>()?.value ?: Int.MAX_VALUE }
}
