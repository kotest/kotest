package io.kotest.assertions.arrow.either

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.fix
import io.kotest.assertions.arrow.choose
import io.kotest.properties.Gen

/**
 * [Gen] extension instance for [Either].
 *
 * Generates random [Right] and [Left] values based on the constants
 * provided by the [GA] and [GB] generators.
 *
 * ```kotlin
 * import io.kotest.assertions.arrow.either.either
 * import io.kotest.properties.forAll
 * import io.kotest.properties.Gen
 *
 * forAll(Gen.either(Gen.constant(1), Gen.constant(0))) {
 *   it.fold({ l -> l == 1 }, { r -> r == 0 })
 * }
 * ```
 */
fun <A, B> Gen.Companion.either(GA: Gen<A>, GB: Gen<B>): Gen<Either<A, B>> =
   object : Gen<Either<A, B>> {
      override fun constants(): Iterable<Either<A, B>> =
         GA.constants().map(::Left) + GB.constants().map(::Right)

      override fun random(seed: Long?): Sequence<Either<A, B>> =
         Either.applicativeError<A>().run {
            generateSequence {
               choose({ GA.random(seed).iterator().next() }, { GB.random(seed).iterator().next() }).fix()
            }
         }
   }
