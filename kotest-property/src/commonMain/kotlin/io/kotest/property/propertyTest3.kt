//@file:Suppress("NOTHING_TO_INLINE")
//
//package io.kotest.property
//
//import io.kotest.matchers.booleans.shouldBeTrue
//import io.kotest.property.arbitrary.Arb
//import io.kotest.property.arbitrary.default
//import io.kotest.property.internal.proptest
//
//suspend fun <A, B, C> checkAll(
//   genA: Gen<A>,
//   genB: Gen<B>,
//   genC: Gen<C>,
//   config: PropTestConfig = PropTestConfig(),
//   property: suspend PropertyContext.(A, B, C) -> Unit
//): PropertyContext = proptest(genA, genB, genC, config, property)
//
//suspend inline fun <reified A, reified B, reified C> checkAll(
//   iterations: Int = 1000,
//   config: PropTestConfig = PropTestConfig(),
//   noinline property: suspend PropertyContext.(A, B, C) -> Unit
//) = proptest(
//   Arb.default<A>().take(iterations / 3),
//   Arb.default<B>().take(iterations / 3),
//   Arb.default<C>().take(iterations / 3),
//   config,
//   property
//)
//
//suspend fun <A, B, C> forAll(
//   genA: Gen<A>,
//   genB: Gen<B>,
//   genC: Gen<C>,
//   config: PropTestConfig = PropTestConfig(),
//   property: suspend PropertyContext.(A, B, C) -> Boolean
//) = proptest(genA, genB, genC, config) { a, b, c -> property(a, b, c).shouldBeTrue() }
//
//suspend inline fun <reified A, reified B, reified C> forAll(
//   iterations: Int = 1000,
//   config: PropTestConfig = PropTestConfig(),
//   noinline property: suspend PropertyContext.(A, B, C) -> Boolean
//) = proptest(
//   Arb.default<A>().take(iterations / 3),
//   Arb.default<B>().take(iterations / 3),
//   Arb.default<C>().take(iterations / 3),
//   config
//) { a, b, c -> property(a, b, c).shouldBeTrue() }
