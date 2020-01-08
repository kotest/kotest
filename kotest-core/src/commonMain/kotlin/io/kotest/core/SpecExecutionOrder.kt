package io.kotest.core

import io.kotest.core.spec.SpecConfiguration
import kotlin.reflect.KClass

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
