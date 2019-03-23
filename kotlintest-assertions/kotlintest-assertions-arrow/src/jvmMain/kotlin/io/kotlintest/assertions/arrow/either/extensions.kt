package io.kotlintest.assertions.arrow.either

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.fix
import io.kotlintest.properties.Gen
import io.kotlintest.assertions.arrow.choose

/**
 * [Gen] extension instance for [Either].
 *
 * Generates random [Right] and [Left] values based on the constants
 * provided by the [GA] and [GB] generators.
 *
 * ```kotlin
 * import io.kotlintest.assertions.arrow.either.either
 * import io.kotlintest.properties.forAll
 * import io.kotlintest.properties.Gen
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

    override fun random(): Sequence<Either<A, B>> =
      Either.applicativeError<A>().run {
        generateSequence {
          choose({ GA.random().iterator().next() }, { GB.random().iterator().next() }).fix()
        }
      }
  }