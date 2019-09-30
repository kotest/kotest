package io.kotest.assertions.arrow.`try`

import arrow.core.Success
import arrow.core.Try
import arrow.core.extensions.`try`.applicativeError.applicativeError
import arrow.core.fix
import io.kotest.assertions.arrow.choose
import io.kotest.properties.Gen


/**
 * [Gen] extension instance for [Try].
 *
 * Generates random [Try] of [A] as provided by the [GA] generators and [GT] generators
 *
 * ```kotlin
 * import io.kotest.assertions.arrow.`try`.`try`
 * import io.kotest.properties.forAll
 * import io.kotest.properties.Gen
 *
 * val BOOM = RuntimeException("BOOM!")
 *
 * forAll(Gen.`try`(Gen.constant(BOOM), Gen.constant(1))) {
 *   it.fold({ ex -> ex == BOOM }, { n -> n == 1 })
 * }
 * ```
 */
fun <A, B : Throwable> Gen.Companion.`try`(GT: Gen<B>, GA: Gen<A>): Gen<Try<A>> =
   object : Gen<Try<A>> {
      override fun constants(): Iterable<Try<A>> =
         GA.constants().map(::Success)

      override fun random(seed: Long?): Sequence<Try<A>> =
         Try.applicativeError().run {
            generateSequence {
               choose({ GT.random(seed).iterator().next() }, { GA.random(seed).iterator().next() }).fix()
            }
         }
   }
