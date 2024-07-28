package io.kotest.property.arbitrary

import io.kotest.property.Arb
import kotlin.math.PI
import kotlin.math.acos

data class GeoLocation(val latitude: Double, val longitude: Double) {
   val latitudeDeg: Double = 180.0 * latitude / PI
   val longitudeDeg: Double = 180.0 * longitude / PI
}

fun Arb.Companion.geoLocation(): Arb<GeoLocation> = arbitrary(
   listOf(
      GeoLocation(-PI / 2, 0.0), // South Pole
      GeoLocation(PI / 2, 0.0) // North Pole
   )
) {
   val random = it.random
   val lat = acos(random.nextDouble(-1.0, 1.0)) - PI / 2
   val lon = random.nextDouble(-PI, PI)
   GeoLocation(latitude = lat, longitude = lon)
}
