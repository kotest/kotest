package io.kotest.engine.spec

import io.kotest.core.spec.Order
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.mpp.bestName
import kotlin.random.Random
import kotlin.reflect.KClass

/**
 * A typeclass for ordering [Spec]s.
 */
interface SpecSorter {

   fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int

   fun sort(specs: List<SpecRef>): List<SpecRef> =
      specs.sortedWith { a, b -> compare(a.kclass, b.kclass) }
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs in lexicographic order.
 */
object LexicographicSpecSorter : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = a.bestName().compareTo(b.bestName())
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs randomly.
 */
class RandomSpecSorter(private val random: Random) : SpecSorter {
   override fun compare(a: KClass<out Spec>, b: KClass<out Spec>): Int = 0
   override fun sort(specs: List<SpecRef>): List<SpecRef> = specs.shuffled(random)
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs based on the integer
 * value of the [Order] annotation. If the annotation is not present, then that spec is
 * assumed to have a [Int.MAX_VALUE] default value.
 *
 * Note: Runtime annotations are not supported on Native or JS so on those platforms
 * this sort order is a no-op.
 */
expect object AnnotatedSpecSorter : SpecSorter

/**
 * An implementation of [SpecExecutionOrder] which will order specs that failed on the last run,
 * by looking for a local file with failure information.
 *
 * Note: This is a JVM sort only.
 */
expect class FailureFirstSorter() : SpecSorter
