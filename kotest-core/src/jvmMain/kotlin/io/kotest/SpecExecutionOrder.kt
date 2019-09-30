package io.kotest

import kotlin.reflect.KClass

interface SpecExecutionOrder {
  fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>>
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a lexicographic order.
 */
object LexicographicSpecExecutionOrder : SpecExecutionOrder {
  override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> = classes.sortedBy { it.simpleName }
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a different random order each time the are executed.
 */
object RandomSpecExecutionOrder : SpecExecutionOrder {
  override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> = classes.shuffled()
}

