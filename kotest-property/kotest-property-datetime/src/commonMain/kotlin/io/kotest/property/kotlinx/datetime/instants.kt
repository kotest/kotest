package io.kotest.property.kotlinx.datetime

import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Returns an [Arb] where each value is a randomly generated Instant in the given range.
 *
 * The default range is the unix epoch to 'now'.
 */
fun Arb.Companion.instant(range: LongRange = 0L..Clock.System.now().toEpochMilliseconds()) =
   Arb.long(range).map { Instant.fromEpochMilliseconds(it) }
