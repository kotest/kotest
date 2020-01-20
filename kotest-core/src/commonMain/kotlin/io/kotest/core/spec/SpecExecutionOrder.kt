package io.kotest.core.spec

import kotlin.reflect.KClass

/**
 * Note: This has no effect on non-JVM targets.
 */
interface SpecExecutionOrder {
   fun sort(classes: List<KClass<out SpecConfiguration>>): List<KClass<out SpecConfiguration>>
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a lexicographic order.
 */
object LexicographicSpecExecutionOrder : SpecExecutionOrder {
   override fun sort(classes: List<KClass<out SpecConfiguration>>): List<KClass<out SpecConfiguration>> =
      classes.sortedBy { it.simpleName }
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a different random order each time the are executed.
 */
object RandomSpecExecutionOrder : SpecExecutionOrder {
   override fun sort(classes: List<KClass<out SpecConfiguration>>): List<KClass<out SpecConfiguration>> =
      classes.shuffled()
}
