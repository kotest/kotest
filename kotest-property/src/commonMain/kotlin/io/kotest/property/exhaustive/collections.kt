package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun <A> Exhaustive.Companion.collection(collection: Collection<A>): Exhaustive<A> {
   return collection.toList().exhaustive()
}
