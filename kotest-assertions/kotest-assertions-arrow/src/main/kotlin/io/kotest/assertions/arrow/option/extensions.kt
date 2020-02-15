package io.kotest.assertions.arrow.option

import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicativeError.applicativeError
import arrow.core.fix
import io.kotest.assertions.arrow.choose
import io.kotest.properties.Gen

/**
 * [Gen] extension instance for [Option].
 *
 * Generates random [Option] of [A] as provided by the [GA] generators.
 *
 * ```kotlin
 * import io.kotest.assertions.arrow.option.option
 * import io.kotest.properties.forAll
 * import io.kotest.properties.Gen
 *
 * forAll(Gen.option(Gen.constant(1))) {
 *   it.fold({ true }, { n -> n == 1 })
 * }
 * ```
 */
fun <A> Gen.Companion.option(GA: Gen<A>): Gen<Option<A>> =
   object : Gen<Option<A>> {
      override fun constants(): Iterable<Option<A>> =
         GA.constants().map(::Some)

      override fun random(seed: Long?): Sequence<Option<A>> =
         Option.applicativeError().run {
            generateSequence {
               choose({ Unit }) { GA.random(seed).iterator().next() }.fix()
            }
         }
   }
