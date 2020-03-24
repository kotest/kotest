package io.kotest.property.arbitrary

import io.kotest.property.Arb

fun Arb.Companion.latlong(): Arb<Pair<Double, Double>> = Arb.create {
   val lat = it.random.nextDouble(-180.0, 180.0)
   val long = it.random.nextDouble(-180.0, 180.0)
   Pair(lat, long)
}
