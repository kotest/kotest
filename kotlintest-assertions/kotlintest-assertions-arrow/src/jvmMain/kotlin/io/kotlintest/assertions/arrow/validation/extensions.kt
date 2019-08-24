package io.kotlintest.assertions.arrow.validation

import arrow.data.Invalid
import arrow.data.Valid
import arrow.data.Validated
import arrow.data.extensions.validated.applicativeError.applicativeError
import arrow.data.fix
import arrow.typeclasses.Semigroup
import io.kotlintest.assertions.arrow.choose
import io.kotlintest.properties.Gen

/**
 * [Gen] extension instance for [Validated].
 *
 * Generates random [Valid] and [Invalid] values based on the constants generation
 * provided by the [GA] and [GB] generators.
 *
 * ```kotlin
 * import arrow.instances.semigroup
 * import io.kotlintest.assertions.arrow.validation.validation
 * import io.kotlintest.properties.forAll
 * import io.kotlintest.properties.Gen
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

    override fun random(): Sequence<Validated<A, B>> =
      Validated.applicativeError(SA).run {
        generateSequence {
          choose({ GA.random().iterator().next() }, { GB.random().iterator().next() }).fix()
        }
      }
  }