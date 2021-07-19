package io.kotest.property.arbitrary

import io.kotest.property.Arb

actual inline fun <reified A : Any> Arb.Companion.default(): Arb<A> =
   defaultForClass(A::class)
      ?: throw NoGeneratorFoundException("Cannot locate generator for ${A::class}; specify generators explicitly")
