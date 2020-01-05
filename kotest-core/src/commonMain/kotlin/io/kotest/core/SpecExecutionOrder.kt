package io.kotest.core

import io.kotest.core.fp.getOrElse
import io.kotest.core.fp.orElse
import io.kotest.core.specs.SpecContainer
import kotlin.random.Random

interface SpecExecutionOrder {
   fun sort(classes: List<SpecContainer>): List<SpecContainer>
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a lexicographic order.
 */
object LexicographicSpecExecutionOrder : SpecExecutionOrder {
   override fun sort(classes: List<SpecContainer>): List<SpecContainer> =
      classes.sortedBy { it.qualifiedName.orElse(it.simpleName).getOrElse("<unknonwn>") }
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs in
 * a different random order each time the are executed.
 */
class RandomSpecExecutionOrder(private val random: Random = Random.Default) : SpecExecutionOrder {
   override fun sort(classes: List<SpecContainer>): List<SpecContainer> = classes.shuffled(random)
}

/**
 * An implementation of [SpecExecutionOrder] which will run specs that
 * contained a failed test on a previous run first, before specs where
 * all the tests passed.
 *
 * Only supported on JVM
 */
object FailureFirstSpecExecutionOrder : SpecExecutionOrder {
   override fun sort(classes: List<SpecContainer>): List<SpecContainer> = failureFirstSort(classes)
}

expect fun failureFirstSort(classes: List<SpecContainer>): List<SpecContainer>
