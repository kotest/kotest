package io.kotest.property.exhaustive

fun <A> Exhaustive.Companion.collection(collection: Collection<A>) = object : Exhaustive<A> {
   override val values: List<A> = collection.toList()
}
