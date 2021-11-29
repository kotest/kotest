package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive

fun <A, B> Exhaustive<A>.zip(other: Exhaustive<B>): Exhaustive<Pair<A, B>> {
   return this.values.zip(other.values).exhaustive()
}
