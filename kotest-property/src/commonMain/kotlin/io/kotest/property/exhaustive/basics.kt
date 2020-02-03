package io.kotest.property.exhaustive

/**
 * Returns a [Exhaustive] of the two possible boolean values - true and false.
 */
fun Exhaustive.Companion.bools() = object : Exhaustive<Boolean> {
   override fun values(): Sequence<Boolean> = sequenceOf(true, false)
}

/**
 * Returns a [Exhaustive] whose value is a single constant.
 */
fun <A> Exhaustive.Companion.single(a: A) = object : Exhaustive<A> {
   override fun values(): Sequence<A> = sequenceOf(a)
}

fun Exhaustive.Companion.nullable() = object : Exhaustive<Nothing?> {
   override fun values(): Sequence<Nothing?> = sequenceOf(null)
}

fun <A> Exhaustive<A>.andNull() = object : Exhaustive<A?> {
   override fun values(): Sequence<A?> = this@andNull.values() + sequenceOf(null)
}
