package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

/**
 * Returns an [Exhaustive] which contains the values in the given collection.
 */
fun <T> Exhaustive.Companion.collection(collection: Collection<T>): Exhaustive<T> = object : Exhaustive<T> {
   override fun values(): Sequence<T> = collection.asSequence()
}
