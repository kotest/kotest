package io.kotest.property.arbitrary

import io.kotest.property.*
import kotlin.jvm.JvmName

/**
 * Creates a new [Arb] that performs no shrinking, has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(fn: (RandomSource) -> A): Arb<A> =
   arbitrary(emptyList(), fn)

/**
 * Creates a new [Arb] that performs no shrinking, uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(edgecases: List<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecases
   override fun edgecases(rs: RandomSource): List<A> = edgecases

   override fun sample(rs: RandomSource): Sample<A> = Sample(fn(rs))
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { Sample(fn(rs)) }
}

/**
 * Creates a new [Arb] that performs no shrinking, uses the given edge cases and generates values from
 * the given function.
 */
fun <A> arbitrary(edgecasesGenerator: (RandomSource) -> List<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecasesGenerator(RandomSource.Default)
   override fun edgecases(rs: RandomSource): List<A> = edgecasesGenerator(rs)
   override fun sample(rs: RandomSource): Sample<A> = Sample(fn(rs))
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { Sample(fn(rs)) }
}

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(edgecases: List<A>, shrinker: Shrinker<A>, fn: (RandomSource) -> A): Arb<A> = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecases
   override fun edgecases(rs: RandomSource): List<A> = edgecases

   override fun sample(rs: RandomSource): Sample<A> = sampleOf(fn(rs), shrinker)
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { sampleOf(fn(rs), shrinker) }
}

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], uses the given edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(
   edgecasesGenerator: (RandomSource) -> List<A>,
   shrinker: Shrinker<A>,
   fn: (RandomSource) -> A
): Arb<A> = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecasesGenerator(RandomSource.Default)
   override fun edgecases(rs: RandomSource): List<A> = edgecasesGenerator(rs)
   override fun sample(rs: RandomSource): Sample<A> = sampleOf(fn(rs), shrinker)
   override fun values(rs: RandomSource): Sequence<Sample<A>> = generateSequence { sampleOf(fn(rs), shrinker) }
}

/**
 * Creates a new [Arb] that performs shrinking using the supplied [Shrinker], has no edge cases and
 * generates values from the given function.
 */
fun <A> arbitrary(shrinker: Shrinker<A>, fn: (RandomSource) -> A): Arb<A> =
   arbitrary(emptyList(), shrinker, fn)

/**
 * Creates a new [Arb] that performs shrinking using the supplied shrinker and generates each value
 * from successive invocations of the given function f.
 */
@Deprecated(
   "use arbitrary(). This function will be removed in 4.5",
   ReplaceWith("arbitrary(fn)")
)
fun <A> arb(fn: (RandomSource) -> A): Arb<A> =
   arbitrary(fn)

/**
 * Creates a new [Arb] that performs no shrinking, uses the supplied edge case values,
 * and generates values from the given function that is invoked once to return a sequence of values.
 */
@Deprecated(
   "This function will no longer accept Sequence<A>. Use arbitrary with (RandomSource -> A) for compatibility. This function will be removed in 4.5."
)
@JvmName("arbSequence")
fun <A> arb(edgecases: List<A> = emptyList(), fn: (RandomSource) -> Sequence<A>) = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecases
   override fun edgecases(rs: RandomSource): List<A> = edgecases
   override fun values(rs: RandomSource): Sequence<Sample<A>> = fn(rs).map { Sample(it) }
   override fun sample(rs: RandomSource): Sample<A> = Sample(fn(rs).first())
}

/**
 * Creates a new [Arb] that performs shrinking using the supplier shrinker, uses the
 * supplied edge case values, and provides values from sequence returning function.
 */
@Deprecated(
   "This function will no longer accept Sequence<A>. Use arbitrary with (RandomSource -> A) for compatibility. This function will be removed in 4.5."
)
@JvmName("arbSequence")
fun <A> arb(
   edgecases: List<A> = emptyList(),
   shrinker: Shrinker<A>,
   fn: (RandomSource) -> Sequence<A>
) = object : Arb<A>() {
   override fun edgecases(): List<A> = edgecases
   override fun edgecases(rs: RandomSource): List<A> = edgecases

   override fun values(rs: RandomSource): Sequence<Sample<A>> = fn(rs).map { sampleOf(it, shrinker) }
   override fun sample(rs: RandomSource): Sample<A> = sampleOf(fn(rs).first(), shrinker)
}

/**
 * Creates a new [Arb] that performs shrinking using the supplied shrinker and generates each value
 * from successive invocations of the given function f.
 */
@Deprecated(
   "Use arbitrary with (RandomSource -> A). This function will be removed in 4.5",
   ReplaceWith("arbitrary(shrinker, fn)")
)
fun <A> arb(shrinker: Shrinker<A>, fn: (RandomSource) -> A): Arb<A> =
   arbitrary(shrinker, fn)

/**
 * Creates a new [Arb] with the given edgecases, that performs shrinking using the supplied shrinker and
 * generates each value from successive invocations of the given function f.
 */
@Deprecated(
   "Use arbitrary with (RandomSource -> A). This function will be removed in 4.5",
   ReplaceWith("arbitrary(edgecases, shrinker, fn)")
)
fun <A> arb(shrinker: Shrinker<A>, edgecases: List<A> = emptyList(), fn: (RandomSource) -> A): Arb<A> =
   arbitrary(edgecases, shrinker, fn)

/**
 * Creates a new [Arb] with the given edgecases, that performs shrinking using the supplied shrinker and
 * generates each value from successive invocations of the given function f.
 */
@Deprecated(
   "Use arbitrary with (RandomSource -> A). This function will be removed in 4.5",
   ReplaceWith("arbitrary(edgecases, fn)")
)
fun <A> arb(edgecases: List<A> = emptyList(), fn: (RandomSource) -> A): Arb<A> =
   arbitrary(edgecases, fn)

/**
 * Returns an [Arb] where each value is generated from the given function.
 */
@Deprecated(
   "Use arbitrary with (RandomSource -> A). This function will be removed in 4.5",
   ReplaceWith("arbitrary(edgeCases, fn)")
)
fun <A> Arb.Companion.create(edgeCases: List<A> = emptyList(), fn: (RandomSource) -> A): Arb<A> =
   arbitrary(edgeCases, fn)

/**
 * Returns an [Arb] which repeatedly generates a single value.
 */
fun <A> Arb.Companion.constant(a: A) = element(a)
