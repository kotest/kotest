package io.kotest.property.progressions

import io.kotest.property.Progression

fun <T> Progression.Companion.collection(collection: Collection<T>): Progression<T> = object : Progression<T> {
   override fun values(): Sequence<T> = collection.asSequence()
}
