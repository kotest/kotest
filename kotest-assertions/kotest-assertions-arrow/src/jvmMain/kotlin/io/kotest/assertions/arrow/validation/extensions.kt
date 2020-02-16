package io.kotest.assertions.arrow.validation


import arrow.core.Validated
import arrow.core.Validated.Invalid
import arrow.core.Validated.Valid
import arrow.core.extensions.validated.applicativeError.applicativeError
import arrow.core.fix
import arrow.typeclasses.Semigroup
import io.kotest.assertions.arrow.choose
import io.kotest.properties.Gen

/**
 * [Gen] extension instance for [Validated].
 *
 * Generates random [Valid] and [Invalid] values based on the constants generation
 * provided by the [GA] and [GB] generators.
 *
 * ```kotlin
 * import arrow.instances.semigroup
 * import io.kotest.assertions.arrow.validation.validation
 * import io.kotest.properties.forAll
 * import io.kotest.properties.Gen
 *
 * forAll(Gen.validated(Gen.constant(1), Gen.constant(0), Int.semigroup())) {
 *   it.fold({ l -> l == 1 }, { r -> r == 0 })
 * }
 * ```
 */
fun <A, B> Gen.Companion.validated(GA: Gen<A>, GB: Gen<B>, SA: Semigroup<A>): Gen<Validated<A, B>> =
   object : Gen<Validated<A, B>> {
      override fun constants(): Iterable<Validated<A, B>> =
         GA.constants().map(::Invalid) + GB.constants().map(::Valid)

      override fun random(seed: Long?): Sequence<Validated<A, B>> =
         Validated.applicativeError(SA).run {
            generateSequence {
               choose({ GA.random(seed).iterator().next() }, { GB.random(seed).iterator().next() }).fix()
            }
         }
   }
