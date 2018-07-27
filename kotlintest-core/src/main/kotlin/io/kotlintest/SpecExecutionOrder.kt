package io.kotlintest

import java.nio.file.Paths
import kotlin.reflect.KClass

interface SpecExecutionOrder {
  fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>>
}

object LexicographicSpecExecutionOrder : SpecExecutionOrder {
  override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> = classes.sortedBy { it.simpleName }.reversed()
}

object RandomSpecExecutionOrder : SpecExecutionOrder {
  override fun sort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> = classes.shuffled()
}