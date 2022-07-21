package io.kotest.property.arbitrary

import io.kotest.property.Arb
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * Arberates a stream of random [ZoneOffset]
 */
fun Arb.Companion.zoneOffset(): Arb<ZoneOffset> =
   int(ZoneOffset.MIN.totalSeconds..ZoneOffset.MAX.totalSeconds).map(ZoneOffset::ofTotalSeconds)

/**
 * Arberates a stream of random region based [ZoneId] values
 */
fun Arb.Companion.zoneRegion(): Arb<ZoneId> = Arb.of(ZoneId.getAvailableZoneIds()).map(ZoneId::of)

/**
 * Arberates a stream of random region and offset based [ZoneId] values
 */
fun Arb.Companion.zoneId(): Arb<ZoneId> = Arb.choice(zoneOffset(), zoneRegion())
