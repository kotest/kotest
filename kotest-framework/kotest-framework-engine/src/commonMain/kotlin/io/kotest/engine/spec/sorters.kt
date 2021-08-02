package io.kotest.engine.spec

import io.kotest.core.spec.Order
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * A typeclass for ordering [Spec]s.
 */
interface SpecSorter {

   fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int

   fun sortClasses(classes: List<KClass<out Spec>>): List<KClass<out Spec>> =
      classes.sortedWith { a, b -> compare(a, b) }

   fun sortSpecs(specs: List<Spec>): List<Spec> =
      specs.sortedWith { a, b -> compare(a::class, b::class) }
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs in a lexicographic order.
 */
object LexicographicSpecSorter : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = a.bestName().compareTo(b.bestName())
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs randomly.
 */
object RandomSpecSorter : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = Random.nextInt()
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs based on the integer
 * value of the [Order] annotation. If the annotation is not present, then that spec is
 * assumed to have a [Int.MAX_VALUE] default value.
 *
 * Note: Runtime annotations are not supported on Native or JS so on those platforms
 * this sort order is a no-op.
 */
expect object AnnotatedSpecSorter :SpecSorter

expect class FailureFirstSorter() : SpecSorter
