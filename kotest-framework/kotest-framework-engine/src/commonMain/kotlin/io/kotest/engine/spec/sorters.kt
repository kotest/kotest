package io.kotest.engine.spec

import io.kotest.core.spec.Order
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.common.reflection.bestName
import kotlin.random.Random

/**
 * A typeclass for ordering [Spec]s.
 */
interface SpecSorter {
   fun sort(specs: List<SpecRef>): List<SpecRef>
}

object NoopSpecSorter : SpecSorter {
   override fun sort(specs: List<SpecRef>): List<SpecRef> = specs
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs in lexicographic order.
 */
internal val LexicographicSpecSorter = object : SpecSorter {
   override fun sort(specs: List<SpecRef>): List<SpecRef> = specs.sortedBy { it.kclass.bestName() }
}

/**
 * An implementation of [SpecExecutionOrder] which will order specs randomly.
 */
internal class RandomSpecSorter(private val random: Random) : SpecSorter {
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
internal expect val AnnotatedSpecSorter: SpecSorter

/**
 * An implementation of [SpecExecutionOrder] which will order specs that failed on the last run,
 * by looking for a local file with failure information.
 *
 * Note: This is a JVM sort only.
 */
internal expect val FailureFirstSorter: SpecSorter
