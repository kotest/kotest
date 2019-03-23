package io.kotlintest.assertions.arrow.option

import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicativeError.applicativeError
import arrow.core.fix
import io.kotlintest.assertions.arrow.choose
import io.kotlintest.properties.Gen

/**
 * [Gen] extension instance for [Option].
 *
 * Generates random [Option] of [A] as provided by the [GA] generators.
 *
 * ```kotlin
 * import io.kotlintest.assertions.arrow.option.option
 * import io.kotlintest.properties.forAll
 * import io.kotlintest.properties.Gen
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

    override fun random(): Sequence<Option<A>> =
      Option.applicativeError().run {
        generateSequence {
          choose({ Unit }) { GA.random().iterator().next() }.fix()
        }
      }
  }