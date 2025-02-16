package io.kotest.property.arrow.core

import arrow.core.Option
import arrow.core.toOption
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull

public fun <B> Arb.Companion.option(arb: Arb<B>): Arb<Option<B>> =
  arb.orNull().map { it.toOption() }
