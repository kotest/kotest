package io.kotest.property.arbitrary

import io.kotest.properties.generateInfiniteSequence
import io.kotest.property.Arbitrary
import kotlin.random.Random
import io.kotest.property.Progression

/**
 * Returns an [Arbitrary] whose values are chosen randomly from those in the supplied collection.
 * May not cover all items. If you want an exhaustive selection from the list, see [Progression.collection]
 * Does not provide shrinking.
 */
fun <T> Arbitrary.Companion.collection(
   iterations: Int,
   collection: Collection<T>
): Arbitrary<T> = object : Arbitrary<T> {
   override fun edgecases(): Iterable<T> = emptyList()
   override fun samples(random: Random): Sequence<PropertyInput<T>> {
      return generateInfiniteSequence { PropertyInput(collection.random(random)) }.take(iterations)
   }
}
