package io.kotest.property.exhaustive

/**
 * Returns a [Exhaustive] of the two possible boolean values - true and false.
 */
fun Exhaustive.Companion.boolean() = object : Exhaustive<Boolean> {
   override val values: List<Boolean> = listOf(true, false)
}

/**
 * Returns a [Exhaustive] whose value is a single constant.
 */
fun <A> Exhaustive.Companion.constant(a: A) = object : Exhaustive<A> {
   override val values: List<A> = listOf(a)
}

fun Exhaustive.Companion.nullable() = object : Exhaustive<Nothing?> {
   override val values: List<Nothing?> = listOf(null)
}

fun <A> Exhaustive<A>.andNull() = object : Exhaustive<A?> {
   override val values: List<A?> = this@andNull.values + listOf(null)
}

/**
 * Returns an Exhaustive which is the concatentation of this exhaustive values plus
 * the given exhaustive's values.
 */
operator fun <A> Exhaustive<A>.plus(other: Exhaustive<A>) = object : Exhaustive<A> {
   override val values: List<A> = this@plus.values + other.values
}

/**
 * Returns the cross product of two [Exhaustive]s.
 */
operator fun <A, B : A> Exhaustive<A>.times(other: Exhaustive<B>) = object : Exhaustive<Pair<A, B>> {
   override val values: List<Pair<A, B>> = this@times.values.flatMap { a ->
      other.values.map { b ->
         Pair(a, b)
      }
   }
}
