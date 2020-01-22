//package io.kotest.property.arbitrary
//
//import io.kotest.property.Arbitrary
//import io.kotest.property.map
//import kotlin.random.Random
//
///**
// * Returns an [Arbitrary] whose value is generated from the given function.
// */
//inline fun <T> Arbitrary.Companion.create(
//   iterations: Int,
//   crossinline fn: () -> T
//): Arbitrary<T> =
//   object : Arbitrary<T> {
//      override fun edgecases(): Iterable<T> = emptyList()
//      override fun samples(random: Random): Sequence<PropertyInput<T>> =
//         generateSequence { PropertyInput(fn()) }.take(iterations)
//   }
//
///**
// * Returns an [Arbitrary] where each random value is a Byte.
// * The edge cases are [[Byte.MIN_VALUE], [Byte.MAX_VALUE], 0]
// */
//fun Arbitrary.Companion.byte(iterations: Int) = int(iterations).map { it.ushr(Int.SIZE_BITS - Byte.SIZE_BITS).toByte() }
