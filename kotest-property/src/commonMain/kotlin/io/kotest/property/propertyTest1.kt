@file:Suppress("NOTHING_TO_INLINE")

package io.kotest.property

import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.Arb
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

suspend fun <A> Gen<A>.checkAll(property: suspend PropertyContext.(A) -> Unit) = 
   checkAll(this, property)
suspend fun <A> checkAll(
   genA: Gen<A>,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(PropertyTesting.defaultIterationCount, genA, PropTestConfig(), property)

suspend fun <A> Gen<A>.checkAll(iterations: Int, property: suspend PropertyContext.(A) -> Unit) = 
   checkAll(iterations, this, property)
suspend fun <A> checkAll(
   iterations: Int,
   genA: Gen<A>,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(iterations, genA, PropTestConfig(), property)

suspend fun <A> Gen<A>.checkAll(config: PropTestConfig, property: suspend PropertyContext.(A) -> Unit) =
   checkAll(config, this, property)
suspend fun <A> checkAll(
   config: PropTestConfig,
   genA: Gen<A>,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(PropertyTesting.defaultIterationCount, genA, config, property)

suspend fun <A> Gen<A>.checkAll(iterations: Int, config: PropTestConfig, property: suspend PropertyContext.(A) -> Unit) =
   checkAll(iterations, config, this, property)
suspend fun <A> checkAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(iterations, genA, config, property)


suspend inline fun <reified A> checkAll(
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(
   PropertyTesting.defaultIterationCount,
   Arb.default<A>(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A> checkAll(
   iterations: Int,
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(
   iterations,
   Arb.default<A>(),
   PropTestConfig(),
   property
)

suspend inline fun <reified A> checkAll(
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = checkAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A> checkAll(
   iterations: Int,
   config: PropTestConfig,
   noinline property: suspend PropertyContext.(A) -> Unit
): PropertyContext = proptest(
   iterations,
   Arb.default<A>(),
   config,
   property
)

suspend fun <A> Gen<A>.forAll(property: PropertyContext.(A) -> Boolean) =
   forAll(this, property)
suspend fun <A> forAll(
   genA: Gen<A>,
   property: PropertyContext.(A) -> Boolean
) = forAll(PropertyTesting.defaultIterationCount, PropTestConfig(), genA, property)


suspend fun <A> Gen<A>.forAll(iterations: Int, property: PropertyContext.(A) -> Boolean) =
   forAll(iterations, this, property)
suspend fun <A> forAll(
   iterations: Int,
   genA: Gen<A>,
   property: PropertyContext.(A) -> Boolean
) = forAll(iterations, PropTestConfig(), genA, property)


suspend fun <A> Gen<A>.forAll(config: PropTestConfig, property: PropertyContext.(A) -> Boolean) =
   forAll(config, this, property)
suspend fun <A> forAll(
   config: PropTestConfig,
   genA: Gen<A>,
   property: PropertyContext.(A) -> Boolean
) = forAll(PropertyTesting.defaultIterationCount, config, genA, property)


suspend fun <A> Gen<A>.forAll(iterations: Int, config: PropTestConfig, property: PropertyContext.(A) -> Boolean) =
   forAll(iterations, config, this, property)
suspend fun <A> forAll(
   iterations: Int,
   config: PropTestConfig,
   genA: Gen<A>,
   property: PropertyContext.(A) -> Boolean
) = proptest(iterations, genA, config) { a -> property(a) shouldBe true }


suspend inline fun <reified A> forAll(
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = forAll(PropertyTesting.defaultIterationCount, PropTestConfig(), property)

suspend inline fun <reified A> forAll(
   iterations: Int,
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = forAll(iterations, PropTestConfig(), property)

suspend inline fun <reified A> forAll(
   config: PropTestConfig,
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = forAll(PropertyTesting.defaultIterationCount, config, property)

suspend inline fun <reified A> forAll(
   iterations: Int,
   config: PropTestConfig,
   crossinline property: suspend PropertyContext.(A) -> Boolean
) = proptest<A>(
   iterations,
   Arb.default<A>(),
   config
) { a -> property(a) shouldBe true }
