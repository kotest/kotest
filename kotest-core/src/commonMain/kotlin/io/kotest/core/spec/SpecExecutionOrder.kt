package io.kotest.core.spec

import io.kotest.mpp.annotation
import kotlin.reflect.KClass

/**
 * Note: This has no effect on non-JVM targets.
 */
interface SpecExecutionOrder {
   fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>>
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a lexicographic order.
 */
object LexicographicSpecExecutionOrder : SpecExecutionOrder {
   override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> =
      classes.sortedBy { it.simpleName }
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a different random order each time the are executed.
 */
object RandomSpecExecutionOrder : SpecExecutionOrder {
   override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> =
      classes.shuffled()
}

object AnnotatedSpecExecutionOrder : SpecExecutionOrder {
   override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> {
      return classes.sortedBy { it.annotation<Order>()?.value ?: Int.MAX_VALUE }
   }
}

annotation class Order(val value: Int)
