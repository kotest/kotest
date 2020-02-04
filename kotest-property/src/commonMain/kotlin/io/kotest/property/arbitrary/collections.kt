package io.kotest.property.arbitrary

/**
 * Returns an [Arb] whose values are chosen randomly from those in the supplied collection.
 * May not cover all items. If you want an exhaustive selection from the list, see [Exhaustive.collection]
 */
fun <T> Arb.Companion.collection(collection: Collection<T>): Arb<T> = arb { collection.random(it) }
